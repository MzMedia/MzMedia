<template>
  <div class="mb-12px mt-24px text-15px text-[var(--el-text-color-primary)] font-600">
    我的请假申请
  </div>
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" stripe>
    <el-table-column label="请假类型" align="center" width="110">
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.HRM_ATTENDANCE_LEAVE_TYPE" :value="scope.row.type" />
      </template>
    </el-table-column>
    <el-table-column label="开始时间" align="center" width="170">
      <template #default="scope">{{ formatDate(scope.row.startTime) }}</template>
    </el-table-column>
    <el-table-column label="结束时间" align="center" width="170">
      <template #default="scope">{{ formatDate(scope.row.endTime) }}</template>
    </el-table-column>
    <el-table-column label="请假天数" align="center" prop="day" width="100">
      <template #default="scope">{{ scope.row.day }} 天</template>
    </el-table-column>
    <el-table-column label="请假事由" align="center" prop="reason" min-width="160" />
    <el-table-column label="审批状态" align="center" width="110">
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.approvalStatus" />
      </template>
    </el-table-column>
    <el-table-column label="操作" align="center" width="150" fixed="right">
      <template #default="scope">
        <el-button
          v-if="scope.row.processInstanceId"
          link
          type="primary"
          @click="openProcessDetail(scope.row.processInstanceId)"
        >
          审批进度
        </el-button>
        <el-button
          v-if="scope.row.approvalStatus === BpmProcessInstanceStatus.RUNNING"
          v-hasPermi="['hrm:portal:attendance:leave']"
          link
          type="danger"
          @click="handleCancel(scope.row.id)"
        >
          取消
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <AttendanceLeaveForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { BpmProcessInstanceStatus } from '@/utils/constants'
import { formatDate } from '@/utils/formatTime'
import * as AttendanceLeaveApi from '@/api/hrm/portal/attendance/leave'
import AttendanceLeaveForm from '../leave/AttendanceLeaveForm.vue'

defineOptions({ name: 'HrmPortalAttendanceLeaveList' })

const message = useMessage() // 消息弹窗
const router = useRouter() // 路由
const loading = ref(false) // 加载中
const list = ref<AttendanceLeaveApi.HrmAttendanceLeaveVO[]>([]) // 列表数据
const formRef = ref<InstanceType<typeof AttendanceLeaveForm>>() // 表单 Ref

const emit = defineEmits<{
  changed: []
}>() // 定义 changed 事件

/** 获得我的请假申请列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await AttendanceLeaveApi.getMyAttendanceLeaveList()
  } finally {
    loading.value = false
  }
}

/** 打开请假申请表单 */
function openCreate() {
  formRef.value?.open()
}

/** 取消请假申请 */
async function handleCancel(id?: number) {
  if (!id) {
    return
  }
  try {
    const { value } = await message.prompt('请输入取消原因', '取消请假申请')
    if (!value.trim()) {
      message.warning('请输入取消原因')
      return
    }
    await AttendanceLeaveApi.cancelMyAttendanceLeave(id, value)
    message.success('请假申请已取消')
    await getList()
    emit('changed')
  } catch {}
}

/** 打开流程详情 */
function openProcessDetail(processInstanceId?: string) {
  if (!processInstanceId) {
    return
  }
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: { id: processInstanceId }
  })
}

defineExpose({ refresh: getList, openCreate }) // 提供刷新列表和新建请假方法
</script>
