<template>
  <Dialog v-model="dialogVisible" title="整理凭证" width="500px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="88px"
    >
      <el-form-item label="整理范围" prop="month">
        <el-date-picker
          v-model="formData.month"
          :clearable="false"
          class="!w-1/1"
          placeholder="请选择月份"
          type="month"
          value-format="YYYY-MM"
        />
      </el-form-item>
      <el-form-item label="凭证字" prop="voucherWordId">
        <FmsVoucherWordSelect
          v-model="formData.voucherWordId"
          :options="voucherWords"
          class="!w-1/1"
        />
      </el-form-item>
      <el-form-item label="起始编号" prop="startNumber">
        <el-input-number v-model="formData.startNumber" :controls="false" :min="1" class="!w-1/1" />
      </el-form-item>
      <el-form-item label-width="20px" prop="type">
        <el-radio-group v-model="formData.type" class="flex flex-col items-start">
          <el-radio :value="FMS_VOUCHER_TIDY_TYPE.FILL_GAPS"> 按凭证号顺次前移补齐断号 </el-radio>
          <el-radio :value="FMS_VOUCHER_TIDY_TYPE.REORDER_BY_TIME">
            按凭证日期重新顺次编号
          </el-radio>
        </el-radio-group>
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
import { FmsVoucherApi } from '@/api/fms/voucher'
import type { FmsVoucherTidyReqVO } from '@/api/fms/voucher'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import { FMS_VOUCHER_TIDY_TYPE } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsVoucherTidyForm' })
const emit = defineEmits<{ success: [] }>()

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的提交中
const voucherWords = ref<FmsVoucherWordVO[]>([]) // 凭证字列表
const formData = ref<FmsVoucherTidyReqVO>({
  accountSetId: 0,
  month: '',
  voucherWordId: undefined,
  startNumber: 1,
  type: FMS_VOUCHER_TIDY_TYPE.FILL_GAPS
}) // 表单数据
const formRules = reactive<FormRules>({
  month: [{ required: true, message: '请选择整理范围', trigger: 'change' }],
  voucherWordId: [{ required: true, message: '请选择凭证字', trigger: 'change' }],
  startNumber: [{ required: true, message: '请输入起始编号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择整理方式', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开凭证整理弹窗 */
function open(accountSetId: number, defaultMonth: string, words: FmsVoucherWordVO[]) {
  dialogVisible.value = true
  voucherWords.value = words
  resetForm(accountSetId, defaultMonth, words)
}
defineExpose({ open })

/** 提交凭证整理 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 整理凭证
  formLoading.value = true
  try {
    await FmsVoucherApi.tidyVoucher(formData.value)
    message.success('整理成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(accountSetId = 0, month = '', words: FmsVoucherWordVO[] = []) {
  formRef.value?.resetFields()
  formData.value = {
    accountSetId,
    month,
    voucherWordId: words.find((item) => item.defaultStatus)?.id || words[0]?.id,
    startNumber: 1,
    type: FMS_VOUCHER_TIDY_TYPE.FILL_GAPS
  }
}
</script>
