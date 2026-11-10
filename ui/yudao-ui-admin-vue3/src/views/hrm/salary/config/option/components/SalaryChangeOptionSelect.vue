<template>
  <el-select
    v-model="selectedCodes"
    :disabled="disabled"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    filterable
    multiple
  >
    <el-option
      v-for="option in optionList"
      :key="option.code"
      :label="`${option.name} / ${option.code}`"
      :value="option.code"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as SalaryChangeTemplateApi from '@/api/hrm/salary/config/change-template'
import * as SalaryOptionApi from '@/api/hrm/salary/config/option'

defineOptions({ name: 'HrmSalaryChangeOptionSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: SalaryChangeTemplateApi.HrmSalaryChangeOptionVO[]
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    modelValue: () => [],
    disabled: false,
    placeholder: '请选择调薪项'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: SalaryChangeTemplateApi.HrmSalaryChangeOptionVO[]]
}>() // 定义 modelValue 更新事件

const loading = ref(false) // 调薪项列表的加载中
const optionList = ref<SalaryOptionApi.SalaryOptionVO[]>([]) // 调薪项列表

/** 已选调薪项编码 */
const selectedCodes = computed({
  get: () => props.modelValue.map((option) => option.code),
  set: (codes: number[]) => {
    emit(
      'update:modelValue',
      codes.map((code) => {
        const selectedOption = props.modelValue.find((option) => option.code === code)
        const option = optionList.value.find((item) => item.code === code)
        return {
          code,
          name: option?.name || selectedOption?.name || ''
        }
      })
    )
  }
})

/** 初始化调薪项 */
async function init(selectAll: boolean) {
  if (optionList.value.length === 0) {
    loading.value = true
    try {
      optionList.value = await SalaryOptionApi.getSalaryOptionSimpleList(true)
    } finally {
      loading.value = false
    }
  }
  if (selectAll) {
    selectedCodes.value = optionList.value.map((option) => option.code)
  }
}
defineExpose({ init }) // 提供 init 方法，用于初始化调薪项
</script>
