<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="月份">
        <el-date-picker
          v-model="month"
          type="month"
          value-format="YYYY-MM"
          :clearable="false"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="员工" prop="search">
        <el-input
          v-model="queryParams.search"
          placeholder="请输入员工姓名或工号"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门" prop="deptIds">
        <DeptSelect v-model="queryParams.deptIds" multiple class="!w-240px" />
      </el-form-item>
      <el-form-item label="打卡类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择打卡类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_CLOCK_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="打卡地点" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入打卡地点"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="打卡来源" prop="sourceTypes">
        <el-select
          v-model="queryParams.sourceTypes"
          placeholder="请选择打卡来源"
          clearable
          multiple
          collapse-tags
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_CLOCK_SOURCE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['hrm:attendance:clock:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          :loading="batchDeleteLoading"
          @click="handleBatchDelete"
          v-hasPermi="['hrm:attendance:clock:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
        <el-button
          plain
          :loading="exportLoading"
          @click="handleExport"
          v-hasPermi="['hrm:attendance:clock:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="48" :selectable="isManualClock" />
      <el-table-column label="员工" prop="employeeName" width="100" show-overflow-tooltip />
      <el-table-column label="工号" prop="jobNumber" width="100" show-overflow-tooltip />
      <el-table-column label="部门" prop="deptName" width="120" show-overflow-tooltip />
      <el-table-column label="岗位" prop="postName" width="120" show-overflow-tooltip />
      <el-table-column label="打卡类型" prop="type" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_ATTENDANCE_CLOCK_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column
        label="应打卡时间"
        prop="attendanceTime"
        width="170"
        :formatter="dateFormatter"
      />
      <el-table-column label="打卡时间" prop="clockTime" width="170" :formatter="dateFormatter" />
      <el-table-column label="状态" prop="status" width="90">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_ATTENDANCE_CLOCK_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="打卡来源" prop="sourceType" width="105">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_ATTENDANCE_CLOCK_SOURCE" :value="scope.row.sourceType" />
        </template>
      </el-table-column>
      <el-table-column label="打卡地点" prop="address" width="130" show-overflow-tooltip />
      <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            :disabled="!isManualClock(scope.row)"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['hrm:attendance:clock:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            :disabled="!isManualClock(scope.row)"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['hrm:attendance:clock:delete']"
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

  <!-- 打卡记录表单弹窗：添加/修改 -->
  <AttendanceClockForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import download from '@/utils/download'
import { dateFormatter, formatDate, getMonthRange } from '@/utils/formatTime'
import * as AttendanceClockApi from '@/api/hrm/attendance/clock'
import { HrmAttendanceClockSource } from '@/views/hrm/utils/constants'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import AttendanceClockForm from './AttendanceClockForm.vue'

/** 打卡记录列表 */
defineOptions({ name: 'HrmAttendanceClockRecordList' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const batchDeleteLoading = ref(false) // 批量删除的加载中
const total = ref(0) // 列表的总页数
const list = ref<AttendanceClockApi.HrmAttendanceClockVO[]>([]) // 列表的数据
const checkedIds = ref<number[]>([]) // 选中的打卡记录编号
const month = ref(formatDate(new Date(), 'YYYY-MM')) // 查询月份
const queryFormRef = ref() // 搜索的表单
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  search: '',
  deptIds: [] as number[],
  type: undefined as number | undefined,
  address: '',
  sourceTypes: [] as number[],
  attendanceTime: [] as string[]
})

/** 监听月份变化 */
watch(
  month,
  (value) => {
    queryParams.attendanceTime = getMonthRange(value)
  },
  { immediate: true }
)

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await AttendanceClockApi.getAttendanceClockPage(queryParams)
    list.value = data.list
    total.value = data.total
    checkedIds.value = []
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
  queryFormRef.value?.resetFields()
  month.value = formatDate(new Date(), 'YYYY-MM')
  handleQuery()
}

const formRef = ref<InstanceType<typeof AttendanceClockForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

/** 导出按钮操作 */
async function handleExport() {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await AttendanceClockApi.exportAttendanceClock(queryParams)
    download.excel(data, '打卡记录.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 选中行操作 */
function handleSelectionChange(rows: AttendanceClockApi.HrmAttendanceClockVO[]) {
  checkedIds.value = rows.map((row) => row.id!)
}

/** 是否为管理员补录 */
function isManualClock(row: AttendanceClockApi.HrmAttendanceClockVO) {
  return row.sourceType === HrmAttendanceClockSource.MANUAL
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
    await AttendanceClockApi.deleteAttendanceClock(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除按钮操作 */
async function handleBatchDelete() {
  if (checkedIds.value.length === 0) {
    return
  }
  try {
    // 删除的二次确认
    await message.confirm(
      `确定删除选中的 ${checkedIds.value.length} 条打卡记录吗？删除后会立即影响日/月考勤统计。`
    )
    // 发起批量删除
    batchDeleteLoading.value = true
    // 发起删除
    await AttendanceClockApi.deleteAttendanceClockList(checkedIds.value)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } finally {
    batchDeleteLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
