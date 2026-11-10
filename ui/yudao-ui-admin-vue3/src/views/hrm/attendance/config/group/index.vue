<template>
  <doc-alert title="【考勤】考勤管理" url="https://doc.iocoder.cn/hrm/attendance/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="考勤组" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入考勤组名称"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['hrm:attendance:group:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true">
      <el-table-column label="考勤组" prop="name" fixed="left" min-width="160" />
      <el-table-column label="考勤班次" min-width="420">
        <template #default="scope">
          <el-space wrap>
            <el-tag v-for="(shift, index) in scope.row.shifts || []" :key="index" effect="plain">
              {{ formatHrmAttendanceWeeks(shift.weeks) }} {{ shift.startTime }}-{{ shift.endTime }}
            </el-tag>
            <span v-if="!scope.row.shifts?.length">-</span>
          </el-space>
        </template>
      </el-table-column>
      <el-table-column label="考勤规则" min-width="120">
        <template #default>早晚打卡</template>
      </el-table-column>
      <el-table-column label="适用范围" min-width="220">
        <template #default="scope">
          <div v-if="scope.row.deptNames?.length">部门：{{ scope.row.deptNames.join('、') }}</div>
          <div v-if="scope.row.employeeNames?.length">
            员工：{{ scope.row.employeeNames.join('、') }}
          </div>
          <span v-if="!scope.row.deptNames?.length && !scope.row.employeeNames?.length">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['hrm:attendance:group:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            :disabled="scope.row.defaultStatus"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['hrm:attendance:group:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <AttendanceGroupForm ref="formRef" @success="refresh" />
</template>

<script lang="ts" setup>
import * as AttendanceGroupApi from '@/api/hrm/attendance/group'
import { formatHrmAttendanceWeeks } from '@/views/hrm/utils/format'
import AttendanceGroupForm from './AttendanceGroupForm.vue'

/** 考勤组列表 */
defineOptions({ name: 'HrmAttendanceGroup' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<AttendanceGroupApi.HrmAttendanceGroupVO[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: ''
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await AttendanceGroupApi.getAttendanceGroupPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref<InstanceType<typeof AttendanceGroupForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

/** 刷新页面数据 */
async function refresh() {
  await getList()
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
    await AttendanceGroupApi.deleteAttendanceGroup(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  refresh()
})
</script>
