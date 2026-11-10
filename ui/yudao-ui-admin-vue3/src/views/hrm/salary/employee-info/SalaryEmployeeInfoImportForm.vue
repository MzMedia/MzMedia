<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="420">
    <!-- 导入文件 -->
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="importUrl"
      :auto-upload="false"
      :disabled="formLoading"
      :headers="uploadHeaders"
      :limit="1"
      :before-upload="beforeUpload"
      :on-error="submitFormError"
      :on-exceed="handleExceed"
      :on-success="submitFormSuccess"
      accept=".xlsx, .xls"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <span>仅允许导入 xls、xlsx 格式文件。</span>
          <el-link
            :underline="false"
            class="align-baseline text-12px"
            type="primary"
            @click="importTemplate"
          >
            下载模板
          </el-link>
        </div>
      </template>
    </el-upload>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { UploadRawFile, UploadUserFile } from 'element-plus'

import * as SalaryEmployeeInfoApi from '@/api/hrm/salary/employee-info'
import { getAccessToken, getTenantId } from '@/utils/auth'
import download from '@/utils/download'

defineOptions({ name: 'HrmSalaryEmployeeInfoImportForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗是否显示
const formLoading = ref(false) // 表单提交中
const uploadRef = ref() // 文件上传组件
const uploadHeaders = ref() // 上传请求头
const fileList = ref<UploadUserFile[]>([]) // 上传文件列表
const importType = ref<'fix' | 'change'>('fix') // 导入类型
const dialogTitle = computed(() => `薪资档案${importType.value === 'fix' ? '定薪' : '调薪'}导入`) // 弹窗标题

/** 导入地址 */
const importUrl = computed(
  () =>
    import.meta.env.VITE_BASE_URL +
    import.meta.env.VITE_API_URL +
    `/hrm/salary/employee-info/import-${importType.value}`
)

/** 打开弹窗 */
function open(type: 'fix' | 'change') {
  dialogVisible.value = true
  importType.value = type
  fileList.value = []
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emits = defineEmits(['success']) // 定义 success 事件

/** 提交导入 */
async function submitForm() {
  if (fileList.value.length === 0) {
    message.error('请上传文件')
    return
  }
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken(),
    'tenant-id': getTenantId()
  }
  // 提交请求
  formLoading.value = true
  uploadRef.value!.submit()
}

/** 处理导入成功 */
function submitFormSuccess(response: SalaryEmployeeInfoApi.SalaryEmployeeInfoImportResponse) {
  if (response.code !== 0) {
    message.error(response.msg)
    resetForm()
    return
  }
  const data = response.data
  const successJobNumbers = data.successJobNumbers as string[]
  const failureEntries = Object.entries(data.failureJobNumbers) as Array<[string, string]>
  let text = `导入成功数量：${successJobNumbers.length}；`
  for (const jobNumber of successJobNumbers.slice(0, 10)) {
    text += `< ${jobNumber} >`
  }
  if (successJobNumbers.length > 10) {
    text += `其余 ${successJobNumbers.length - 10} 条已省略。`
  }
  text += `导入失败数量：${failureEntries.length}；`
  for (const [jobNumber, reason] of failureEntries.slice(0, 10)) {
    text += `< ${jobNumber}: ${reason} >`
  }
  if (failureEntries.length > 10) {
    text += `其余 ${failureEntries.length - 10} 条已省略。`
  }
  message.alert(text)
  formLoading.value = false
  dialogVisible.value = false
  // 发送操作成功的事件
  emits('success')
}

/** 处理导入失败 */
function submitFormError() {
  message.error('上传失败，请您重新上传！')
  formLoading.value = false
}

/** 重置表单 */
async function resetForm() {
  formLoading.value = false
  await nextTick()
  uploadRef.value?.clearFiles()
}

/** 处理文件超出限制 */
function handleExceed() {
  message.error('最多只能上传一个文件！')
}

/** 校验导入文件格式 */
function beforeUpload(file: UploadRawFile) {
  const fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1).toLowerCase()
  if (!['xls', 'xlsx'].includes(fileExtension)) {
    message.error('仅允许导入 xls、xlsx 格式文件')
    formLoading.value = false
    nextTick(() => uploadRef.value?.clearFiles())
    return false
  }
  return true
}

/** 下载导入模板 */
async function importTemplate() {
  const res =
    importType.value === 'fix'
      ? await SalaryEmployeeInfoApi.getFixSalaryImportTemplate()
      : await SalaryEmployeeInfoApi.getChangeSalaryImportTemplate()
  download.excel(
    res,
    importType.value === 'fix' ? '薪资档案定薪导入模板.xls' : '薪资档案调薪导入模板.xls'
  )
}
</script>
