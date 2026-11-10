<template>
  <el-select
    v-model="selectValue"
    :clearable="clearable"
    :disabled="disabled"
    :placeholder="placeholder"
    class="w-full"
  >
    <el-option
      v-for="level in levels"
      :key="level"
      :label="formatHrmPerformanceRaterLevel(raterType, level)"
      :value="level"
    />
  </el-select>
</template>

<script lang="ts" setup>
import { HRM_PERFORMANCE_RATER_MAX_LEVEL } from '@/views/hrm/utils/constants'
import { formatHrmPerformanceRaterLevel } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPerformanceRaterLevelSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number
    raterType?: number
    disabled?: boolean
    clearable?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: false,
    placeholder: '请选择层级'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [value: number | undefined]
}>() // 定义 modelValue 更新和 change 事件

const raterType = computed(() => props.raterType) // 评分人类型
const levels = Array.from({ length: HRM_PERFORMANCE_RATER_MAX_LEVEL }, (_, index) => index + 1) // 评分人层级选项
const selectValue = computed({
  get: () => props.modelValue,
  set: (value: number | undefined) => {
    emit('update:modelValue', value)
    emit('change', value)
  }
})
</script>
