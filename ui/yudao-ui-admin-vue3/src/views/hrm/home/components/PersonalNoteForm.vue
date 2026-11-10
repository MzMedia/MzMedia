<template>
  <Dialog v-model="dialogVisible" title="新增备忘" width="520px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="84px">
      <el-form-item label="提醒时间" prop="reminderTime">
        <el-date-picker
          v-model="formData.reminderTime"
          type="datetime"
          value-format="x"
          class="!w-1/1"
        />
      </el-form-item>
      <el-form-item label="备忘内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="4"
          :maxlength="1024"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :loading="submitting" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'
import * as PersonalNoteApi from '@/api/hrm/employee/personal-note'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'HrmHomePersonalNoteForm' })

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否展示
const submitting = ref(false) // 提交中
const formRef = ref<FormInstance>() // 表单 Ref
const formData = reactive<PersonalNoteApi.HrmEmployeePersonalNoteVO>({
  content: '',
  reminderTime: new Date().getTime()
})
const formRules = reactive<FormRules>({
  reminderTime: [{ required: true, message: '提醒时间不能为空', trigger: 'change' }],
  content: [{ required: true, message: '备忘内容不能为空', trigger: 'blur' }]
})

/** 打开新增备忘表单 */
function open(date: string) {
  formData.content = ''
  formData.reminderTime = dayjs(`${date} ${formatDate(new Date(), 'HH:mm')}:00`).valueOf()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

/** 提交新增备忘 */
async function submitForm() {
  // 校验表单
  if (!formRef.value || !(await formRef.value.validate())) {
    return
  }
  // 提交请求
  submitting.value = true
  try {
    await PersonalNoteApi.createEmployeePersonalNote(formData)
    message.success('新增备忘成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
