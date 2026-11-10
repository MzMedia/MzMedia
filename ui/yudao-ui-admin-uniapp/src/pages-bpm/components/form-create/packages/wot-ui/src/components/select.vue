<template>
  <view class="fc-select">
    <wd-form-item
      v-if="isMultiple"
      :title="rule.title"
      :title-width="titleWidth"
      :prop="rule.field"
      center
    >
      <wd-checkbox-group
        :model-value="Array.isArray(modelValue) ? modelValue : []"
        :disabled="disabled"
        :type="rule.props?.type || 'button'"
        :direction="rule.props?.direction || 'horizontal'"
        @change="handleMultipleChange"
      >
        <wd-checkbox
          v-for="option in columns"
          :key="String(option.value)"
          :name="option.value"
          :disabled="option.disabled"
        >
          {{ option.label || option.text || option.value }}
        </wd-checkbox>
      </wd-checkbox-group>
    </wd-form-item>

    <wd-form-item
      v-else
      :title="rule.title"
      :title-width="titleWidth"
      :prop="rule.field"
      :value="displayValue"
      :placeholder="placeholder"
      is-link
      @click="open"
    />
    <wd-picker
      v-model:visible="visible"
      :model-value="pickerValue"
      :columns="columns"
      label-key="label"
      value-key="value"
      @cancel="emit('cancel')"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { FormCreateOptionItem, NormalizedFormCreateRule } from '../../../../types/typing'
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
const isMultiple = computed(() => !!props.rule.props?.multiple || props.rule.props?.mode === 'multiple')
const columns = computed(() => props.rule.options || [])
const pickerValue = computed(() => props.modelValue === undefined || props.modelValue === null || props.modelValue === '' ? [] : [props.modelValue])
const displayValue = computed(() => {
  if (props.modelValue === undefined || props.modelValue === null || props.modelValue === '') {
    return ''
  }
  if (isMultiple.value && Array.isArray(props.modelValue)) {
    return props.modelValue
      .map(value => getOptionLabel(value))
      .filter(Boolean)
      .join('，')
  }
  return getOptionLabel(props.modelValue)
})

watch(visible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
  }
})

function getOptionLabel(value: any) {
  const selected = (columns.value as FormCreateOptionItem[]).find(item =>
    item.value === value || String(item.value) === String(value),
  )
  return selected?.label || selected?.text || String(value)
}

function open() {
  if (!props.disabled) {
    visible.value = true
  }
}

function handleConfirm({ value }: { value: any[] }) {
  const nextValue = value?.[0]
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function handleMultipleChange({ value }: { value: any[] }) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>
