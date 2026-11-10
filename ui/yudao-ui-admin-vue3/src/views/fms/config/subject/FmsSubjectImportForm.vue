<template>
  <Dialog v-model="dialogVisible" title="科目导入" width="600px">
    <el-upload
      v-if="!importResult"
      ref="uploadRef"
      v-model:file-list="fileList"
      :auto-upload="false"
      :disabled="formLoading"
      :limit="1"
      :on-exceed="handleExceed"
      accept=".xlsx, .xls"
      action="none"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <div>一级科目的上级科目编码填写 0，多项辅助核算使用“/”分隔</div>
          <span>仅允许导入 xls、xlsx 格式文件，且不超过 2 MB。</span>
          <el-link
            :disabled="templateLoading"
            :underline="false"
            class="ml-5px !align-baseline !text-12px"
            type="primary"
            @click="importTemplate"
          >
            下载模板
          </el-link>
        </div>
      </template>
    </el-upload>

    <!-- 导入结果 -->
    <template v-else>
      <el-result
        :icon="failureCount ? 'warning' : 'success'"
        :sub-title="`共 ${importResult.totalCount} 个科目，成功 ${importResult.successSubjectCodes.length} 个，失败 ${failureCount} 个`"
        :title="failureCount ? '科目导入完成，部分数据未导入' : '科目导入成功'"
      />
      <el-table v-if="failureCount" :data="failureRows" border max-height="260px">
        <el-table-column label="导入行" min-width="220" prop="label" show-overflow-tooltip />
        <el-table-column label="失败原因" min-width="260" prop="reason" show-overflow-tooltip />
      </el-table>
    </template>

    <template #footer>
      <template v-if="!importResult">
        <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </template>
      <template v-else>
        <el-button @click="resetForm">继续导入</el-button>
        <el-button type="primary" @click="dialogVisible = false">完 成</el-button>
      </template>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectImportRespVO } from '@/api/fms/config/subject'
import download from '@/utils/download'
import type { UploadInstance, UploadUserFile } from 'element-plus'

defineOptions({ name: 'FmsSubjectImportForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const templateLoading = ref(false) // 导入模板的下载中
const accountSetId = ref(0) // 当前账套编号
const uploadRef = ref<UploadInstance>() // 上传组件 Ref
const fileList = ref<UploadUserFile[]>([]) // 文件列表
const importResult = ref<FmsSubjectImportRespVO>() // 导入结果

const failureRows = computed(() =>
  Object.entries(importResult.value?.failureReasons || {}).map(([label, reason]) => ({
    label,
    reason
  }))
) // 导入失败明细
const failureCount = computed(() => failureRows.value.length) // 导入失败数量

/** 打开弹窗 */
function open(id: number) {
  dialogVisible.value = true
  accountSetId.value = id
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交导入 */
const emit = defineEmits(['success']) // 定义 success 事件，用于导入成功后的回调
async function submitForm() {
  if (fileList.value.length === 0 || !fileList.value[0].raw) {
    message.error('请上传文件')
    return
  }
  const file = fileList.value[0].raw
  if (file.size > 2 * 1024 * 1024) {
    message.error('导入文件不能超过 2 MB')
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    importResult.value = await FmsSubjectApi.importSubject(accountSetId.value, file)
    if (importResult.value.successSubjectCodes.length > 0) {
      // 发送导入成功的事件
      emit('success')
    }
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formLoading.value = false
  fileList.value = []
  importResult.value = undefined
  nextTick(() => uploadRef.value?.clearFiles())
}

/** 文件数超出提示 */
function handleExceed() {
  message.error('最多只能上传一个文件！')
}

/** 下载模板操作 */
async function importTemplate() {
  templateLoading.value = true
  try {
    const data = await FmsSubjectApi.getSubjectImportTemplate()
    download.excel(data, '科目导入模板.xlsx')
  } finally {
    templateLoading.value = false
  }
}
</script>
