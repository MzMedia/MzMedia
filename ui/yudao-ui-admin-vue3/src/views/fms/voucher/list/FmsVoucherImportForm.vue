<template>
  <Dialog v-model="dialogVisible" title="导入凭证" width="750px">
    <el-steps :active="step" align-center finish-status="success" class="mb-28px">
      <el-step title="上传文件" />
      <el-step title="导入数据" />
      <el-step title="导入完成" />
    </el-steps>

    <div v-if="step === 0" class="min-h-340px px-36px [&>div+div]:mt-28px">
      <div>
        <div class="mb-14px text-15px font-600 text-[var(--el-text-color-primary)]">
          一、请按照数据模板的格式准备要导入的数据
        </div>
        <div class="pl-20px">
          <el-button link type="primary" :loading="templateLoading" @click="downloadTemplate">
            <Icon icon="ep:download" /> 下载《凭证导入模板》
          </el-button>
          <div class="mt-8px text-13px text-[var(--el-text-color-secondary)]">
            导入文件请勿超过 2MB
          </div>
        </div>
      </div>
      <div>
        <div class="mb-14px text-15px font-600 text-[var(--el-text-color-primary)]">
          二、请选择需要导入的文件
        </div>
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          :auto-upload="false"
          :limit="1"
          :on-exceed="handleExceed"
          accept=".xlsx,.xls"
          action="none"
          drag
        >
          <Icon icon="ep:upload-filled" class="text-48px text-[var(--el-color-primary)]" />
          <div class="el-upload__text">将文件拖到此处，或<em>点击选择文件</em></div>
          <template #tip>
            <div class="el-upload__tip">仅支持 xls、xlsx 格式</div>
          </template>
        </el-upload>
      </div>
    </div>

    <div
      v-else-if="step === 1"
      class="flex min-h-330px flex-col items-center justify-center text-center"
    >
      <el-icon class="is-loading text-58px text-[var(--el-color-primary)]"><Loading /></el-icon>
      <div class="mt-24px text-18px font-600 text-[var(--el-text-color-primary)]">
        凭证正在导入，请稍候
      </div>
      <div class="mt-8px text-13px text-[var(--el-text-color-secondary)]">
        系统会按同一日期、凭证字和凭证号合并分录
      </div>
    </div>

    <div v-else class="flex min-h-330px flex-col items-center justify-center text-center">
      <el-result
        :icon="result?.failureVoucherCount ? 'warning' : 'success'"
        :title="result?.failureVoucherCount ? '凭证导入完成，部分数据未导入' : '凭证导入成功'"
      >
        <template #sub-title>
          <div class="leading-26px">
            {{ importResultSummary }}
          </div>
        </template>
        <template #extra>
          <el-button v-if="result?.errorFileUrl" type="primary" plain @click="downloadErrorFile">
            <Icon icon="ep:download" /> 下载错误数据
          </el-button>
        </template>
      </el-result>
    </div>

    <template #footer>
      <template v-if="step === 0">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :disabled="!fileList.length" @click="submitImport">
          开始导入
        </el-button>
      </template>
      <template v-else-if="step === 2">
        <el-button @click="resetImport">继续导入</el-button>
        <el-button type="primary" @click="dialogVisible = false">完 成</el-button>
      </template>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { UploadInstance, UploadUserFile } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

import { FmsVoucherApi } from '@/api/fms/voucher'
import type { FmsVoucherImportRespVO } from '@/api/fms/voucher'
import download from '@/utils/download'
import { downloadByUrl } from '@/utils/filt'

defineOptions({ name: 'FmsVoucherImportForm' })
const emit = defineEmits<{ success: [] }>()

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const step = ref(0) // 导入步骤
const accountSetId = ref(0) // 账套编号
const templateLoading = ref(false) // 导入模板的下载中
const fileList = ref<UploadUserFile[]>([]) // 导入文件列表
const result = ref<FmsVoucherImportRespVO>() // 凭证导入结果
const uploadRef = ref<UploadInstance>() // 文件上传 Ref

/** 凭证导入结果摘要 */
const importResultSummary = computed(() => {
  const importResult = result.value
  return `共 ${importResult?.totalVoucherCount || 0} 张凭证、${importResult?.totalRowCount || 0} 条分录，成功 ${importResult?.successVoucherCount || 0} 张、${importResult?.successRowCount || 0} 条，失败 ${importResult?.failureVoucherCount || 0} 张、${importResult?.failureRowCount || 0} 条`
})

/** 打开凭证导入弹窗 */
function open(id: number) {
  accountSetId.value = id
  dialogVisible.value = true
  resetImport()
}
defineExpose({ open })

/** 下载凭证导入模板 */
async function downloadTemplate() {
  templateLoading.value = true
  try {
    const data = await FmsVoucherApi.getVoucherImportTemplate(accountSetId.value)
    download.excel(data, '凭证导入模板.xlsx')
  } finally {
    templateLoading.value = false
  }
}

/** 提交凭证导入 */
async function submitImport() {
  const rawFile = fileList.value[0]?.raw
  if (!rawFile) {
    message.warning('请选择需要导入的文件')
    return
  }
  if (rawFile.size > 2 * 1024 * 1024) {
    message.error('导入文件不能超过 2MB')
    return
  }
  step.value = 1
  try {
    const importResult = await FmsVoucherApi.importVoucher(accountSetId.value, rawFile)
    result.value = importResult
    step.value = 2
    if (importResult.successVoucherCount > 0) {
      emit('success')
    }
  } catch {
    step.value = 0
  }
}

/** 下载导入错误文件 */
function downloadErrorFile() {
  if (!result.value?.errorFileUrl) return
  downloadByUrl({ url: result.value.errorFileUrl, fileName: '凭证导入错误数据.xlsx' })
}

/** 重置凭证导入 */
function resetImport() {
  step.value = 0
  fileList.value = []
  result.value = undefined
  nextTick(() => uploadRef.value?.clearFiles())
}

/** 处理上传文件数量超限 */
function handleExceed() {
  message.warning('每次只能上传一个文件')
}
</script>
