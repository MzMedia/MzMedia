<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="920px">
    <!-- 方案信息 -->
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="方案名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入方案名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="凭证字" prop="voucherWordId">
            <FmsVoucherWordSelect
              v-model="formData.voucherWordId"
              :options="voucherWords"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="10">
          <el-form-item label="来源科目" prop="subjectId">
            <FmsSubjectSelect
              v-model="formData.subjectId"
              :options="subjects"
              class="!w-full"
              placeholder="请选择来源科目"
            />
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="取数规则" prop="formulaRule">
            <el-select v-model="formData.formulaRule" class="!w-full">
              <el-option
                v-for="item in formulaRuleOptions.slice(0, 3)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="时间类型" prop="timeType">
            <el-select v-model="formData.timeType" class="!w-full">
              <el-option
                v-for="item in timeTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="期末结转">
        <el-checkbox v-model="formData.periodEnd">用于期末结账前生成凭证</el-checkbox>
      </el-form-item>
    </el-form>

    <!-- 凭证分录规则 -->
    <div class="mb-10px flex items-center justify-between">
      <span class="font-600">凭证分录规则</span>
      <el-button type="primary" link @click="addSubjectRule()">
        <Icon icon="ep:plus" class="mr-4px" />添加分录
      </el-button>
    </div>
    <el-table :data="formData.subjects" border max-height="360px">
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
    <el-text type="info" class="mt-10px block">
      借方和贷方的金额比例需要分别等于 100%，科目规则随方案保存为 JSON
    </el-text>

    <template #footer>
      <div class="flex justify-between">
        <el-button v-if="formData.id" type="danger" link @click="deleteScheme">删除方案</el-button>
        <span v-else></span>
        <div>
          <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { FmsClosingTemplateVO } from '@/api/fms/closing/template'
import { FmsClosingSchemeApi } from '@/api/fms/closing/scheme'
import type { FmsClosingSchemeSaveReqVO, FmsClosingSchemeVO } from '@/api/fms/closing/scheme'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import {
  FMS_CLOSING_TIME_TYPE,
  FMS_DEBIT_CREDIT_DIRECTION,
  FMS_FORMULA_RULE
} from '@/views/fms/utils/constants'

const formulaRuleOptions = getIntDictOptions(DICT_TYPE.FMS_FORMULA_RULE)
const timeTypeOptions = getIntDictOptions(DICT_TYPE.FMS_CLOSING_TIME_TYPE)

defineOptions({ name: 'FmsClosingSchemeForm' })

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
const dialogTitle = computed(() => (formData.id ? '编辑期末结转方案' : '新增期末结转方案'))
const formData = reactive<FmsClosingSchemeSaveReqVO>(createDefaultForm()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '方案名称不能为空', trigger: 'blur' }],
  voucherWordId: [{ required: true, message: '凭证字不能为空', trigger: 'change' }],
  subjectId: [{ required: true, message: '来源科目不能为空', trigger: 'change' }],
  formulaRule: [{ required: true, message: '取数规则不能为空', trigger: 'change' }],
  timeType: [{ required: true, message: '时间类型不能为空', trigger: 'change' }]
})

/** 创建默认表单数据 */
function createDefaultForm(): FmsClosingSchemeSaveReqVO {
  return {
    id: undefined,
    accountSetId: props.accountSetId,
    name: '',
    periodEnd: true,
    subjectId: undefined,
    formulaRule: FMS_FORMULA_RULE.BALANCE,
    timeType: FMS_CLOSING_TIME_TYPE.PERIOD_END,
    voucherWordId: undefined,
    subjects: []
  }
}

/** 打开弹窗 */
function open(scheme?: FmsClosingSchemeVO, template?: FmsClosingTemplateVO) {
  Object.assign(formData, createDefaultForm())
  formData.subjects = []
  if (scheme) {
    Object.assign(formData, scheme)
    formData.subjects = scheme.subjects.map((item) => ({ ...item }))
  } else if (template) {
    Object.assign(formData, template, {
      id: undefined,
      formulaRule: template.formulaRule ?? FMS_FORMULA_RULE.BALANCE,
      timeType: template.timeType ?? FMS_CLOSING_TIME_TYPE.PERIOD_END,
      voucherWordId:
        props.voucherWords.find((item) => item.defaultStatus)?.id || props.voucherWords[0]?.id
    })
    formData.subjects = template.subjects.map((item) => ({ ...item }))
  } else {
    formData.voucherWordId =
      props.voucherWords.find((item) => item.defaultStatus)?.id || props.voucherWords[0]?.id
    addSubjectRule(FMS_DEBIT_CREDIT_DIRECTION.DEBIT)
    addSubjectRule(FMS_DEBIT_CREDIT_DIRECTION.CREDIT)
  }
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

/** 添加凭证分录规则 */
function addSubjectRule(direction: number = FMS_DEBIT_CREDIT_DIRECTION.DEBIT) {
  formData.subjects.push({
    subjectId: undefined,
    digest: formData.name || '期末结转',
    direction,
    amountRatio: 100
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
  if (Math.abs(debitRatio - 100) > 0.001 || Math.abs(creditRatio - 100) > 0.001) {
    message.warning('借方和贷方的金额比例需要分别等于 100%')
    return
  }
  submitting.value = true
  try {
    if (formData.id) {
      await FmsClosingSchemeApi.updateClosingScheme(formData)
      message.success('修改成功')
    } else {
      await FmsClosingSchemeApi.createClosingScheme(formData)
      message.success('创建成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

/** 删除结账方案 */
async function deleteScheme() {
  if (!formData.id) return
  try {
    await message.delConfirm('确认删除该结账方案吗？')
  } catch {
    return
  }
  await FmsClosingSchemeApi.deleteClosingScheme(props.accountSetId, formData.id)
  message.success('删除成功')
  dialogVisible.value = false
  emit('success')
}

defineExpose({ open })
</script>
