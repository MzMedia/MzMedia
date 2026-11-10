<template>
  <view class="fc-calendar">
    <wd-form-item
      :title="rule.title"
      :title-width="titleWidth"
      :prop="rule.field"
      :value="displayValue"
      :placeholder="placeholder"
      :is-link="!disabled"
      @click="open"
    />

    <wd-calendar
      v-model:visible="visible"
      :model-value="calendarValue"
      :type="calendarType"
      :title="rule.title || placeholder"
      :min-date="minDate"
      :max-date="maxDate"
      v-bind="componentProps"
      @cancel="emit('cancel')"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { CalendarType } from '@wot-ui/ui/components/wd-calendar-view/types'
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
const calendarType = computed(() => normalizeCalendarType(props.rule.props?.type || props.rule.type))
const valueFormat = computed(() => props.rule.props?.valueFormat || props.rule.props?.format)
const calendarValue = computed(() => normalizeCalendarValue(props.modelValue))
const minDate = computed(() => normalizeTimestamp(props.rule.props?.minDate || props.rule.props?.min, valueFormat.value))
const maxDate = computed(() => normalizeTimestamp(props.rule.props?.maxDate || props.rule.props?.max, valueFormat.value))
const componentProps = computed(() => getComponentProps())
const displayValue = computed(() => formatDisplayValue(props.modelValue))

watch(visible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
  }
})

function open() {
  if (props.disabled) {
    return
  }
  visible.value = true
}

function handleConfirm({ value }: { value: number | number[] | null }) {
  const nextValue = formatSubmitValue(value)
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function normalizeCalendarType(type: any): CalendarType {
  const typeMap: Record<string, CalendarType> = {
    date: 'date',
    dates: 'dates',
    datetime: 'datetime',
    datetimeRange: 'datetimerange',
    datetimerange: 'datetimerange',
    dateRange: 'daterange',
    daterange: 'daterange',
    month: 'month',
    monthRange: 'monthrange',
    monthrange: 'monthrange',
    multiple: 'dates',
    range: 'daterange',
    week: 'week',
    weekRange: 'weekrange',
    weekrange: 'weekrange',
  }
  return typeMap[String(type || 'date')] || 'date'
}

function normalizeCalendarValue(value: any): number | number[] | null {
  if (Array.isArray(value)) {
    const values = value.map(item => normalizeTimestamp(item, valueFormat.value)).filter((item): item is number => item !== undefined)
    return values.length ? values : null
  }
  return normalizeTimestamp(value, valueFormat.value) ?? null
}

function normalizeTimestamp(value: any, format?: string): number | undefined {
  if (value === undefined || value === null || value === '') {
    return undefined
  }
  if (typeof value === 'number' && Number.isFinite(value)) {
    return normalizeNumericTimestamp(value, format)
  }
  if (value instanceof Date) {
    return value.getTime()
  }
  const parsedNumber = Number(value)
  if (Number.isFinite(parsedNumber) && String(value).trim() !== '') {
    return normalizeNumericTimestamp(parsedNumber, format)
  }
  const parsedDate = dayjs(value)
  return parsedDate.isValid() ? parsedDate.valueOf() : undefined
}

function normalizeNumericTimestamp(value: number, format?: string) {
  if (format === 'X') {
    return value * 1000
  }
  if (format === 'x' || format === 'timestamp' || format === 'number') {
    return value
  }
  return value < 10000000000 ? value * 1000 : value
}

function formatSubmitValue(value: number | number[] | null) {
  if (Array.isArray(value)) {
    return value.map(formatTimestampValue)
  }
  if (value === undefined || value === null) {
    return undefined
  }
  return formatTimestampValue(value)
}

function formatTimestampValue(value: number) {
  if (valueFormat.value === 'timestamp' || valueFormat.value === 'number' || valueFormat.value === 'x') {
    return value
  }
  if (valueFormat.value === 'X') {
    return Math.floor(value / 1000)
  }
  if (typeof valueFormat.value === 'string' && valueFormat.value.trim()) {
    return dayjs(value).format(valueFormat.value)
  }
  return dayjs(value).format(getDefaultFormat())
}

function formatDisplayValue(value: any) {
  const normalizedValue = normalizeCalendarValue(value)
  if (Array.isArray(normalizedValue)) {
    if (normalizedValue.length === 0) {
      return ''
    }
    if (isRangeType(calendarType.value)) {
      return normalizedValue.map(item => dayjs(item).format(getDefaultFormat())).join(' - ')
    }
    return `选择了 ${normalizedValue.length} 个日期`
  }
  if (!normalizedValue) {
    return ''
  }
  return dayjs(normalizedValue).format(getDefaultFormat())
}

function getDefaultFormat() {
  switch (calendarType.value) {
    case 'datetime':
    case 'datetimerange':
      return props.rule.props?.hideSecond ? 'YYYY-MM-DD HH:mm' : 'YYYY-MM-DD HH:mm:ss'
    case 'month':
    case 'monthrange':
      return 'YYYY-MM'
    case 'week':
      return 'YYYY-MM-DD [所在周]'
    default:
      return 'YYYY-MM-DD'
  }
}

function getComponentProps() {
  const {
    format: _format,
    max: _max,
    maxDate: _maxDate,
    min: _min,
    minDate: _minDate,
    modelValue: _modelValue,
    placeholder: _placeholder,
    type: _type,
    valueFormat: _valueFormat,
    ...restProps
  } = props.rule.props || {}

  return restProps
}

function isRangeType(type: CalendarType) {
  return type.endsWith('range')
}
</script>
