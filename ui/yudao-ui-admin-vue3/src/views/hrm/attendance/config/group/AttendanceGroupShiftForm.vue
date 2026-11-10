<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="760">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
      <el-form-item label="工作日" prop="weeks">
        <el-checkbox-group v-model="formData.weeks">
          <el-checkbox v-for="item in HRM_WEEK_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-alert
        class="mb-16px"
        :closable="false"
        type="info"
        show-icon
        title="打卡窗口需覆盖对应的上下班时间；结束时间早于开始时间时按次日计算，例如 18:00 至次日 04:59。"
      />
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上班时间" prop="startTime">
            <el-time-picker
              v-model="formData.startTime"
              class="!w-1/1"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="请选择上班时间"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下班时间" prop="endTime">
            <el-time-picker
              v-model="formData.endTime"
              class="!w-1/1"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="请选择下班时间"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="上班打卡时间段" prop="clockInTimeRange">
        <el-time-picker
          v-model="clockInTimeRange"
          is-range
          class="!w-1/1"
          format="HH:mm"
          value-format="HH:mm"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />
      </el-form-item>
      <el-form-item label="下班打卡时间段" prop="clockOutTimeRange">
        <el-time-picker
          v-model="clockOutTimeRange"
          is-range
          class="!w-1/1"
          format="HH:mm"
          value-format="HH:mm"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />
      </el-form-item>
      <el-form-item label="休息时间" prop="restTimeRange">
        <div class="flex w-1/1 items-center gap-16px">
          <el-time-picker
            v-model="restTimeRange"
            is-range
            class="!flex-1"
            format="HH:mm"
            value-format="HH:mm"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
          <el-checkbox v-model="formData.excludeRestTime">不计入工作时长</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item label="合计工作时长">
        <span>{{ formatHrmAttendanceShiftDuration(formData) }}</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { HrmAttendanceShift } from '@/api/hrm/attendance/group'
import { HRM_WEEK_OPTIONS } from '@/views/hrm/utils/constants'
import { formatHrmAttendanceShiftDuration } from '@/views/hrm/utils/format'

/** 考勤组班次表单 */
defineOptions({ name: 'HrmAttendanceGroupShiftForm' })

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formData = ref<HrmAttendanceShift>(createDefaultShift()) // 表单数据
const formRef = ref<FormInstance>() // 表单 Ref
const clockInTimeRange = computed<[string, string] | undefined>({
  get: () => buildTimeRange(formData.value.clockInStartTime, formData.value.clockInEndTime),
  set: (value) => {
    formData.value.clockInStartTime = value?.[0] || ''
    formData.value.clockInEndTime = value?.[1] || ''
  }
})
const clockOutTimeRange = computed<[string, string] | undefined>({
  get: () => buildTimeRange(formData.value.clockOutStartTime, formData.value.clockOutEndTime),
  set: (value) => {
    formData.value.clockOutStartTime = value?.[0] || ''
    formData.value.clockOutEndTime = value?.[1] || ''
  }
})
const restTimeRange = computed<[string, string] | undefined>({
  get: () => buildTimeRange(formData.value.restStartTime, formData.value.restEndTime),
  set: (value) => {
    formData.value.restStartTime = value?.[0] || ''
    formData.value.restEndTime = value?.[1] || ''
  }
})
const formRules = reactive<FormRules>({
  weeks: [{ required: true, message: '工作日不能为空', trigger: 'change' }],
  startTime: [{ required: true, message: '上班时间不能为空', trigger: 'change' }],
  endTime: [{ required: true, message: '下班时间不能为空', trigger: 'change' }],
  clockInTimeRange: [
    {
      validator: (_, __, callback) =>
        callback(
          clockInTimeRange.value?.[0] && clockInTimeRange.value?.[1]
            ? undefined
            : new Error('上班打卡时间段不能为空')
        ),
      trigger: 'change'
    }
  ],
  clockOutTimeRange: [
    {
      validator: (_, __, callback) =>
        callback(
          clockOutTimeRange.value?.[0] && clockOutTimeRange.value?.[1]
            ? undefined
            : new Error('下班打卡时间段不能为空')
        ),
      trigger: 'change'
    }
  ],
  restTimeRange: [
    {
      validator: (_, __, callback) =>
        callback(
          restTimeRange.value?.[0] && restTimeRange.value?.[1]
            ? undefined
            : new Error('休息时间不能为空')
        ),
      trigger: 'change'
    }
  ]
})

/** 打开弹窗 */
function open(shift?: HrmAttendanceShift) {
  dialogVisible.value = true
  dialogTitle.value = shift ? '编辑班次' : '新增班次'
  resetForm()
  if (shift) {
    formData.value = { ...shift, weeks: [...shift.weeks] }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ confirm: [shift: HrmAttendanceShift] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 发送操作成功的事件
  emit('confirm', {
    ...formData.value,
    weeks: [...formData.value.weeks].sort()
  })
  dialogVisible.value = false
}

/** 构造时间范围 */
function buildTimeRange(startTime?: string, endTime?: string) {
  return startTime && endTime ? ([startTime, endTime] as [string, string]) : undefined
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultShift()
  formRef.value?.resetFields()
}

/** 创建默认班次 */
function createDefaultShift(): HrmAttendanceShift {
  return {
    weeks: [1, 2, 3, 4, 5],
    startTime: '09:00',
    endTime: '18:00',
    clockInStartTime: '05:00',
    clockInEndTime: '17:59',
    clockOutStartTime: '09:01',
    clockOutEndTime: '04:59',
    restStartTime: '12:00',
    restEndTime: '13:00',
    excludeRestTime: false
  }
}
</script>
