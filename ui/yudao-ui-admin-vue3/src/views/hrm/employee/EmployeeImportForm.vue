<template>
  <Dialog v-model="dialogVisible" title="员工档案导入" width="460">
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="importUrl + '?duplicateStrategy=' + duplicateStrategy"
      :auto-upload="false"
      :disabled="formLoading"
      :headers="uploadHeaders"
      :limit="1"
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
          <div class="el-upload__tip mb-8px">
            重复员工：
            <el-radio-group v-model="duplicateStrategy">
              <el-radio :value="1">跳过</el-radio>
              <el-radio :value="2">覆盖</el-radio>
              <el-radio :value="3">判失败</el-radio>
            </el-radio-group>
          </div>
          <span>仅允许导入 xls、xlsx 格式文件。</span>
          <el-link
            :underline="false"
            style="font-size: 12px; vertical-align: baseline"
            type="primary"
            @click="importTemplate"
          >
            下载模板
          </el-link>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as EmployeeApi from '@/api/hrm/employee'
import { getAccessToken, getTenantId } from '@/utils/auth'
import download from '@/utils/download'

defineOptions({ name: 'HrmEmployeeImportForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const uploadRef = ref() // 上传组件 Ref
const importUrl =
  import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/hrm/employee/import'
const uploadHeaders = ref() // 上传 Header 头
const fileList = ref([]) // 文件列表
const duplicateStrategy = ref(3) // 重复员工处理策略：1 跳过，2 覆盖，3 判失败

/** 打开弹窗 */
function open() {
  dialogVisible.value = true
  duplicateStrategy.value = 3
  fileList.value = []
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emits = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交导入 */
function submitForm() {
  if (fileList.value.length === 0) {
    message.error('请上传文件')
    return
  }
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken(),
    'tenant-id': getTenantId()
  }
  formLoading.value = true
  uploadRef.value!.submit()
}

/** 文件上传成功 */
function submitFormSuccess(response: EmployeeApi.HrmEmployeeImportResponse) {
  if (response.code !== 0) {
    message.error(response.msg)
    resetForm()
    return
  }
  const data = response.data
  let text = '上传成功数量：' + data.createJobNumbers.length + ';'
  for (const jobNumber of data.createJobNumbers) {
    text += '< ' + jobNumber + ' >'
  }
  text += '更新成功数量：' + data.updateJobNumbers.length + ';'
  for (const jobNumber of data.updateJobNumbers) {
    text += '< ' + jobNumber + ' >'
  }
  text += '跳过数量：' + data.skipJobNumbers.length + ';'
  for (const jobNumber of data.skipJobNumbers) {
    text += '< ' + jobNumber + ' >'
  }
  text += '更新失败数量：' + Object.keys(data.failureJobNumbers).length + ';'
  for (const jobNumber in data.failureJobNumbers) {
    text += '< ' + jobNumber + ': ' + data.failureJobNumbers[jobNumber] + ' >'
  }
  message.alert(text)
  formLoading.value = false
  dialogVisible.value = false
  // 发送操作成功的事件
  emits('success')
}

/** 文件上传失败 */
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

/** 文件超限 */
function handleExceed() {
  message.error('最多只能上传一个文件！')
}

/** 下载导入模板 */
async function importTemplate() {
  const res = await EmployeeApi.importEmployeeTemplate()
  download.excel(res, '员工档案导入模板.xlsx')
}
</script>
