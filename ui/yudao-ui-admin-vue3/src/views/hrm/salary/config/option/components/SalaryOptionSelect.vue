<template>
  <el-select
    v-model="selectedCodes"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    filterable
    multiple
  >
    <el-option
      v-for="option in selectableOptionList"
      :key="option.code"
      :disabled="disabledCodes.includes(option.code)"
      :label="`${option.name} / ${option.code}`"
      :value="option.code"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as SalaryOptionApi from '@/api/hrm/salary/config/option'
import { HrmSalaryOptionCategoryCode } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmSalaryOptionSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number[]
    disabledCodes?: number[]
    adjustable?: boolean
    placeholder?: string
  }>(),
  {
    modelValue: () => [],
    disabledCodes: () => [],
    adjustable: undefined,
    placeholder: '请选择薪资项'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number[]]
  change: [value: number[]]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 薪资项列表的加载中
const optionList = ref<SalaryOptionApi.SalaryOptionVO[]>([]) // 薪资项列表
const selectableOptionList = computed(() =>
  optionList.value.filter((option) => option.parentCode !== HrmSalaryOptionCategoryCode.ROOT)
)
const selectedCodes = computed({
  get: () => props.modelValue,
  set: (value: number[]) => {
    emit('update:modelValue', value)
    emit('change', value)
  }
})

/** 初始化薪资项 */
async function init() {
  if (optionList.value.length === 0) {
    loading.value = true
    try {
      optionList.value = await SalaryOptionApi.getSalaryOptionSimpleList(props.adjustable)
    } finally {
      loading.value = false
    }
  }
  return optionList.value
}
defineExpose({ init }) // 提供 init 方法，用于初始化数据
</script>
