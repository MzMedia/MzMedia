<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="员工" prop="employeeId">
            <HrmEmployeeSelect
              v-model="formData.employeeId"
              :disabled="formType === 'update'"
              @update:model-value="handleShiftConditionChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="打卡类型" prop="type">
            <el-select v-model="formData.type" class="!w-1/1" @change="applyShiftDefaultTime">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_CLOCK_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="打卡日期" prop="attendanceTime">
            <el-date-picker
              v-model="formData.attendanceTime"
              type="date"
              placeholder="请选择打卡日期"
              class="!w-1/1"
              @change="handleShiftConditionChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="打卡时间" prop="clockTime">
            <el-time-picker
              v-model="formData.clockTime"
              format="HH:mm:ss"
              placeholder="请选择打卡时间"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-alert
        v-if="shiftInfo"
        :title="shiftTimeTip"
        type="info"
        :closable="false"
        class="mb-18px"
      />
      <el-alert
        v-else-if="formData.employeeId && formData.attendanceTime && !shiftLoading"
        title="该员工当天未配置有效班次，不能补录打卡"
        type="warning"
        :closable="false"
        class="mb-18px"
      />
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          maxlength="255"
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as AttendanceClockApi from '@/api/hrm/attendance/clock'
import { HrmAttendanceClockType } from '@/views/hrm/utils/constants'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'

/** 打卡记录表单 */
defineOptions({ name: 'HrmAttendanceClockForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const shiftLoading = ref(false) // 实际班次的加载中
const shiftInfo = ref<AttendanceClockApi.HrmAttendanceClockShiftVO>() // 员工当天实际班次
const formData = ref<AttendanceClockApi.HrmAttendanceClockVO>({
  id: undefined,
  employeeId: undefined,
  type: HrmAttendanceClockType.ON_DUTY,
  attendanceTime: undefined,
  clockTime: undefined,
  remark: ''
}) // 表单数据
const formRules = reactive<FormRules>({
  employeeId: [{ required: true, message: '员工不能为空', trigger: 'change' }],
  type: [{ required: true, message: '打卡类型不能为空', trigger: 'change' }],
  attendanceTime: [{ required: true, message: '打卡日期不能为空', trigger: 'change' }],
  clockTime: [{ required: true, message: '打卡时间不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await AttendanceClockApi.getAttendanceClock(id)
      await loadShift()
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  await formRef.value.validate()
  if (!shiftInfo.value) {
    message.warning('该员工当天未配置有效班次，不能补录打卡')
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    const attendanceTime =
      formData.value.type === HrmAttendanceClockType.ON_DUTY
        ? shiftInfo.value.startTime
        : shiftInfo.value.endTime
    const beginClockTime =
      formData.value.type === HrmAttendanceClockType.ON_DUTY
        ? shiftInfo.value.clockInStartTime
        : shiftInfo.value.clockOutStartTime
    const endClockTime =
      formData.value.type === HrmAttendanceClockType.ON_DUTY
        ? shiftInfo.value.clockInEndTime
        : shiftInfo.value.clockOutEndTime
    const clockTime = buildClockTime(beginClockTime, endClockTime)
    if (!clockTime) {
      message.warning(`打卡时间需在 ${formatShiftTimeRange(beginClockTime, endClockTime)} 内`)
      return
    }
    formData.value.attendanceTime = dayjs(attendanceTime).valueOf()
    formData.value.clockTime = clockTime.getTime()
    if (formType.value === 'create') {
      await AttendanceClockApi.createAttendanceClock(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await AttendanceClockApi.updateAttendanceClock(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    employeeId: undefined,
    type: HrmAttendanceClockType.ON_DUTY,
    attendanceTime: new Date(),
    clockTime: undefined,
    remark: ''
  }
  shiftInfo.value = undefined
  formRef.value?.resetFields()
}

/** 重新加载员工当天实际班次 */
function handleShiftConditionChange() {
  nextTick(() => loadShift(true))
}

/** 加载员工当天实际班次 */
async function loadShift(applyDefaultTime = false) {
  shiftInfo.value = undefined
  if (!formData.value.employeeId || !formData.value.attendanceTime) {
    return
  }
  shiftLoading.value = true
  try {
    shiftInfo.value = await AttendanceClockApi.getAttendanceClockShift({
      employeeId: formData.value.employeeId,
      attendanceTime: formatDate(formData.value.attendanceTime, 'YYYY-MM-DD HH:mm:ss')
    })
    if (applyDefaultTime) {
      applyShiftDefaultTime()
    }
  } finally {
    shiftLoading.value = false
  }
}

/** 根据打卡类型回填实际班次的应打卡时间 */
function applyShiftDefaultTime() {
  if (!shiftInfo.value) {
    formData.value.clockTime = undefined
    return
  }
  formData.value.clockTime =
    formData.value.type === HrmAttendanceClockType.ON_DUTY
      ? shiftInfo.value.startTime
      : shiftInfo.value.endTime
}

/** 构建实际打卡时间，并兼容跨日打卡范围 */
function buildClockTime(beginTime: Date, endTime: Date) {
  let clockTime = dayjs(
    `${formatDate(formData.value.attendanceTime, 'YYYY-MM-DD')} ${formatDate(
      formData.value.clockTime,
      'HH:mm:ss'
    )}`
  )
  const begin = dayjs(beginTime)
  const end = dayjs(endTime)
  const nextDayClockTime = clockTime.add(1, 'day')
  if (
    clockTime.isBefore(begin) &&
    (nextDayClockTime.isBefore(end) || nextDayClockTime.isSame(end))
  ) {
    clockTime = nextDayClockTime
  }
  return clockTime.isBefore(begin) || clockTime.isAfter(end) ? undefined : clockTime.toDate()
}

/** 格式化允许打卡时间范围 */
function formatShiftTimeRange(beginTime: Date, endTime: Date) {
  return `${formatDate(beginTime, 'MM-DD HH:mm')} 至 ${formatDate(endTime, 'MM-DD HH:mm')}`
}

/** 员工实际班次提示 */
const shiftTimeTip = computed(() => {
  if (!shiftInfo.value) {
    return ''
  }
  const clockInRange = formatShiftTimeRange(
    shiftInfo.value.clockInStartTime,
    shiftInfo.value.clockInEndTime
  )
  const clockOutRange = formatShiftTimeRange(
    shiftInfo.value.clockOutStartTime,
    shiftInfo.value.clockOutEndTime
  )
  return `班次 ${formatDate(shiftInfo.value.startTime, 'HH:mm')}-${formatDate(
    shiftInfo.value.endTime,
    'HH:mm'
  )}；上班可打卡 ${clockInRange}；下班可打卡 ${clockOutRange}`
})
</script>
