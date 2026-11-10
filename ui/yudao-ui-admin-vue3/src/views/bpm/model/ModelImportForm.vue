<template>
  <Dialog v-model="dialogVisible" title="导入流程模型" width="640">
    <el-alert
      class="mb-15px"
      description="导入会完整保留流程配置，并将新模型归属到当前租户。请确认人员、部门、表单、子流程等关联在当前租户有效后再发布。"
      show-icon
      title="导入说明"
      type="info"
    />
    <el-form ref="formRef" :model="formData" label-width="100px">
      <el-form-item label="流程模型文件">
        <el-upload
          v-model:file-list="fileList"
          :auto-upload="false"
          :limit="1"
          accept=".json"
          drag
          :on-change="handleChange"
          :on-remove="resetFile"
        >
          <Icon class="mb-10px" icon="ep:upload-filled" :size="32" />
          <div>点击或拖拽 JSON 流程模型文件到此处</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="流程标识" prop="key" :rules="formRules.key">
        <el-input v-model="formData.key" placeholder="请输入流程标识" />
      </el-form-item>
      <el-form-item label="流程名称" prop="name" :rules="formRules.name">
        <el-input v-model="formData.name" placeholder="请输入流程名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules, UploadFile, UploadUserFile } from 'element-plus'

import * as ModelApi from '@/api/bpm/model'

defineOptions({ name: 'BpmModelImportForm' })

const emit = defineEmits(['success'])
const message = useMessage()
const dialogVisible = ref(false)
const formLoading = ref(false)
const file = ref<File>()
const fileList = ref<UploadUserFile[]>([])
const formRef = ref<FormInstance>()
const formData = reactive({ key: '', name: '' })
const formRules: FormRules = {
  key: [{ required: true, message: '请输入流程标识' }],
  name: [{ required: true, message: '请输入流程名称' }]
}

const open = () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open })

const handleChange = async (uploadFile: UploadFile) => {
  if (!uploadFile.raw) return
  if (!uploadFile.name.toLowerCase().endsWith('.json')) {
    message.error('仅支持上传 JSON 格式的流程模型文件')
    resetFile()
    return
  }
  try {
    const data = JSON.parse(await uploadFile.raw.text())
    file.value = uploadFile.raw
    formData.key = data.key || ''
    formData.name = data.name || ''
  } catch {
    resetFile()
    message.error('JSON 文件格式不正确')
  }
}

const submitForm = async () => {
  if (!file.value) return message.warning('请上传流程模型文件')
  await formRef.value?.validate()
  formLoading.value = true
  try {
    await ModelApi.importModel(file.value, formData.key, formData.name)
    message.success('导入成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetFile = () => {
  file.value = undefined
  fileList.value = []
}

const resetForm = () => {
  resetFile()
  formData.key = ''
  formData.name = ''
  formRef.value?.clearValidate()
}
</script>
