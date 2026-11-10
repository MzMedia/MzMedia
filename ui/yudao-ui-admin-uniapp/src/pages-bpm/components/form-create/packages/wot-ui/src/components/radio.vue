<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" center>
    <wd-radio-group
      :model-value="modelValue"
      :disabled="disabled"
      :type="rule.props?.type || 'button'"
      :direction="rule.props?.direction || 'horizontal'"
      @change="handleChange"
    >
      <wd-radio
        v-for="option in rule.options"
        :key="String(option.value)"
        :value="option.value"
        :disabled="option.disabled"
      >
        {{ option.label || option.text || option.value }}
      </wd-radio>
    </wd-radio-group>
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
  'update:modelValue': [value: any]
  'change': [value: any]
}>()

function handleChange({ value }: { value: any }) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>
