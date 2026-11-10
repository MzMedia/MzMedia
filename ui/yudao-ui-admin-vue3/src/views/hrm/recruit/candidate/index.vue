<template>
  <doc-alert title="【招聘】招聘管理" url="https://doc.iocoder.cn/hrm/recruit/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="候选人" prop="search">
        <el-input
          v-model="queryParams.search"
          class="!w-240px"
          clearable
          placeholder="请输入姓名、手机号或邮箱"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="应聘职位" prop="postId">
        <RecruitPostSelect
          v-model="queryParams.postId"
          class="!w-240px"
          placeholder="请选择应聘职位"
        />
      </el-form-item>
      <el-form-item label="招聘负责人" prop="ownerEmployeeId">
        <HrmEmployeeSelect
          v-model="queryParams.ownerEmployeeId"
          class="!w-240px"
          :entry-status="HrmEmployeeEntryStatus.ACTIVE"
          placeholder="请选择招聘负责人"
          title="选择招聘负责人"
        />
      </el-form-item>
      <el-form-item label="招聘渠道" prop="channelId">
        <RecruitChannelSelect
          v-model="queryParams.channelId"
          class="!w-240px"
          placeholder="请选择招聘渠道"
        />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="性别" prop="sex">
        <el-select v-model="queryParams.sex" class="!w-240px" clearable placeholder="请选择性别">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="年龄" prop="minAge">
        <div class="flex w-240px items-center gap-8px">
          <el-input-number
            v-model="queryParams.minAge"
            :controls="false"
            :max="99"
            :min="0"
            class="!w-0 flex-1"
            placeholder="最小年龄"
          />
          <span class="shrink-0 text-[var(--el-text-color-secondary)]">至</span>
          <el-input-number
            v-model="queryParams.maxAge"
            :controls="false"
            :max="99"
            :min="0"
            class="!w-0 flex-1"
            placeholder="最大年龄"
          />
        </div>
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="工作年限" prop="minWorkTime">
        <div class="flex w-240px items-center gap-8px">
          <el-input-number
            v-model="queryParams.minWorkTime"
            :controls="false"
            :min="0"
            class="!w-0 flex-1"
            placeholder="最小年限"
          />
          <span class="shrink-0 text-[var(--el-text-color-secondary)]">至</span>
          <el-input-number
            v-model="queryParams.maxWorkTime"
            :controls="false"
            :min="0"
            class="!w-0 flex-1"
            placeholder="最大年限"
          />
        </div>
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="学历" prop="education">
        <el-select
          v-model="queryParams.education"
          class="!w-240px"
          clearable
          placeholder="请选择学历"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_CANDIDATE_EDUCATION)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="毕业院校" prop="graduateSchool">
        <el-input
          v-model="queryParams.graduateSchool"
          class="!w-240px"
          clearable
          placeholder="请输入毕业院校"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="最近单位" prop="latestWorkPlace">
        <el-input
          v-model="queryParams.latestWorkPlace"
          class="!w-240px"
          clearable
          placeholder="请输入最近工作单位"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="面试官" prop="interviewEmployeeId">
        <HrmEmployeeSelect
          v-model="queryParams.interviewEmployeeId"
          class="!w-240px"
          :entry-status="HrmEmployeeEntryStatus.ACTIVE"
          placeholder="请选择面试官"
          title="选择面试官"
        />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="面试时间" prop="interviewTime">
        <el-date-picker
          v-model="queryParams.interviewTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-360px"
          end-placeholder="结束时间"
          range-separator="-"
          start-placeholder="开始时间"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="创建人" prop="creator">
        <UserSelect v-model="queryParams.creator" class="!w-240px" placeholder="请选择创建人" />
      </el-form-item>
      <el-form-item v-show="showMoreQuery" label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-360px"
          end-placeholder="结束时间"
          range-separator="-"
          start-placeholder="开始时间"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button link type="primary" @click="showMoreQuery = !showMoreQuery">
          <Icon class="mr-5px" :icon="showMoreQuery ? 'ep:arrow-up' : 'ep:arrow-down'" />
          {{ showMoreQuery ? '收起' : '展开' }}
        </el-button>
        <el-button
          v-hasPermi="['hrm:recruit:candidate:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
        <el-dropdown
          v-if="hasBatchPermission"
          :disabled="!hasBatchOperations || !selectedIds.length"
          class="ml-12px"
          @command="handleBatchCommand"
        >
          <el-button :disabled="!hasBatchOperations || !selectedIds.length" plain type="primary">
            <Icon class="mr-5px" icon="ep:operation" />批量操作
            <Icon class="ml-5px" icon="ep:arrow-down" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-if="canBatchUpdateStatus && checkPermi(['hrm:recruit:candidate:update'])"
                command="status"
              >
                批量流转
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canBatchInterview && checkPermi(['hrm:recruit:interview:create'])"
                command="interview"
              >
                批量面试
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canBatchUpdatePostOrChannel && checkPermi(['hrm:recruit:candidate:update'])"
                command="post"
              >
                修改职位
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canBatchUpdatePostOrChannel && checkPermi(['hrm:recruit:candidate:update'])"
                command="channel"
              >
                修改渠道
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canBatchEliminate && checkPermi(['hrm:recruit:candidate:update'])"
                command="eliminate"
              >
                批量淘汰
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canBatchDelete && checkPermi(['hrm:recruit:candidate:delete'])"
                command="delete"
                :divided="hasBatchNonDeleteOperations"
              >
                批量删除
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button
          v-hasPermi="['hrm:recruit:candidate:delete']"
          class="ml-12px"
          plain
          type="warning"
          @click="openCleanForm"
        >
          <Icon class="mr-5px" icon="ep:brush" />一键清理
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeStatus" @tab-click="handleStatusTabClick">
      <el-tab-pane name="all">
        <template #label>
          全部<span class="text-[var(--el-text-color-secondary)]">（{{ allStatusCount }}）</span>
        </template>
      </el-tab-pane>
      <el-tab-pane v-for="item in statusTabOptions" :key="item.value" :name="item.value">
        <template #label>
          <span class="inline-flex">
            <span>{{ item.label }}</span>
            <span class="text-[var(--el-text-color-secondary)]">（{{ item.count }}）</span>
          </span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <el-table
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        :selectable="isCandidateSelectable"
        align="center"
        fixed="left"
        type="selection"
        width="50"
      />
      <el-table-column align="center" fixed="left" label="姓名" min-width="110" prop="name">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="应聘职位" min-width="160" prop="postName" />
      <el-table-column align="center" label="用人部门" min-width="130" prop="deptName" />
      <el-table-column align="center" label="候选人状态" prop="status" width="190">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS" :value="scope.row.status" />
          <span
            v-if="
              scope.row.status === HrmRecruitCandidateStatus.INTERVIEW &&
              scope.row.interviewResult &&
              scope.row.interviewResult !== HrmRecruitInterviewResult.UNFINISHED
            "
          >
            （面试{{
              getDictLabel(DICT_TYPE.HRM_RECRUIT_INTERVIEW_RESULT, scope.row.interviewResult)
            }}）
          </span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="手机号码" prop="mobile" width="130" />
      <el-table-column align="center" label="性别" prop="sex" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="scope.row.sex" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="年龄" prop="age" width="80" />
      <el-table-column align="center" label="邮箱" min-width="180" prop="email" />
      <el-table-column align="center" label="招聘负责人" min-width="130" prop="ownerEmployeeName" />
      <el-table-column align="center" label="工作年限" prop="workTime" width="100">
        <template #default="scope">
          {{ scope.row.workTime != null ? `${scope.row.workTime} 年` : '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="学历" prop="education" width="90">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.HRM_RECRUIT_CANDIDATE_EDUCATION"
            :value="scope.row.education"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="毕业院校" min-width="140" prop="graduateSchool" />
      <el-table-column align="center" label="最近工作单位" min-width="150" prop="latestWorkPlace" />
      <el-table-column align="center" label="招聘渠道" min-width="120" prop="channelName" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="面试时间"
        prop="interviewTime"
        width="180"
      />
      <el-table-column align="center" label="面试轮次" prop="stageNumber" width="100" />
      <el-table-column
        align="center"
        label="主面试官"
        min-width="120"
        prop="interviewEmployeeName"
      />
      <el-table-column align="center" label="面试方式" prop="interviewType" width="110">
        <template #default="scope">
          <dict-tag
            v-if="scope.row.interviewType != null"
            :type="DICT_TYPE.HRM_RECRUIT_INTERVIEW_TYPE"
            :value="scope.row.interviewType"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="其他面试官" min-width="150">
        <template #default="scope">
          {{ scope.row.otherInterviewEmployeeNames?.join('、') || '-' }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['hrm:recruit:candidate:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <template
            v-for="actions in [getCandidateActions(scope.row)]"
            :key="actions.primary?.command || actions.more.length"
          >
            <el-button
              v-if="actions.primary"
              link
              type="primary"
              @click="handlePrimaryAction(actions.primary.command, scope.row)"
            >
              {{ actions.primary.label }}
            </el-button>
            <el-dropdown
              v-if="actions.more.length"
              class="!align-middle ml-12px"
              @command="(command) => handleMoreCommand(command, scope.row)"
            >
              <el-button link type="primary">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="(action, index) in actions.more"
                    :key="action.command"
                    :command="action.command"
                    :divided="index > 0 && action.command === 'delete'"
                  >
                    {{ action.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <RecruitCandidateForm ref="formRef" @success="refreshList" />
  <!-- 表单弹窗：批量流转 -->
  <RecruitCandidateStatusListForm ref="statusListFormRef" @success="handleBatchSuccess" />
  <!-- 表单弹窗：批量修改应聘职位 -->
  <RecruitCandidatePostListForm ref="postListFormRef" @success="handleBatchSuccess" />
  <!-- 表单弹窗：批量修改招聘渠道 -->
  <RecruitCandidateChannelListForm ref="channelListFormRef" @success="handleBatchSuccess" />
  <!-- 表单弹窗：淘汰候选人 -->
  <RecruitCandidateEliminateForm ref="eliminateFormRef" @success="handleBatchSuccess" />
  <!-- 表单弹窗：候选人转员工、确认员工入职 -->
  <EmployeeForm ref="employeeFormRef" @success="refreshList" />
  <!-- 表单弹窗：清理候选人 -->
  <RecruitCandidateCleanForm ref="cleanFormRef" @success="refreshList" />
  <!-- 表单弹窗：安排面试 -->
  <RecruitInterviewForm ref="interviewFormRef" @success="handleBatchSuccess" />
  <!-- 表单弹窗：登记面试结果 -->
  <RecruitInterviewResultForm ref="interviewResultFormRef" @success="refreshList" />
</template>

<script lang="ts" setup>
import type { FormInstance, TabsPaneContext } from 'element-plus'
import { DICT_TYPE, getDictLabel, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { checkPermi } from '@/utils/permission'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import * as RecruitInterviewApi from '@/api/hrm/recruit/interview'
import UserSelect from '@/views/system/user/components/UserSelect.vue'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import EmployeeForm from '@/views/hrm/employee/EmployeeForm.vue'
import RecruitChannelSelect from '@/views/hrm/recruit/channel/components/RecruitChannelSelect.vue'
import RecruitPostSelect from '@/views/hrm/recruit/post/components/RecruitPostSelect.vue'
import RecruitCandidateForm from './RecruitCandidateForm.vue'
import RecruitCandidateChannelListForm from './RecruitCandidateChannelListForm.vue'
import RecruitCandidateCleanForm from './RecruitCandidateCleanForm.vue'
import RecruitCandidateEliminateForm from './RecruitCandidateEliminateForm.vue'
import RecruitCandidatePostListForm from './RecruitCandidatePostListForm.vue'
import RecruitCandidateStatusListForm from './RecruitCandidateStatusListForm.vue'
import RecruitInterviewForm from './RecruitInterviewForm.vue'
import RecruitInterviewResultForm from './RecruitInterviewResultForm.vue'
import { useBatchOperation } from '@/views/hrm/utils/batch'
import {
  HRM_RECRUIT_CANDIDATE_EMPLOYEE_EDUCATION_MAP,
  HrmEmployeeEntryStatus,
  HrmEmployeeStatus,
  HrmEmployeeType,
  HrmRecruitCandidateStatus,
  HrmRecruitInterviewResult,
  type HrmRecruitCandidateStatusValue
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmRecruitCandidate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { executeBatch } = useBatchOperation() // 批量操作执行方法
const route = useRoute() // 当前路由

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总条数
const list = ref<RecruitCandidateApi.HrmRecruitCandidateVO[]>([]) // 列表的数据
const statusCounts = ref<RecruitCandidateApi.HrmRecruitCandidateStatusCountVO[]>([]) // 候选人状态统计
const activeStatus = ref('all') // 当前选中的候选人状态
const showMoreQuery = ref(false) // 是否展示更多搜索条件
const selectedIds = ref<number[]>([]) // 选中的候选人编号数组
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  search: '',
  postId: undefined as number | undefined,
  ownerEmployeeId: undefined as number | undefined,
  sex: undefined as number | undefined,
  minAge: undefined as number | undefined,
  maxAge: undefined as number | undefined,
  minWorkTime: undefined as number | undefined,
  maxWorkTime: undefined as number | undefined,
  education: undefined as number | undefined,
  graduateSchool: '',
  latestWorkPlace: '',
  channelId: undefined as number | undefined,
  interviewEmployeeId: undefined as number | undefined,
  interviewTime: [] as string[],
  creator: undefined as number | undefined,
  status: undefined as HrmRecruitCandidateStatusValue | undefined,
  createTime: [] as string[]
})
const queryFormRef = ref<FormInstance>() // 搜索的表单

/** 候选人状态页签 */
const statusTabOptions = computed(() => {
  const countMap = Object.fromEntries(statusCounts.value.map((item) => [item.status, item.count]))
  return getIntDictOptions(DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS).map((item) => ({
    label: item.label,
    value: String(item.value),
    count: countMap[Number(item.value)] ?? 0
  }))
})

/** 全部候选人数量 */
const allStatusCount = computed(() =>
  statusCounts.value.reduce((totalCount, item) => totalCount + item.count, 0)
)

/** 当前选中的候选人状态值 */
const activeStatusValue = computed<HrmRecruitCandidateStatusValue | undefined>(() =>
  activeStatus.value === 'all'
    ? undefined
    : (Number(activeStatus.value) as HrmRecruitCandidateStatusValue)
)

/** 判断当前是否为指定候选人状态 */
function isActiveStatusIn(statuses: number[]) {
  return activeStatusValue.value !== undefined && statuses.includes(activeStatusValue.value)
}

/** 是否允许批量流转状态 */
const canBatchUpdateStatus = computed(() =>
  isActiveStatusIn([
    HrmRecruitCandidateStatus.NEW,
    HrmRecruitCandidateStatus.PRIMARY_PASS,
    HrmRecruitCandidateStatus.INTERVIEW_PASS
  ])
)

/** 是否允许批量安排面试 */
const canBatchInterview = computed(() =>
  isActiveStatusIn([
    HrmRecruitCandidateStatus.NEW,
    HrmRecruitCandidateStatus.PRIMARY_PASS,
    HrmRecruitCandidateStatus.INTERVIEW_PASS
  ])
)

const canBatchUpdatePostOrChannel = computed(() => canBatchUpdateStatus.value) // 是否可批量修改职位或渠道

/** 是否允许批量淘汰 */
const canBatchEliminate = computed(() =>
  isActiveStatusIn([
    HrmRecruitCandidateStatus.NEW,
    HrmRecruitCandidateStatus.PRIMARY_PASS,
    HrmRecruitCandidateStatus.INTERVIEW,
    HrmRecruitCandidateStatus.INTERVIEW_PASS,
    HrmRecruitCandidateStatus.OFFER_SENT,
    HrmRecruitCandidateStatus.PENDING_ENTRY
  ])
)

/** 是否允许批量删除 */
const candidateDeleteStatuses: number[] = [
  HrmRecruitCandidateStatus.NEW,
  HrmRecruitCandidateStatus.PRIMARY_PASS,
  HrmRecruitCandidateStatus.INTERVIEW,
  HrmRecruitCandidateStatus.INTERVIEW_PASS,
  HrmRecruitCandidateStatus.ELIMINATED
]
const canBatchDelete = computed(() => isActiveStatusIn(candidateDeleteStatuses)) // 是否可批量删除

/** 是否拥有任一批量操作权限 */
const hasBatchPermission = computed(
  () =>
    checkPermi(['hrm:recruit:candidate:update']) ||
    checkPermi(['hrm:recruit:interview:create']) ||
    checkPermi(['hrm:recruit:candidate:delete'])
)

/** 当前状态是否存在可用的非删除批量操作 */
const hasBatchNonDeleteOperations = computed(
  () =>
    (checkPermi(['hrm:recruit:candidate:update']) &&
      (canBatchUpdateStatus.value ||
        canBatchUpdatePostOrChannel.value ||
        canBatchEliminate.value)) ||
    (checkPermi(['hrm:recruit:interview:create']) && canBatchInterview.value)
)

/** 当前状态是否存在可用的批量操作 */
const hasBatchOperations = computed(
  () =>
    hasBatchNonDeleteOperations.value ||
    (checkPermi(['hrm:recruit:candidate:delete']) && canBatchDelete.value)
)

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await RecruitCandidateApi.getRecruitCandidatePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 查询候选人状态统计 */
async function getStatusCounts() {
  statusCounts.value = await RecruitCandidateApi.getRecruitCandidateStatusCount(queryParams)
}

/** 刷新列表和状态统计 */
async function refreshList() {
  await Promise.all([getList(), getStatusCounts()])
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNo = 1
  selectedIds.value = []
  refreshList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  queryParams.maxAge = undefined
  queryParams.maxWorkTime = undefined
  activeStatus.value = 'all'
  queryParams.status = undefined
  handleQuery()
}

/** tab 切换 */
function handleStatusTabClick(tab: TabsPaneContext) {
  if (tab.paneName === undefined) {
    return
  }
  queryParams.status =
    tab.paneName === 'all' ? undefined : (Number(tab.paneName) as HrmRecruitCandidateStatusValue)
  handleQuery()
}

/** 是否允许选择候选人 */
function isCandidateSelectable() {
  return activeStatus.value !== 'all'
}

/** 表格选中项变化 */
function handleSelectionChange(rows: RecruitCandidateApi.HrmRecruitCandidateVO[]) {
  selectedIds.value = rows.map((row) => row.id!)
}

const formRef = ref<InstanceType<typeof RecruitCandidateForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

const { push } = useRouter() // 路由操作

/** 打开详情 */
function openDetail(id: number) {
  push({ name: 'HrmRecruitCandidateDetail', params: { id } })
}

const interviewFormRef = ref<InstanceType<typeof RecruitInterviewForm>>() // 面试表单 Ref

/** 安排面试 */
function openInterview(candidateId: number) {
  interviewFormRef.value?.open('create', candidateId)
}

/** 安排复试 */
function openReinterview(candidateId: number) {
  interviewFormRef.value?.open('create', candidateId, undefined, '安排复试')
}

/** 批量安排面试 */
function openBatchInterview() {
  if (!selectedIds.value.length) {
    return
  }
  interviewFormRef.value?.open('batch', selectedIds.value)
}

const interviewResultFormRef = ref<InstanceType<typeof RecruitInterviewResultForm>>() // 面试结果表单 Ref

/** 登记面试结果 */
async function openInterviewResult(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.interviewId) {
    message.warning('请先安排面试')
    return
  }
  const interview = await RecruitInterviewApi.getRecruitInterview(candidate.interviewId)
  interviewResultFormRef.value?.open(interview)
}

/** 更改面试安排 */
async function openInterviewChange(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.id || !candidate.interviewId) {
    return
  }
  const interview = await RecruitInterviewApi.getRecruitInterview(candidate.interviewId)
  interviewFormRef.value?.open('update', candidate.id, interview)
}

/** 取消面试 */
async function openInterviewCancel(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.interviewId) {
    return
  }
  const interview = await RecruitInterviewApi.getRecruitInterview(candidate.interviewId)
  interviewResultFormRef.value?.open(interview, HrmRecruitInterviewResult.CANCELED)
}

const statusListFormRef = ref<InstanceType<typeof RecruitCandidateStatusListForm>>() // 批量修改状态表单 Ref

/** 批量修改操作 */
function openBatchStatusForm() {
  if (!activeStatusValue.value) {
    return
  }
  statusListFormRef.value?.open(selectedIds.value, activeStatusValue.value)
}

const postListFormRef = ref<InstanceType<typeof RecruitCandidatePostListForm>>() // 批量修改职位表单 Ref

/** 批量修改应聘职位 */
function openBatchPostForm() {
  postListFormRef.value?.open(selectedIds.value)
}

const channelListFormRef = ref<InstanceType<typeof RecruitCandidateChannelListForm>>() // 批量修改渠道表单 Ref

/** 批量修改招聘渠道 */
function openBatchChannelForm() {
  channelListFormRef.value?.open(selectedIds.value)
}

const eliminateFormRef = ref<InstanceType<typeof RecruitCandidateEliminateForm>>() // 淘汰表单 Ref

/** 淘汰操作 */
function openEliminateForm(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.id) {
    return
  }
  eliminateFormRef.value?.open(candidate.id, candidate.name)
}

/** 批量淘汰候选人 */
function openBatchEliminateForm() {
  eliminateFormRef.value?.open(selectedIds.value)
}

/** 候选人转员工 */
function openEntryForm(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.id) {
    return
  }
  const entryTime = candidate.entryTime ? Number(candidate.entryTime) : Date.now()
  employeeFormRef.value?.open('candidate', undefined, {
    candidateId: candidate.id,
    name: candidate.name,
    mobile: candidate.mobile,
    sex: candidate.sex,
    age: candidate.age,
    email: candidate.email,
    highestEducation:
      candidate.education == null
        ? undefined
        : HRM_RECRUIT_CANDIDATE_EMPLOYEE_EDUCATION_MAP[candidate.education],
    deptId: candidate.deptId,
    postName: candidate.postName,
    channelId: candidate.channelId,
    entryStatus: HrmEmployeeEntryStatus.PENDING_ENTRY,
    status: HrmEmployeeStatus.PROBATION,
    type: HrmEmployeeType.FORMAL,
    entryTime,
    companyAgeStartTime: entryTime,
    probation: 3,
    remark: candidate.remark
  })
}
const employeeFormRef = ref<InstanceType<typeof EmployeeForm>>() // 员工表单 Ref
const cleanFormRef = ref<InstanceType<typeof RecruitCandidateCleanForm>>() // 清理候选人表单 Ref

/** 一键清理 */
function openCleanForm() {
  cleanFormRef.value?.open()
}

/** 获得候选人的主操作 */
function getPrimaryAction(
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
): { command: string; label: string } | undefined {
  // 重新安排已取消的当前轮次面试
  if (
    checkPermi(['hrm:recruit:interview:update']) &&
    candidate.status === HrmRecruitCandidateStatus.INTERVIEW &&
    candidate.interviewId &&
    candidate.interviewResult === HrmRecruitInterviewResult.CANCELED
  ) {
    return { command: 'interview-change', label: '重新安排' }
  }
  // 登记结果
  if (
    checkPermi(['hrm:recruit:interview:update']) &&
    candidate.status === HrmRecruitCandidateStatus.INTERVIEW &&
    candidate.interviewId &&
    candidate.interviewResult !== HrmRecruitInterviewResult.CANCELED
  ) {
    return { command: 'interview-result', label: '登记结果' }
  }
  // 确认入职
  if (
    checkPermi(['hrm:employee:update']) &&
    candidate.status === HrmRecruitCandidateStatus.PENDING_ENTRY &&
    candidate.employeeId
  ) {
    return { command: 'confirm-entry', label: '确认入职' }
  }
  // 转为员工
  if (
    checkPermi(['hrm:recruit:candidate:update']) &&
    (candidate.status === HrmRecruitCandidateStatus.INTERVIEW_PASS ||
      candidate.status === HrmRecruitCandidateStatus.OFFER_SENT) &&
    !candidate.employeeId
  ) {
    return { command: 'convert-employee', label: '转为员工' }
  }
  // 安排面试
  if (
    checkPermi(['hrm:recruit:interview:create']) &&
    (candidate.status === HrmRecruitCandidateStatus.NEW ||
      candidate.status === HrmRecruitCandidateStatus.PRIMARY_PASS ||
      candidate.status === HrmRecruitCandidateStatus.INTERVIEW_PASS)
  ) {
    return { command: 'interview', label: '安排面试' }
  }
  return undefined
}

/** 候选人的主操作 */
async function handlePrimaryAction(
  command: string,
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
) {
  // 登记结果
  if (command === 'interview-result') {
    await openInterviewResult(candidate)
    return
  }
  // 重新安排面试
  if (command === 'interview-change') {
    await openInterviewChange(candidate)
    return
  }
  // 确认入职
  if (command === 'confirm-entry') {
    await handleConfirmEntry(candidate)
    return
  }
  // 转为员工
  if (command === 'convert-employee') {
    openEntryForm(candidate)
    return
  }
  // 安排面试
  if (command === 'interview' && candidate.id) {
    openInterview(candidate.id)
  }
}

/** 获得候选人的更多操作 */
function getMoreActions(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  const actions: Array<{ command: string; label: string }> = []
  if (
    checkPermi(['hrm:recruit:interview:update']) &&
    candidate.status === HrmRecruitCandidateStatus.INTERVIEW &&
    candidate.interviewId &&
    candidate.interviewResult !== HrmRecruitInterviewResult.CANCELED
  ) {
    actions.push({ command: 'interview-change', label: '更改面试安排' })
    actions.push({ command: 'interview-cancel', label: '取消面试' })
  }
  if (
    checkPermi(['hrm:recruit:interview:create']) &&
    candidate.status === HrmRecruitCandidateStatus.INTERVIEW_PASS
  ) {
    actions.push({ command: 'reinterview', label: '安排复试' })
  }
  if (checkPermi(['hrm:recruit:candidate:update'])) {
    if (candidate.status === HrmRecruitCandidateStatus.NEW) {
      actions.push({ command: 'primary-pass', label: '初选通过' })
    }
    if (candidate.status === HrmRecruitCandidateStatus.INTERVIEW_PASS) {
      actions.push({ command: 'offer', label: '发 Offer' })
    }
    if (candidate.status === HrmRecruitCandidateStatus.ELIMINATED) {
      actions.push({ command: 'restore', label: '恢复为新候选人' })
    }
    if (
      candidate.status !== HrmRecruitCandidateStatus.ELIMINATED &&
      candidate.status !== HrmRecruitCandidateStatus.JOINED
    ) {
      actions.push({ command: 'eliminate', label: '淘汰' })
    }
  }
  if (
    checkPermi(['hrm:recruit:candidate:delete']) &&
    !candidate.employeeId &&
    candidate.status != null &&
    candidateDeleteStatuses.includes(candidate.status)
  ) {
    actions.push({ command: 'delete', label: '删除' })
  }
  return actions
}

/** 获得候选人的操作 */
function getCandidateActions(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  return {
    primary: getPrimaryAction(candidate),
    more: getMoreActions(candidate)
  }
}

/** 候选人的更多操作 */
async function handleMoreCommand(
  command: string,
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
) {
  // 初选通过
  if (command === 'primary-pass') {
    await handleStatus(candidate, HrmRecruitCandidateStatus.PRIMARY_PASS)
    return
  }
  // 发 Offer
  if (command === 'offer') {
    await handleStatus(candidate, HrmRecruitCandidateStatus.OFFER_SENT)
    return
  }
  // 恢复为新候选人
  if (command === 'restore') {
    await handleStatus(candidate, HrmRecruitCandidateStatus.NEW)
    return
  }
  // 更改面试安排
  if (command === 'interview-change') {
    await openInterviewChange(candidate)
    return
  }
  // 取消面试
  if (command === 'interview-cancel') {
    await openInterviewCancel(candidate)
    return
  }
  // 安排复试
  if (command === 'reinterview' && candidate.id) {
    openReinterview(candidate.id)
    return
  }
  // 淘汰
  if (command === 'eliminate') {
    openEliminateForm(candidate)
    return
  }
  // 删除
  if (command === 'delete') {
    await handleDelete(candidate.id)
  }
}

/** 修改候选人状态 */
async function handleStatus(
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO,
  status: HrmRecruitCandidateStatusValue
) {
  if (!candidate.id) {
    return
  }
  await RecruitCandidateApi.updateRecruitCandidateStatus({ id: candidate.id, status })
  message.success(t('common.updateSuccess'))
  await refreshList()
}

/** 确认入职 */
async function handleConfirmEntry(candidate: RecruitCandidateApi.HrmRecruitCandidateVO) {
  if (!candidate.employeeId) {
    return
  }
  employeeFormRef.value?.open('confirm', candidate.employeeId)
}

/** 删除按钮操作 */
async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await RecruitCandidateApi.deleteRecruitCandidate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await refreshList()
  } catch {}
}

/** 批量删除候选人 */
async function handleBatchDelete() {
  if (!selectedIds.value.length) {
    return
  }
  try {
    // 删除的二次确认
    await message.confirm(`确认删除选中的 ${selectedIds.value.length} 位候选人吗？`)
    const hasSuccess = await executeBatch(
      selectedIds.value.map((id) => RecruitCandidateApi.deleteRecruitCandidate(id))
    )
    if (!hasSuccess) {
      return
    }
    await handleBatchSuccess()
  } catch {}
}

/** 批量操作 */
async function handleBatchCommand(command: string) {
  if (command === 'status') {
    openBatchStatusForm()
    return
  }
  if (command === 'interview') {
    openBatchInterview()
    return
  }
  if (command === 'post') {
    openBatchPostForm()
    return
  }
  if (command === 'channel') {
    openBatchChannelForm()
    return
  }
  if (command === 'eliminate') {
    openBatchEliminateForm()
    return
  }
  if (command === 'delete') {
    await handleBatchDelete()
  }
}

/** 批量操作成功 */
async function handleBatchSuccess() {
  selectedIds.value = []
  await refreshList()
}

/** 初始化 */
onMounted(() => {
  if (route.query.status) {
    const status = Number(route.query.status) as HrmRecruitCandidateStatusValue
    activeStatus.value = String(status)
    queryParams.status = status
  }
  refreshList()
})
</script>
