<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="820px">
    <div v-loading="loading">
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="班次">{{ detailData.shiftName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="考勤结果">
          {{ detailData.attendanceResult || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="应打卡次数">
          {{ detailData.requiredClockCount || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="实际打卡次数">
          {{ detailData.clockList?.length || 0 }}
        </el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailData?.clockList || []" class="mt-16px">
        <el-table-column label="打卡类型" prop="type" width="110">
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
        <el-table-column label="地点" prop="address" min-width="140" show-overflow-tooltip />
      </el-table>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import * as AttendanceStatisticsApi from '@/api/hrm/attendance/statistics'

/** 每日考勤详情 */
defineOptions({ name: 'HrmAttendanceClockDailyDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 详情的加载中
const detailData = ref<AttendanceStatisticsApi.HrmAttendanceDailyDetailVO>() // 每日考勤详情
const dialogTitle = computed(() => {
  if (!detailData.value) {
    return '每日考勤详情'
  }
  return `${detailData.value.employeeName || ''} ${formatDate(detailData.value.attendanceTime, 'YYYY-MM-DD')}`
})

/** 打开弹窗 */
async function open(employeeId: number, attendanceDate: string) {
  dialogVisible.value = true
  loading.value = true
  detailData.value = undefined
  try {
    // 获取详情数据
    detailData.value = await AttendanceStatisticsApi.getAttendanceDailyDetail({
      employeeId,
      attendanceTime: formatDate(attendanceDate, 'YYYY-MM-DD HH:mm:ss')
    })
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
