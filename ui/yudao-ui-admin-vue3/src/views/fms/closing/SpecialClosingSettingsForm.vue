<template>
  <Dialog v-model="dialogVisible" :title="`编辑${schemeName}`" width="880px">
    <!-- 凭证字 -->
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
      <el-form-item label="凭证字" prop="voucherWordId">
        <FmsVoucherWordSelect
          v-model="formData.voucherWordId"
          :options="voucherWords"
          class="!w-260px"
        />
      </el-form-item>
    </el-form>

    <!-- 凭证分录规则 -->
    <div class="mb-10px flex items-center justify-between">
      <span class="font-600">凭证分录规则</span>
      <el-button type="primary" link @click="addSubjectRule()">
        <Icon icon="ep:plus" class="mr-4px" />添加分录
      </el-button>
    </div>
    <el-table :data="formData.subjects" border max-height="420px">
      <el-table-column label="摘要" min-width="180">
        <template #default="{ row }">
          <el-input v-model="row.digest" placeholder="请输入摘要" />
        </template>
      </el-table-column>
      <el-table-column label="借/贷" width="105">
        <template #default="{ row }">
          <el-select v-model="row.direction">
            <el-option label="借" :value="FMS_DEBIT_CREDIT_DIRECTION.DEBIT" />
            <el-option label="贷" :value="FMS_DEBIT_CREDIT_DIRECTION.CREDIT" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="科目" min-width="280">
        <template #default="{ row }">
          <FmsSubjectSelect v-model="row.subjectId" :options="subjects" class="!w-full" />
        </template>
      </el-table-column>
      <el-table-column label="金额比例%" width="130">
        <template #default="{ row }">
          <el-input-number
            v-model="row.amountRatio"
            :min="0.01"
            :max="100"
            :controls="false"
            :precision="2"
            class="!w-full"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="70" align="center">
        <template #default="{ $index }">
          <el-button type="danger" link @click="formData.subjects.splice($index, 1)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-text type="info" class="mt-10px block">{{ ratioTip }}</el-text>

    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { FmsClosingSchemeApi } from '@/api/fms/closing/scheme'
import type { FmsClosingSchemeVO, FmsSpecialClosingSettingsVO } from '@/api/fms/closing/scheme'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import { FMS_CLOSING_TYPE, FMS_DEBIT_CREDIT_DIRECTION } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsSpecialClosingSettingsForm' })

const props = defineProps<{
  accountSetId: number
  subjects: FmsSubjectVO[]
  voucherWords: FmsVoucherWordVO[]
}>()
const emit = defineEmits<{ success: [] }>()
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const submitting = ref(false) // 表单的提交中
const formRef = ref<FormInstance>() // 表单 Ref
const schemeName = ref('专用结转') // 方案名称
const schemeType = ref<number>(FMS_CLOSING_TYPE.UNPAID_VAT) // 方案类型
const formData = reactive<FmsSpecialClosingSettingsVO>({
  id: 0,
  accountSetId: props.accountSetId,
  voucherWordId: 0,
  subjects: []
}) // 表单数据
const formRules = reactive<FormRules>({
  voucherWordId: [{ required: true, message: '凭证字不能为空', trigger: 'change' }]
})
const ratioTip = computed(() =>
  schemeType.value === FMS_CLOSING_TYPE.UNPAID_VAT
    ? '转出未交增值税的借方和贷方比例必须分别等于 100%'
    : '借方和贷方比例必须相等，该比例同时作为本方案的计提税率'
)

/** 打开弹窗 */
function open(scheme: FmsClosingSchemeVO) {
  schemeName.value = scheme.name
  schemeType.value = scheme.type
  Object.assign(formData, {
    id: scheme.id,
    accountSetId: props.accountSetId,
    voucherWordId: scheme.voucherWordId,
    subjects: scheme.subjects.map((item) => ({ ...item }))
  })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

/** 添加凭证分录规则 */
function addSubjectRule(direction: number = FMS_DEBIT_CREDIT_DIRECTION.DEBIT) {
  formData.subjects.push({
    subjectId: undefined,
    digest: schemeName.value,
    direction,
    amountRatio: schemeType.value === FMS_CLOSING_TYPE.UNPAID_VAT ? 100 : 1
  })
}

/** 提交表单 */
async function submitForm() {
  await formRef.value?.validate()
  if (
    formData.subjects.length < 2 ||
    formData.subjects.some((item) => !item.digest || !item.subjectId)
  ) {
    message.warning('请完整填写至少两条凭证分录规则')
    return
  }
  const debitRatio = formData.subjects
    .filter((item) => item.direction === FMS_DEBIT_CREDIT_DIRECTION.DEBIT)
    .reduce((sum, item) => sum + Number(item.amountRatio), 0)
  const creditRatio = formData.subjects
    .filter((item) => item.direction === FMS_DEBIT_CREDIT_DIRECTION.CREDIT)
    .reduce((sum, item) => sum + Number(item.amountRatio), 0)
  if (
    debitRatio <= 0 ||
    debitRatio > 100 ||
    Math.abs(debitRatio - creditRatio) > 0.001 ||
    (schemeType.value === FMS_CLOSING_TYPE.UNPAID_VAT && Math.abs(debitRatio - 100) > 0.001)
  ) {
    message.warning(ratioTip.value)
    return
  }
  submitting.value = true
  try {
    await FmsClosingSchemeApi.updateSpecialClosingSettings(formData)
    message.success('保存成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
