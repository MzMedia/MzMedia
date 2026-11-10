<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1000">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="104px"
    >
      <!-- 模板信息 -->
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" maxlength="64" placeholder="请输入模板名称" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="隐藏空项">
            <el-switch v-model="formData.hideEmpty" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 工资项选择 -->
      <el-form-item label="工资项">
        <SalaryOptionSelect
          ref="optionSelectRef"
          v-model="selectedCodes"
          :disabled-codes="[HrmSalaryOptionCode.REAL_PAY]"
          placeholder="请选择工资条项目"
          @change="handleSelectedCodesChange"
        />
      </el-form-item>
      <!-- 模板明细 -->
      <el-form-item label="模板明细">
        <SalarySlipTemplateOptionEditor
          ref="optionEditorRef"
          v-model="formData.options"
          @remove="handleOptionRemove"
        />
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as SalaryOptionApi from '@/api/hrm/salary/config/option'
import * as SalarySlipTemplateApi from '@/api/hrm/salary/slip/template'
import {
  HrmSalaryOptionCategoryCode,
  HrmSalaryOptionCode,
  HrmSalarySlipTemplateOptionType
} from '@/views/hrm/utils/constants'
import SalaryOptionSelect from '../../config/option/components/SalaryOptionSelect.vue'
import SalarySlipTemplateOptionEditor from './SalarySlipTemplateOptionEditor.vue'

defineOptions({ name: 'HrmSalarySlipTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否显示
const dialogTitle = ref('') // 弹窗标题
const formType = ref('') // 表单类型：create - 新增；update - 修改
const formLoading = ref(false) // 表单加载中
const formData = ref<SalarySlipTemplateApi.SalarySlipTemplateVO>({
  id: undefined,
  name: '',
  hideEmpty: false,
  options: []
}) // 表单数据
const salaryOptionList = ref<SalaryOptionApi.SalaryOptionVO[]>([]) // 可选薪资项
const salaryOptionAllList = ref<SalaryOptionApi.SalaryOptionVO[]>([]) // 全部薪资项
const selectedCodes = ref<number[]>([]) // 已选薪资项编码
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单
const optionSelectRef = ref<InstanceType<typeof SalaryOptionSelect>>() // 薪资项选择器
const optionEditorRef = ref<InstanceType<typeof SalarySlipTemplateOptionEditor>>() // 工资条模板明细

/** 打开表单 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  resetForm()
  await nextTick()
  dialogTitle.value = t('action.' + type)
  formType.value = type
  formLoading.value = true
  try {
    // 1. 初始化薪资项
    salaryOptionAllList.value = (await optionSelectRef.value?.init()) || []
    salaryOptionList.value = salaryOptionAllList.value.filter(
      (item) => item.parentCode !== HrmSalaryOptionCategoryCode.ROOT
    )
    selectedCodes.value = [HrmSalaryOptionCode.REAL_PAY]
    handleSelectedCodesChange(selectedCodes.value)

    // 2. 修改时加载工资条模板
    if (id) {
      formData.value = await SalarySlipTemplateApi.getSalarySlipTemplate(id)
      formData.value.options = (formData.value.options || []).map((item) => ({
        ...item,
        parentCode:
          item.parentCode === HrmSalaryOptionCategoryCode.ROOT ? undefined : item.parentCode
      }))
      selectedCodes.value = (formData.value.options || [])
        .filter((item) => item.type !== HrmSalarySlipTemplateOptionType.CATEGORY)
        .map((item) => item.code)
        .filter((code): code is number => code !== undefined)
      if (!selectedCodes.value.includes(HrmSalaryOptionCode.REAL_PAY)) {
        selectedCodes.value.push(HrmSalaryOptionCode.REAL_PAY)
        handleSelectedCodesChange(selectedCodes.value)
      }
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: [id: number]
}>() // 定义 success 事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  const validateMessage = optionEditorRef.value?.validate()
  if (validateMessage) {
    message.warning(validateMessage)
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    formData.value.options = optionEditorRef.value?.getNormalizedOptions() || []
    if (formType.value === 'update') {
      await SalarySlipTemplateApi.updateSalarySlipTemplate(formData.value)
      message.success(t('common.updateSuccess'))
    } else {
      formData.value.id = await SalarySlipTemplateApi.createSalarySlipTemplate(formData.value)
      message.success(t('common.createSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success', formData.value.id!)
  } finally {
    formLoading.value = false
  }
}

/** 同步已选工资项 */
function handleSelectedCodesChange(codes: number[]) {
  const options = formData.value.options || []
  formData.value.options = options.filter(
    (item) =>
      item.type === HrmSalarySlipTemplateOptionType.CATEGORY ||
      (item.code !== undefined && codes.includes(item.code))
  )
  codes.forEach((code) => {
    if (
      formData.value.options?.some(
        (item) => item.type === HrmSalarySlipTemplateOptionType.ITEM && item.code === code
      )
    ) {
      return
    }
    const salaryOption = salaryOptionList.value.find((item) => item.code === code)
    if (!salaryOption) {
      return
    }
    const categoryCode = salaryOption.parentCode || HrmSalaryOptionCategoryCode.ROOT
    ensureSalaryOptionCategory(categoryCode)
    formData.value.options?.push({
      name: salaryOption.name,
      type: HrmSalarySlipTemplateOptionType.ITEM,
      code: salaryOption.code,
      parentCode: categoryCode,
      hidden: false,
      sort: getNextSort()
    })
  })
}

/** 创建薪资项对应的默认分类 */
function ensureSalaryOptionCategory(categoryCode: number) {
  if (
    !categoryCode ||
    formData.value.options?.some(
      (item) => item.type === HrmSalarySlipTemplateOptionType.CATEGORY && item.code === categoryCode
    )
  ) {
    return
  }
  const category = salaryOptionAllList.value.find((item) => item.code === categoryCode)
  formData.value.options?.push({
    name: category?.name || '其他',
    type: HrmSalarySlipTemplateOptionType.CATEGORY,
    code: categoryCode,
    parentCode: HrmSalaryOptionCategoryCode.ROOT,
    hidden: false,
    sort: getNextSort()
  })
}

/** 同步已删除的薪资项 */
function handleOptionRemove(option: SalarySlipTemplateApi.SalarySlipTemplateOptionVO) {
  if (option.type === HrmSalarySlipTemplateOptionType.ITEM && option.code !== undefined) {
    selectedCodes.value = selectedCodes.value.filter((code) => code !== option.code)
  }
}

/** 获得下一个排序值 */
function getNextSort() {
  return Math.max(0, ...(formData.value.options || []).map((item) => item.sort || 0)) + 1
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    name: '',
    hideEmpty: false,
    options: []
  }
  selectedCodes.value = []
  formType.value = ''
  formRef.value?.resetFields()
}
</script>
