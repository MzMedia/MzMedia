<template>
  <Dialog v-model="dialogVisible" title="移动凭证" width="480px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="88px"
    >
      <el-form-item label="期间" prop="month">
        <el-date-picker
          v-model="formData.month"
          :clearable="false"
          class="!w-1/1"
          placeholder="请选择期间"
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
      <el-form-item label="移动规则" prop="sourceNumber">
        <div class="flex items-center whitespace-nowrap">
          <span>将上述期间的：</span>
          <el-input-number
            v-model="formData.sourceNumber"
            :controls="false"
            :min="1"
            class="mx-5px !w-58px"
            @blur="formRef?.validateField('sourceNumber')"
          />
          <span>号移动到：</span>
          <el-input-number
            v-model="formData.targetNumber"
            :controls="false"
            :min="1"
            class="mx-5px !w-58px"
          />
          <span>号之前</span>
        </div>
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
import type { FmsVoucherMoveReqVO } from '@/api/fms/voucher'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'

defineOptions({ name: 'FmsVoucherMoveForm' })
const emit = defineEmits<{ success: [] }>()

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的提交中
const voucherWords = ref<FmsVoucherWordVO[]>([]) // 凭证字列表
const formData = ref<FmsVoucherMoveReqVO>(createDefault()) // 表单数据
const formRules = reactive<FormRules>({
  month: [{ required: true, message: '请选择期间', trigger: 'change' }],
  voucherWordId: [{ required: true, message: '请选择凭证字', trigger: 'change' }],
  sourceNumber: [
    {
      validator: (_rule, value, callback) => {
        if (!value || !formData.value.targetNumber) {
          callback(new Error('请输入完整的移动规则'))
          return
        }
        if (formData.value.targetNumber >= value) {
          callback(new Error('移动到的凭证号必须小于原凭证号'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开凭证移动弹窗 */
function open(accountSetId: number, defaultMonth: string, words: FmsVoucherWordVO[]) {
  dialogVisible.value = true
  voucherWords.value = words
  resetForm(accountSetId, defaultMonth, words)
}
defineExpose({ open })

/** 提交凭证移动 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 移动凭证
  formLoading.value = true
  try {
    await FmsVoucherApi.moveVoucher(formData.value)
    message.success('移动成功')
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
  formData.value = createDefault(accountSetId, month, words)
}

/** 创建默认凭证移动参数 */
function createDefault(
  accountSetId = 0,
  month = '',
  words: FmsVoucherWordVO[] = []
): FmsVoucherMoveReqVO {
  return {
    accountSetId,
    month,
    voucherWordId: words.find((item) => item.defaultStatus)?.id || words[0]?.id,
    sourceNumber: undefined,
    targetNumber: undefined
  }
}
</script>
