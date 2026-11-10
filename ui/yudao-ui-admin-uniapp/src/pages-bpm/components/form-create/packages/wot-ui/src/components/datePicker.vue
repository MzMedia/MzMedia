<template>
  <view class="fc-date-picker">
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
      :type="pickerType"
      :title="placeholder"
      @cancel="emit('cancel')"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import dayjs from 'dayjs'
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
const pickerType = computed(() => props.rule.props?.type || 'date')
const valueFormat = computed(() => props.rule.props?.valueFormat || props.rule.props?.format)
const pickerValue = computed(() => normalizePickerValue(props.modelValue) ?? Date.now())
const displayValue = computed(() => {
  const value = normalizePickerValue(props.modelValue)
  if (value === undefined) {
    return ''
  }
  return dayjs(value).format(getDefaultFormat())
})

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
  const nextValue = formatSubmitValue(value)
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function normalizePickerValue(value: any): number | undefined {
  if (value === undefined || value === null || value === '') {
    return undefined
  }
  if (typeof value === 'number' && Number.isFinite(value)) {
    return normalizeNumericTimestamp(value)
  }
  if (value instanceof Date) {
    return value.getTime()
  }
  const parsedNumber = Number(value)
  if (Number.isFinite(parsedNumber) && String(value).trim() !== '') {
    return normalizeNumericTimestamp(parsedNumber)
  }
  const parsedDate = dayjs(value)
  return parsedDate.isValid() ? parsedDate.valueOf() : undefined
}

function normalizeNumericTimestamp(value: number) {
  if (valueFormat.value === 'X') {
    return value * 1000
  }
  if (valueFormat.value === 'x' || valueFormat.value === 'timestamp' || valueFormat.value === 'number') {
    return value
  }
  return value < 10000000000 ? value * 1000 : value
}

function formatSubmitValue(value: any) {
  if (value === undefined || value === null || value === '') {
    return undefined
  }
  const timestamp = normalizePickerValue(value)
  if (timestamp === undefined) {
    return value
  }
  if (valueFormat.value === 'timestamp' || valueFormat.value === 'number' || valueFormat.value === 'x') {
    return timestamp
  }
  if (valueFormat.value === 'X') {
    return Math.floor(timestamp / 1000)
  }
  if (typeof valueFormat.value === 'string' && valueFormat.value.trim()) {
    return dayjs(timestamp).format(valueFormat.value)
  }
  return dayjs(timestamp).format(getDefaultFormat())
}

function getDefaultFormat() {
  if (pickerType.value === 'datetime') {
    return props.rule.props?.hideSecond ? 'YYYY-MM-DD HH:mm' : 'YYYY-MM-DD HH:mm:ss'
  }
  if (pickerType.value === 'year-month') {
    return 'YYYY-MM'
  }
  if (pickerType.value === 'year') {
    return 'YYYY'
  }
  return 'YYYY-MM-DD'
}
</script>
