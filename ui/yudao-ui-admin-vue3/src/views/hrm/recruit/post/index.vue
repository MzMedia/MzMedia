<template>
  <doc-alert title="【招聘】招聘管理" url="https://doc.iocoder.cn/hrm/recruit/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="72px"
    >
      <el-form-item label="职位名称" prop="postName">
        <el-input
          v-model="queryParams.postName"
          class="!w-240px"
          clearable
          placeholder="请输入职位名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工作性质" prop="jobNature">
        <el-select
          v-model="queryParams.jobNature"
          class="!w-240px"
          clearable
          placeholder="请选择工作性质"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_JOB_NATURE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="工作城市" prop="areaId">
        <AreaSelect v-model="queryParams.areaId" class="!w-240px" placeholder="请选择工作城市" />
      </el-form-item>
      <el-form-item label="用人部门" prop="deptId">
        <DeptSelect v-model="queryParams.deptId" class="!w-240px" placeholder="请选择用人部门" />
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
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
        <el-button
          v-hasPermi="['hrm:recruit:post:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeStatus" @tab-click="handleStatusTabClick">
      <el-tab-pane v-for="item in statusTabOptions" :key="item.value" :name="item.value">
        <template #label>
          {{ item.label }}
          <span class="text-[var(--el-text-color-secondary)]">（{{ item.count }}）</span>
        </template>
      </el-tab-pane>
    </el-tabs>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" fixed="left" label="职位名称" prop="postName" width="180">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.postName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="用人部门" prop="deptName" width="120" />
      <el-table-column align="center" label="工作性质" prop="jobNature" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_JOB_NATURE" :value="scope.row.jobNature" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="工作城市" prop="areaName" width="200">
        <template #default="scope">{{ scope.row.areaName || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="招聘人数" prop="recruitNum" width="100" />
      <el-table-column align="center" label="已入职人数" prop="hasEntryNum" width="110">
        <template #default="scope">{{ scope.row.hasEntryNum ?? 0 }}</template>
      </el-table-column>
      <el-table-column align="center" label="招聘进度" prop="recruitSchedule" width="100">
        <template #default="scope">{{ formatRecruitPostSchedule(scope.row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="工作经验" prop="workTime" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_WORK_TIME" :value="scope.row.workTime" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="学历要求" prop="educationRequire" width="120">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.HRM_RECRUIT_POST_EDUCATION"
            :value="scope.row.educationRequire"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="薪资范围" prop="minSalary" width="180">
        <template #default="scope">{{ formatRecruitPostSalary(scope.row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="年龄要求" prop="minAge" width="110">
        <template #default="scope">{{ formatRecruitPostAge(scope.row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="紧急程度" prop="emergencyLevel" width="100">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.HRM_RECRUIT_EMERGENCY_LEVEL"
            :value="scope.row.emergencyLevel"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="最迟到岗时间" prop="latestEntryTime" width="120">
        <template #default="scope">
          {{
            scope.row.latestEntryTime ? formatDate(scope.row.latestEntryTime, 'YYYY-MM-DD') : '-'
          }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="招聘负责人" prop="ownerEmployeeName" width="120" />
      <el-table-column align="center" label="职位类型" prop="postTypeName" width="120" />
      <el-table-column align="center" label="面试官" prop="interviewEmployeeNames" width="160">
        <template #default="scope">
          {{ scope.row.interviewEmployeeNames?.join('、') || '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_POST_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="160">
        <template #default="scope">
          <el-button
            v-hasPermi="['hrm:recruit:post:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['hrm:recruit:post:update']"
            link
            :type="isRecruiting(scope.row) ? 'warning' : 'success'"
            @click="handleStatus(scope.row)"
          >
            {{ isRecruiting(scope.row) ? '停止招聘' : '重新招聘' }}
          </el-button>
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
  <RecruitPostForm ref="formRef" @success="refreshList" />
</template>

<script lang="ts" setup>
import { ElMessageBox, type FormInstance, type TabsPaneContext } from 'element-plus'
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as RecruitPostApi from '@/api/hrm/recruit/post'
import AreaSelect from '@/views/system/area/components/AreaSelect.vue'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import RecruitPostForm from './RecruitPostForm.vue'
import {
  HrmEmployeeEntryStatus,
  HrmRecruitPostStatus,
  type HrmRecruitPostStatusValue
} from '@/views/hrm/utils/constants'
import {
  formatRecruitPostAge,
  formatRecruitPostSalary,
  formatRecruitPostSchedule
} from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmRecruitPost' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总条数
const list = ref<RecruitPostApi.HrmRecruitPostVO[]>([]) // 列表的数据
const statusCounts = ref<RecruitPostApi.HrmRecruitPostStatusCountVO[]>([]) // 招聘状态统计
const activeStatus = ref(String(HrmRecruitPostStatus.RECRUITING)) // 当前选中的招聘状态
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  postName: '',
  jobNature: undefined as number | undefined,
  areaId: undefined as number | undefined,
  deptId: undefined as number | undefined,
  ownerEmployeeId: undefined as number | undefined,
  status: HrmRecruitPostStatus.RECRUITING as HrmRecruitPostStatusValue
})
const queryFormRef = ref<FormInstance>() // 搜索的表单

/** 招聘状态页签 */
const statusTabOptions = computed(() => {
  const countMap = Object.fromEntries(statusCounts.value.map((item) => [item.status, item.count]))
  return getIntDictOptions(DICT_TYPE.HRM_RECRUIT_POST_STATUS).map((item) => ({
    label: item.label,
    value: String(item.value),
    count: countMap[Number(item.value)] ?? 0
  }))
})

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await RecruitPostApi.getRecruitPostPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 查询招聘职位状态统计 */
async function getStatusCounts() {
  statusCounts.value = await RecruitPostApi.getRecruitPostStatusCount(queryParams)
}

/** 刷新列表和状态统计 */
async function refreshList() {
  await Promise.all([getList(), getStatusCounts()])
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNo = 1
  refreshList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  activeStatus.value = String(HrmRecruitPostStatus.RECRUITING)
  queryParams.status = HrmRecruitPostStatus.RECRUITING
  handleQuery()
}

/** tab 切换 */
function handleStatusTabClick(tab: TabsPaneContext) {
  if (tab.paneName === undefined) {
    return
  }
  queryParams.status = Number(tab.paneName) as HrmRecruitPostStatusValue
  handleQuery()
}

const formRef = ref<InstanceType<typeof RecruitPostForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

const { push } = useRouter() // 路由操作

/** 打开详情 */
function openDetail(id: number) {
  push({ name: 'HrmRecruitPostDetail', params: { id } })
}

/** 是否正在招聘 */
function isRecruiting(post: RecruitPostApi.HrmRecruitPostVO) {
  return post.status === HrmRecruitPostStatus.RECRUITING
}

/** 修改招聘职位状态 */
async function handleStatus(row: RecruitPostApi.HrmRecruitPostVO) {
  if (!row.id) {
    return
  }
  if (isRecruiting(row)) {
    let stopReason: string
    try {
      const { value } = await ElMessageBox.prompt('请输入停止原因', '停止招聘', {
        inputPlaceholder: '例如：岗位暂停',
        confirmButtonText: '确 定',
        cancelButtonText: '取 消',
        inputValidator: (inputValue) => {
          const reason = String(inputValue ?? '').trim()
          if (!reason) {
            return '停止原因不能为空'
          }
          if (reason.length > 255) {
            return '停止原因不能超过 255 个字符'
          }
          return true
        }
      })
      stopReason = value.trim()
    } catch {
      return
    }
    await RecruitPostApi.updateRecruitPostStatus({
      id: row.id,
      status: HrmRecruitPostStatus.STOPPED,
      stopReason
    })
  } else {
    await RecruitPostApi.updateRecruitPostStatus({
      id: row.id,
      status: HrmRecruitPostStatus.RECRUITING
    })
  }
  message.success(t('common.updateSuccess'))
  await refreshList()
}

/** 初始化 */
onMounted(() => {
  refreshList()
})
</script>
