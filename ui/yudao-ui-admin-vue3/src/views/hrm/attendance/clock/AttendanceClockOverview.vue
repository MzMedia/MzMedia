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
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
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
    <el-table v-loading="loading" :data="list" :stripe="true" border scrollbar-always-on>
      <el-table-column
        label="员工"
        prop="employeeName"
        fixed="left"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="工号"
        prop="jobNumber"
        fixed="left"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="部门"
        prop="deptName"
        fixed="left"
        width="140"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="岗位"
        prop="postName"
        fixed="left"
        width="140"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-for="day in dayColumns"
        :key="day.date"
        :label="day.day"
        :min-width="168"
        align="center"
      >
        <template #header>
          <div class="flex flex-col leading-20px">
            <span>{{ day.day }}</span>
            <span>{{ day.week }}</span>
          </div>
        </template>
        <template #default="scope">
          <el-button
            v-if="scope.row.dailyClockMap?.[day.date]"
            link
            class="!h-auto min-h-52px w-full !justify-start !px-8px !py-6px text-left"
            :aria-label="`查看 ${day.date} 考勤详情`"
            @click="openDailyDetail(scope.row, day.date)"
          >
            <span class="flex w-full flex-col gap-2px whitespace-normal">
              <span
                v-for="(item, index) in scope.row.dailyClockMap[day.date].overviews || []"
                :key="`${item.text || item.type}-${index}`"
                class="grid min-h-20px w-full grid-cols-[32px_48px_1fr] items-center gap-x-4px leading-20px"
              >
                <template v-if="item.type">
                  <span class="text-[var(--el-text-color-secondary)]">{{ item.type }}</span>
                  <span class="text-[var(--el-text-color-primary)]">{{ item.time }}</span>
                  <span :class="getOverviewTextClass(item.status)">{{ item.status }}</span>
                </template>
                <span
                  v-else
                  class="col-span-3 text-center"
                  :class="getOverviewTextClass(item.text)"
                >
                  {{ item.text }}
                </span>
              </span>
            </span>
          </el-button>
          <span v-else class="text-[var(--el-text-color-placeholder)]">-</span>
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

  <!-- 每日考勤详情弹窗 -->
  <AttendanceClockDailyDetail ref="dailyDetailRef" />
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import * as AttendanceStatisticsApi from '@/api/hrm/attendance/statistics'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import AttendanceClockDailyDetail from './AttendanceClockDailyDetail.vue'

/** 月度打卡概况列表 */
defineOptions({ name: 'HrmAttendanceClockOverview' })

const message = useMessage() // 消息弹窗
const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const total = ref(0) // 列表的总页数
const list = ref<AttendanceStatisticsApi.HrmAttendanceMonthDailyOverviewVO[]>([]) // 列表的数据
const queryFormRef = ref() // 搜索的表单
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  month: formatDate(new Date(), 'YYYY-MM'),
  search: '',
  deptIds: [] as number[]
})

const dailyDetailRef = ref<InstanceType<typeof AttendanceClockDailyDetail>>() // 每日考勤详情 Ref
const dayColumns = computed(() => {
  const month = dayjs(queryParams.month)
  return Array.from({ length: month.daysInMonth() }, (_, index) => {
    const date = month.date(index + 1)
    return {
      date: formatDate(date, 'YYYY-MM-DD'),
      day: formatDate(date, 'DD'),
      week: `周${'日一二三四五六'[date.day()]}`
    }
  })
})

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await AttendanceStatisticsApi.getAttendanceMonthDailyOverviewPage(getQueryParams())
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
    const data = await AttendanceStatisticsApi.exportAttendanceMonthDailyOverview(getQueryParams())
    download.excel(data, '员工月度打卡概况.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 打开每日考勤详情 */
function openDailyDetail(
  row: AttendanceStatisticsApi.HrmAttendanceMonthDailyOverviewVO,
  attendanceDate: string
) {
  dailyDetailRef.value?.open(row.employeeId, attendanceDate)
}

/** 构建查询参数 */
function getQueryParams() {
  const month = dayjs(queryParams.month)
  return {
    ...queryParams,
    year: month.year(),
    month: month.month() + 1
  }
}

/** 获得概况文字颜色 */
function getOverviewTextClass(value?: string) {
  if (!value || value === '休息' || value === '未排班') {
    return 'text-[var(--el-text-color-secondary)]'
  }
  if (value.includes('旷工') || value.includes('缺卡')) {
    return 'text-[var(--el-color-danger)]'
  }
  if (value.includes('迟到') || value.includes('早退')) {
    return 'text-[var(--el-color-warning)]'
  }
  if (value.includes('正常')) {
    return 'text-[var(--el-color-success)]'
  }
  return 'text-[var(--el-color-primary)]'
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
