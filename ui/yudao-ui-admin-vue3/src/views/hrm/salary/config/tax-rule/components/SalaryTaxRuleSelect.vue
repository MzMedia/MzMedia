<template>
  <el-select
    v-model="selectValue"
    :clearable="clearable"
    :disabled="disabled"
    :filterable="filterable"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    @change="handleChange"
  >
    <el-option
      v-for="taxRule in taxRuleOptions"
      :key="taxRule.id"
      :label="taxRule.name"
      :value="taxRule.id"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as SalaryTaxRuleApi from '@/api/hrm/salary/config/tax-rule'

defineOptions({ name: 'HrmSalaryTaxRuleSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number
    disabled?: boolean
    clearable?: boolean
    filterable?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    filterable: true,
    placeholder: '请选择计税规则'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [taxRule: SalaryTaxRuleApi.SalaryTaxRuleVO | undefined]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 计税规则列表的加载中
const taxRuleOptions = ref<(SalaryTaxRuleApi.SalaryTaxRuleVO & { id: number })[]>([]) // 计税规则列表

/** 当前选中的计税规则 */
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

/** 选中变化 */
function handleChange(value: number | undefined) {
  emit(
    'change',
    taxRuleOptions.value.find((taxRule) => taxRule.id === value)
  )
}

/** 获得计税规则列表 */
async function getTaxRuleList() {
  loading.value = true
  try {
    const data = await SalaryTaxRuleApi.getSalaryTaxRuleList()
    taxRuleOptions.value = data.filter(
      (taxRule): taxRule is SalaryTaxRuleApi.SalaryTaxRuleVO & { id: number } =>
        taxRule.id !== undefined
    )
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getTaxRuleList()
})
</script>
