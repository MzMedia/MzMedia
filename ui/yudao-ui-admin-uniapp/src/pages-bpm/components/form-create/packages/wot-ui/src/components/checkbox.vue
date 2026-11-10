<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" center>
    <wd-checkbox-group
      :model-value="Array.isArray(modelValue) ? modelValue : []"
      :disabled="disabled"
      :type="rule.props?.type || 'button'"
      :direction="rule.props?.direction || 'horizontal'"
      @change="handleChange"
    >
      <wd-checkbox
        v-for="option in rule.options"
        :key="String(option.value)"
        :name="option.value"
        :disabled="option.disabled"
      >
        {{ option.label || option.text || option.value }}
      </wd-checkbox>
    </wd-checkbox-group>
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'

defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any[]]
  'change': [value: any[]]
}>()

function handleChange({ value }: { value: any[] }) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>
