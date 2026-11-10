<template>
  <Dialog v-model="dialogVisible" title="结转损益参数设置" width="680px">
    <!-- 结转损益参数 -->
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="230px">
      <el-form-item label="凭证日期" prop="closingDay">
        <el-date-picker
          v-model="voucherDate"
          type="date"
          value-format="YYYY-MM-DD"
          format="YYYY年MM月DD日"
          :clearable="false"
          :disabled-date="disabledDate"
        />
      </el-form-item>
      <el-form-item label="凭证字" prop="voucherWordId">
        <FmsVoucherWordSelect
          v-model="formData.voucherWordId"
          :options="voucherWords"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item label="凭证摘要" prop="digest">
        <el-input v-model="formData.digest" class="!w-360px" placeholder="请输入凭证摘要" />
      </el-form-item>
      <el-form-item label="凭证分类" prop="voucherType">
        <el-radio-group v-model="formData.voucherType" class="flex flex-col items-start">
          <el-radio v-for="item in voucherTypeOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="“以前年度损益调整”科目" prop="priorYearAdjustmentSubjectId">
        <FmsSubjectSelect
          v-model="formData.priorYearAdjustmentSubjectId"
          :options="profitLossSubjects"
          class="!w-360px"
          placeholder="请选择科目"
        />
      </el-form-item>
      <el-form-item label="“以前年度损益调整”结转科目" prop="adjustmentClosingSubjectId">
        <FmsSubjectSelect
          v-model="formData.adjustmentClosingSubjectId"
          :options="closingSubjects"
          class="!w-360px"
          placeholder="请选择科目"
        />
      </el-form-item>
      <el-form-item label="其他损益科目的结转科目" prop="otherClosingSubjectId">
        <FmsSubjectSelect
          v-model="formData.otherClosingSubjectId"
          :options="closingSubjects"
          class="!w-360px"
          placeholder="请选择科目"
        />
      </el-form-item>
      <el-form-item label-width="0">
        <el-checkbox v-model="formData.reverseBalance">
          结转方式：按余额反向结转
          <el-tooltip placement="top">
            <template #content>
              选中时按科目实际余额的相反方向结转<br />
              未选中时按科目属性中定义的余额方向反向结转
            </template>
            <Icon icon="ep:question-filled" class="ml-4px" />
          </el-tooltip>
        </el-checkbox>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { FmsClosingSchemeApi } from '@/api/fms/closing/scheme'
import type { FmsClosingSchemeVO, FmsProfitLossSettingsVO } from '@/api/fms/closing/scheme'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import { FMS_CLOSING_VOUCHER_TYPE, FMS_SUBJECT_TYPE } from '@/views/fms/utils/constants'

const voucherTypeOptions = getIntDictOptions(DICT_TYPE.FMS_CLOSING_VOUCHER_TYPE)

defineOptions({ name: 'FmsProfitLossSettingsForm' })

const props = defineProps<{
  accountSetId: number
  month: string
  subjects: FmsSubjectVO[]
  voucherWords: FmsVoucherWordVO[]
}>()
const emit = defineEmits<{ success: [] }>()
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const submitting = ref(false) // 表单的提交中
const formRef = ref<FormInstance>() // 表单 Ref
const voucherDate = ref('') // 凭证日期
const profitLossSubjects = computed(() =>
  props.subjects.filter((item) => item.type === FMS_SUBJECT_TYPE.PROFIT_LOSS)
) // 损益类科目列表
const closingSubjects = computed(() =>
  props.subjects.filter((item) => item.type !== FMS_SUBJECT_TYPE.PROFIT_LOSS)
) // 非损益类科目列表
const formData = reactive<FmsProfitLossSettingsVO>(createDefaultForm()) // 表单数据
const formRules = reactive<FormRules>({
  voucherWordId: [{ required: true, message: '凭证字不能为空', trigger: 'change' }],
  digest: [{ required: true, message: '凭证摘要不能为空', trigger: 'blur' }],
  voucherType: [{ required: true, message: '凭证分类不能为空', trigger: 'change' }],
  priorYearAdjustmentSubjectId: [
    { required: true, message: '以前年度损益调整科目不能为空', trigger: 'change' }
  ],
  adjustmentClosingSubjectId: [
    { required: true, message: '以前年度损益调整结转科目不能为空', trigger: 'change' }
  ],
  otherClosingSubjectId: [
    { required: true, message: '其他损益结转科目不能为空', trigger: 'change' }
  ]
})

/** 创建默认表单数据 */
function createDefaultForm(): FmsProfitLossSettingsVO {
  return {
    accountSetId: props.accountSetId,
    voucherWordId: undefined,
    digest: '结转损益',
    voucherType: FMS_CLOSING_VOUCHER_TYPE.COMBINED_GAIN_AND_LOSS,
    priorYearAdjustmentSubjectId: undefined,
    adjustmentClosingSubjectId: undefined,
    otherClosingSubjectId: undefined,
    reverseBalance: true,
    closingDay: 31
  }
}

/** 打开弹窗 */
function open(settings?: FmsClosingSchemeVO) {
  Object.assign(formData, createDefaultForm())
  formData.voucherWordId =
    props.voucherWords.find((item) => item.defaultStatus)?.id || props.voucherWords[0]?.id
  formData.priorYearAdjustmentSubjectId = props.subjects.find((item) => item.code === '6000')?.id
  formData.adjustmentClosingSubjectId = props.subjects.find((item) => item.code === '310415')?.id
  formData.otherClosingSubjectId = props.subjects.find((item) => item.code === '3103')?.id
  if (settings) {
    const legacySettings = !settings.priorYearAdjustmentSubjectId
    Object.assign(formData, {
      voucherWordId: settings.voucherWordId || formData.voucherWordId,
      digest: settings.digest || formData.digest,
      voucherType: settings.voucherType ?? formData.voucherType,
      priorYearAdjustmentSubjectId:
        settings.priorYearAdjustmentSubjectId || formData.priorYearAdjustmentSubjectId,
      adjustmentClosingSubjectId:
        settings.adjustmentClosingSubjectId || formData.adjustmentClosingSubjectId,
      otherClosingSubjectId: settings.otherClosingSubjectId || formData.otherClosingSubjectId,
      reverseBalance: legacySettings
        ? formData.reverseBalance
        : (settings.reverseBalance ?? formData.reverseBalance),
      closingDay: settings.closingDay || formData.closingDay
    })
  }
  const closingDay = Math.min(formData.closingDay || 31, dayjs(`${props.month}-01`).daysInMonth())
  voucherDate.value = dayjs(`${props.month}-${String(closingDay).padStart(2, '0')}`).format(
    'YYYY-MM-DD'
  )
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

/** 提交表单 */
async function submitForm() {
  await formRef.value?.validate()
  if (!voucherDate.value) {
    message.warning('凭证日期不能为空')
    return
  }
  formData.closingDay = dayjs(voucherDate.value).date()
  submitting.value = true
  try {
    await FmsClosingSchemeApi.saveProfitLossSettings(formData)
    message.success('保存成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

/** 禁用非当前会计期间的日期 */
function disabledDate(date: Date) {
  return dayjs(date).format('YYYY-MM') !== props.month
}

defineExpose({ open })
</script>
