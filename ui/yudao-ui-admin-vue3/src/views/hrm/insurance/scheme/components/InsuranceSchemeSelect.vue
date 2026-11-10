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
      v-for="scheme in schemeList"
      :key="scheme.id"
      :label="scheme.name"
      :value="scheme.id"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as InsuranceSchemeApi from '@/api/hrm/insurance/scheme'

defineOptions({ name: 'HrmInsuranceSchemeSelect' })

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
    placeholder: '请选择参保方案'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [scheme: InsuranceSchemeApi.InsuranceSchemeVO | undefined]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 参保方案列表的加载中
const schemeList = ref<(InsuranceSchemeApi.InsuranceSchemeVO & { id: number })[]>([]) // 参保方案列表
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

/** 选中变化 */
function handleChange(value: number | undefined) {
  emit(
    'change',
    schemeList.value.find((scheme) => scheme.id === value)
  )
}

/** 获得参保方案列表 */
async function getSchemeList() {
  loading.value = true
  try {
    const data = await InsuranceSchemeApi.getInsuranceSchemeSimpleList()
    schemeList.value = data.filter(
      (scheme): scheme is InsuranceSchemeApi.InsuranceSchemeVO & { id: number } =>
        scheme.id !== undefined
    )
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getSchemeList()
})
</script>
