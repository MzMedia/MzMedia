<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="920px">
    <!-- 结账模板表单 -->
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="模板名称" prop="name">
            <el-input v-model="formData.name" maxlength="255" placeholder="请输入模板名称" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="模板分类" prop="category">
            <el-select v-model="formData.category" class="!w-full">
              <el-option
                v-for="item in categoryOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="显示顺序" prop="sort">
            <el-input-number v-model="formData.sort" :min="0" :controls="false" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="10">
          <el-form-item label="来源科目">
            <FmsSubjectSelect
              v-model="formData.subjectId"
              :options="subjects"
              clearable
              class="!w-full"
              placeholder="可在使用模板时补充"
            />
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="取数规则">
            <el-select v-model="formData.formulaRule" clearable class="!w-full">
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
          <el-form-item label="时间类型">
            <el-select v-model="formData.timeType" clearable class="!w-full">
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
      <el-table-column label="操作" align="center" width="70">
        <template #default="{ $index }">
          <el-button type="danger" link @click="formData.subjects.splice($index, 1)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { FmsClosingTemplateApi, type FmsClosingTemplateVO } from '@/api/fms/closing/template'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import {
  FMS_CLOSING_TEMPLATE_CATEGORY,
  FMS_DEBIT_CREDIT_DIRECTION
} from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsClosingTemplateForm' })

const props = defineProps<{
  accountSetId: number // 账套编号
  subjects: FmsSubjectVO[] // 末级科目列表
}>()
const emit = defineEmits<{ success: [] }>()
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const submitting = ref(false) // 表单的提交中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formRef = ref<FormInstance>() // 表单 Ref
const categoryOptions = getIntDictOptions(DICT_TYPE.FMS_CLOSING_TEMPLATE_CATEGORY) // 模板分类选项
const formulaRuleOptions = getIntDictOptions(DICT_TYPE.FMS_FORMULA_RULE)
const timeTypeOptions = getIntDictOptions(DICT_TYPE.FMS_CLOSING_TIME_TYPE)
const formData = reactive<FmsClosingTemplateVO>(createDefaultForm()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '模板分类不能为空', trigger: 'change' }],
  sort: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }]
})

/** 创建默认表单数据 */
function createDefaultForm(): FmsClosingTemplateVO {
  return {
    id: undefined,
    accountSetId: props.accountSetId,
    presetCode: undefined,
    name: '',
    category: FMS_CLOSING_TEMPLATE_CATEGORY.DAILY_EXPENSE,
    periodEnd: true,
    subjects: [],
    sort: 0,
    createTime: undefined
  }
}

/** 打开弹窗 */
function open(
  type: string,
  template?: FmsClosingTemplateVO,
  category: number = FMS_CLOSING_TEMPLATE_CATEGORY.DAILY_EXPENSE
) {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增结账模板' : '编辑结账模板'
  formType.value = type
  resetForm()
  Object.assign(formData, template || { category })
  formData.subjects = template?.subjects.map((item) => ({ ...item })) || []
  if (!template) {
    addSubjectRule(FMS_DEBIT_CREDIT_DIRECTION.DEBIT)
    addSubjectRule(FMS_DEBIT_CREDIT_DIRECTION.CREDIT)
  }
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
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
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
    if (formType.value === 'update') {
      await FmsClosingTemplateApi.updateClosingTemplate(formData)
      message.success('修改成功')
    } else {
      await FmsClosingTemplateApi.createClosingTemplate(formData)
      message.success('新增成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

/** 重置表单 */
function resetForm() {
  Object.assign(formData, createDefaultForm())
  formRef.value?.resetFields()
}

defineExpose({ open })
</script>
