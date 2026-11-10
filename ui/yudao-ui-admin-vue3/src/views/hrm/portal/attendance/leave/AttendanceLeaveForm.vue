<template>
  <Dialog v-model="dialogVisible" title="请假申请" width="600px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="请假类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择请假类型" clearable class="!w-100%">
          <el-option
            v-for="item in getStrDictOptions(DICT_TYPE.HRM_ATTENDANCE_LEAVE_TYPE)"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          type="datetime"
          value-format="x"
          placeholder="请选择开始时间"
          class="!w-100%"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          type="datetime"
          value-format="x"
          placeholder="请选择结束时间"
          class="!w-100%"
        />
      </el-form-item>
      <el-form-item label="请假天数" prop="day">
        <el-input-number
          v-model="formData.day"
          :min="0.01"
          :precision="2"
          :step="0.5"
          controls-position="right"
          placeholder="请输入请假天数"
          class="!w-100%"
        />
      </el-form-item>
      <el-form-item label="请假事由" prop="reason">
        <el-input
          v-model="formData.reason"
          type="textarea"
          :rows="3"
          :maxlength="300"
          show-word-limit
          placeholder="请输入请假事由"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          :maxlength="500"
          show-word-limit
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import * as AttendanceLeaveApi from '@/api/hrm/portal/attendance/leave'

defineOptions({ name: 'HrmPortalAttendanceLeaveForm' })

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formRef = ref<FormInstance>() // 表单 Ref
const formData = ref<AttendanceLeaveApi.HrmAttendanceLeaveCreateVO>({
  type: undefined,
  startTime: undefined,
  endTime: undefined,
  day: 1,
  reason: '',
  remark: ''
}) // 表单数据
const formRules = reactive<FormRules>({
  type: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (_, value, callback) => {
        if (
          value &&
          formData.value.startTime &&
          new Date(value).getTime() <= new Date(formData.value.startTime).getTime()
        ) {
          callback(new Error('结束时间必须晚于开始时间'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  day: [{ required: true, message: '请输入请假天数', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入请假事由', trigger: 'blur' }]
}) // 表单校验规则

/** 打开弹窗 */
function open() {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) {
    return
  }
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    await AttendanceLeaveApi.createMyAttendanceLeave({
      ...formData.value,
      startTime: Number(formData.value.startTime),
      endTime: Number(formData.value.endTime)
    })
    message.success('请假申请已提交')
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
    type: undefined,
    startTime: undefined,
    endTime: undefined,
    day: 1,
    reason: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
