<template>
  <view class="fc-time-picker">
    <wd-form-item
      :title="rule.title"
      :title-width="titleWidth"
      :prop="rule.field"
      :value="displayValue"
      :placeholder="placeholder"
      is-link
      @click="open"
    />
    <wd-datetime-picker
      v-model:visible="visible"
      :model-value="pickerValue"
      type="time"
      :title="placeholder"
      @cancel="emit('cancel')"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed, ref, watch } from 'vue'
import { getPlaceholder } from '../core/utils'

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'cancel': []
  'change': [value: any]
  'close': []
  'confirm': [value: any]
  'open': []
}>()

const visible = ref(false)
const placeholder = computed(() => getPlaceholder(props.rule, '请选择'))
const pickerValue = computed(() => normalizeTimeValue(props.modelValue) || props.rule.props?.defaultValue || '09:00')
const displayValue = computed(() => normalizeTimeValue(props.modelValue))

watch(visible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
  }
})

function open() {
  if (!props.disabled) {
    visible.value = true
  }
}

function handleConfirm({ value }: { value: any }) {
  const nextValue = normalizeTimeValue(value)
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function normalizeTimeValue(value: any) {
  if (value === undefined || value === null || value === '') {
    return ''
  }
  if (value instanceof Date) {
    return value.toTimeString().slice(0, props.rule.props?.showSecond ? 8 : 5)
  }
  return String(value)
}
</script>
