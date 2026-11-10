<template>
  <Dialog v-model="dialogVisible" title="凭证附件" width="570px">
    <UploadFile
      v-if="editable || attachmentUrls.length"
      v-model="attachmentUrls"
      :disabled="!editable"
      :file-type="FMS_VOUCHER_ATTACHMENT_FILE_TYPES"
      :limit="100"
    />
    <el-empty v-else description="暂无附件" />
    <template #footer>
      <template v-if="editable">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button :loading="formLoading" type="primary" @click="submitForm">保 存</el-button>
      </template>
      <el-button v-else type="primary" @click="dialogVisible = false">确 定</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsVoucherApi } from '@/api/fms/voucher'
import type { FmsVoucherVO } from '@/api/fms/voucher'
import { checkPermi } from '@/utils/permission'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_VOUCHER_ATTACHMENT_FILE_TYPES, FMS_VOUCHER_STATUS } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsVoucherAttachmentForm' })
const emit = defineEmits<{ success: [] }>()

const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的提交中
const accountSetId = ref(0) // 账套编号
const voucherId = ref(0) // 凭证编号
const attachmentUrls = ref<string[]>([]) // 附件地址列表
const editable = ref(false) // 附件的是否可编辑

/** 打开凭证附件弹窗 */
function open(currentAccountSetId: number, voucher: FmsVoucherVO) {
  // 回显凭证附件，并按凭证状态和权限判断是否可编辑
  accountSetId.value = currentAccountSetId
  voucherId.value = voucher.id
  attachmentUrls.value = [...voucher.attachmentUrls]
  editable.value =
    fmsStore.isAccountSetWritable &&
    voucher.status === FMS_VOUCHER_STATUS.PENDING_REVIEW &&
    !voucher.closingGenerated &&
    checkPermi(['fms:voucher:update'])
  dialogVisible.value = true
}
defineExpose({ open })

/** 提交凭证附件 */
async function submitForm() {
  // 提交附件保存
  formLoading.value = true
  try {
    await FmsVoucherApi.updateVoucherAttachments({
      id: voucherId.value,
      accountSetId: accountSetId.value,
      attachmentUrls: attachmentUrls.value
    })
    message.success('附件保存成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
