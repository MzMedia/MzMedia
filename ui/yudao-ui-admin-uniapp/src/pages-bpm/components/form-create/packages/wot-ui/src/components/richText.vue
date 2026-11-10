<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" layout="vertical">
    <wd-textarea
      :model-value="textValue"
      :placeholder="placeholder"
      :disabled="disabled"
      clearable
      @update:model-value="handleUpdate"
    />
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'
import { getPlaceholder } from '../core/utils'

defineOptions({
  name: 'FcRichText',
})

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
}>()

const placeholder = computed(() => props.rule.props?.placeholder || getPlaceholder(props.rule))
const textValue = computed(() => props.modelValue === undefined || props.modelValue === null ? '' : String(props.modelValue))

function handleUpdate(value: string) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>
