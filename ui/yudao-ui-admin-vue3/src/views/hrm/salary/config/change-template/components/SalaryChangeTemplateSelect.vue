<template>
  <el-select
    :model-value="modelValue"
    :clearable="clearable"
    :disabled="disabled"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    filterable
    @update:model-value="handleChange"
  >
    <el-option
      v-for="template in templateList"
      :key="template.id"
      :label="template.name"
      :value="template.id as number"
    >
      <span>{{ template.name }}</span>
      <el-tag v-if="template.defaultStatus" type="success" size="small" class="ml-8px">
        默认
      </el-tag>
    </el-option>
  </el-select>
</template>

<script lang="ts" setup>
import * as SalaryChangeTemplateApi from '@/api/hrm/salary/config/change-template'

defineOptions({ name: 'HrmSalaryChangeTemplateSelect' })

withDefaults(
  defineProps<{
    modelValue?: number
    disabled?: boolean
    clearable?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    placeholder: '全部薪资项'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [value: number | undefined]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 调薪模板列表的加载中
const templateList = ref<SalaryChangeTemplateApi.HrmSalaryChangeTemplateVO[]>([]) // 调薪模板列表

/** 选中变化 */
function handleChange(value?: number) {
  emit('update:modelValue', value)
  emit('change', value)
}

/** 初始化调薪模板 */
async function init() {
  if (templateList.value.length === 0) {
    loading.value = true
    try {
      templateList.value = await SalaryChangeTemplateApi.getSalaryChangeTemplateList()
    } finally {
      loading.value = false
    }
  }
  return templateList.value
}
defineExpose({ init }) // 提供 init 方法，用于初始化数据
</script>
