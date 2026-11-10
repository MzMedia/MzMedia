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
      <el-form-item label="月份" prop="month">
        <el-date-picker
          v-model="queryParams.month"
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
      <el-form-item label="是否全勤" prop="fullAttendance">
        <el-select
          v-model="queryParams.fullAttendance"
          placeholder="请选择"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_YES_NO)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value === 1"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          plain
          :loading="exportLoading"
          @click="handleExport"
          v-hasPermi="['hrm:attendance:statistics:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true">
      <el-table-column label="员工" prop="employeeName" fixed="left" min-width="120">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row)"
            v-hasPermi="['hrm:attendance:statistics:query']"
          >
            {{ scope.row.employeeName }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="工号" prop="jobNumber" min-width="110" />
      <el-table-column label="部门" prop="deptName" min-width="120" />
      <el-table-column label="岗位" prop="postName" min-width="120" />
      <el-table-column label="应出勤天数" prop="attendDays" width="110" />
      <el-table-column label="实际出勤天数" width="120">
        <template #default="scope">{{ formatHrmDays(scope.row.actualDays) }}</template>
      </el-table-column>
      <el-table-column label="迟到时长（分钟）" prop="lateMinute" width="140" />
      <el-table-column label="迟到次数" prop="lateCount" width="100" />
      <el-table-column label="早退时长（分钟）" prop="earlyMinute" width="140" />
      <el-table-column label="早退次数" prop="earlyCount" width="100" />
      <el-table-column label="旷工天数" width="100">
        <template #default="scope">{{ formatHrmDays(scope.row.absenteeismDays) }}</template>
      </el-table-column>
      <el-table-column label="缺卡次数" prop="misscardCount" width="100" />
      <el-table-column label="请假天数" width="100">
        <template #default="scope">{{ formatHrmDays(scope.row.leaveDays) }}</template>
      </el-table-column>
      <el-table-column label="考勤扣款" width="110">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.attendanceDeductAmount) }} 元
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
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import * as AttendanceStatisticsApi from '@/api/hrm/attendance/statistics'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import { formatHrmDays, formatHrmMoney } from '@/views/hrm/utils/format'

/** 月度考勤管理 */
defineOptions({ name: 'HrmAttendanceMonth' })

const message = useMessage() // 消息弹窗
const router = useRouter() // 路由
const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const total = ref(0) // 列表的总页数
const list = ref<AttendanceStatisticsApi.HrmAttendanceMonthRecordVO[]>([]) // 列表的数据
const queryFormRef = ref() // 搜索的表单
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  month: formatDate(new Date(), 'YYYY-MM'),
  search: '',
  deptIds: [] as number[],
  fullAttendance: undefined as boolean | undefined
})

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await AttendanceStatisticsApi.getAttendanceMonthRecordPage(getQueryParams())
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 导出按钮操作 */
async function handleExport() {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await AttendanceStatisticsApi.exportAttendanceMonthRecord(getQueryParams())
    download.excel(data, '员工月度考勤汇总.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 打开月度考勤详情 */
function openDetail(row: AttendanceStatisticsApi.HrmAttendanceMonthRecordVO) {
  router.push({
    name: 'HrmAttendanceMonthDetail',
    params: {
      employeeId: row.employeeId
    },
    query: {
      year: row.year,
      month: row.month
    }
  })
}

/** 构建查询参数 */
function getQueryParams() {
  const [year, month] = queryParams.month.split('-').map(Number)
  return {
    ...queryParams,
    year,
    month
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
