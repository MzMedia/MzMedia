<template>
  <view class="fc-cascader">
    <wd-form-item
      :title="rule.title"
      :title-width="titleWidth"
      :prop="rule.field"
      :value="displayValue"
      :placeholder="placeholder"
      :is-link="!disabled"
      @click="open"
    />

    <wd-cascader
      v-model:visible="visible"
      :model-value="cascaderValue"
      :options="options"
      :title="rule.title || '请选择'"
      :check-strictly="checkStrictly"
      text-key="text"
      value-key="value"
      children-key="children"
      v-bind="componentProps"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { CascaderOption } from '@wot-ui/ui/components/wd-cascader/types'
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed, ref, watch } from 'vue'
import { getPlaceholder } from '../core/utils'

type CascaderValue = string | number

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
  'cancel': []
  'close': []
  'confirm': [value: any]
  'open': []
}>()

const visible = ref(false)
const closingByConfirm = ref(false)

const placeholder = computed(() => getPlaceholder(props.rule, '请选择'))
const fieldNames = computed(() => getFieldNames())
const options = computed(() => normalizeOptions(getSourceOptions()))
const cascaderValue = computed(() => normalizeCascaderValue(props.modelValue))
const checkStrictly = computed(() => props.rule.props?.checkStrictly ?? props.rule.props?.props?.checkStrictly ?? false)
const componentProps = computed(() => getComponentProps())
const displayValue = computed(() => formatDisplayValue(props.modelValue))

watch(visible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
    if (!closingByConfirm.value) {
      emit('cancel')
    }
    closingByConfirm.value = false
  }
})

function open() {
  if (props.disabled) {
    return
  }
  visible.value = true
}

function handleConfirm({ value, selectedOptions }: { value: any, selectedOptions?: CascaderOption[] }) {
  closingByConfirm.value = true
  const values = getSelectedValues(value, selectedOptions)
  const leafValue = values.length ? values[values.length - 1] : value
  const nextValue = shouldEmitPath()
    ? values
    : (leafValue === '' || leafValue === undefined ? undefined : leafValue)

  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function getSourceOptions() {
  const ruleProps = props.rule.props || {}
  const candidates = [
    ruleProps.options,
    ruleProps.data,
    ruleProps.treeData,
    props.rule.options,
  ]
  return candidates.find(item => Array.isArray(item)) || []
}

function normalizeOptions(list: Record<string, any>[]): CascaderOption[] {
  const fields = fieldNames.value
  return list
    .map((item) => {
      const value = getNodeValue(item, fields.value)
      if (!isValidValue(value)) {
        return undefined
      }
      const text = getNodeText(item, fields.label, value)
      const children = Array.isArray(item[fields.children])
        ? normalizeOptions(item[fields.children])
        : []
      return {
        disabled: item.disabled,
        isLeaf: item.isLeaf,
        text,
        tip: item.tip,
        value,
        ...(children.length ? { children } : {}),
      }
    })
    .filter(Boolean) as CascaderOption[]
}

function getFieldNames() {
  const ruleProps = props.rule.props || {}
  const fieldProps = ruleProps.fieldNames || ruleProps.props || {}
  return {
    children: fieldProps.children || ruleProps.childrenKey || 'children',
    label: fieldProps.label || fieldProps.text || ruleProps.labelKey || ruleProps.textKey || 'label',
    value: fieldProps.value || ruleProps.valueKey,
  }
}

function getNodeValue(item: Record<string, any>, configuredValueKey?: string): any {
  if (configuredValueKey && item[configuredValueKey] !== undefined) {
    return item[configuredValueKey]
  }
  return item.value ?? item.key ?? item.id
}

function getNodeText(item: Record<string, any>, configuredLabelKey: string | undefined, value: CascaderValue) {
  if (configuredLabelKey && item[configuredLabelKey] !== undefined) {
    return String(item[configuredLabelKey])
  }
  return String(item.label ?? item.text ?? item.name ?? item.title ?? value)
}

function normalizeCascaderValue(value: any): CascaderValue | CascaderValue[] | '' {
  if (Array.isArray(value)) {
    return value.filter(isValidValue)
  }
  return isValidValue(value) ? value : ''
}

function formatDisplayValue(value: any) {
  const values = Array.isArray(value) ? value.filter(isValidValue) : []
  const leafValue = values.length ? values[values.length - 1] : value

  if (!isValidValue(leafValue)) {
    return ''
  }

  const path = findPathByValue(options.value, leafValue)
  if (path?.length) {
    return path.map(item => item.text).join(' / ')
  }

  return values.length ? values.join(' / ') : String(leafValue)
}

function findPathByValue(list: CascaderOption[], value: CascaderValue): CascaderOption[] | undefined {
  for (const item of list) {
    if (isSameValue(item.value, value)) {
      return [item]
    }
    const children = item.children as CascaderOption[] | undefined
    if (children?.length) {
      const childPath = findPathByValue(children, value)
      if (childPath) {
        return [item, ...childPath]
      }
    }
  }
  return undefined
}

function getSelectedValues(value: any, selectedOptions?: CascaderOption[]) {
  if (selectedOptions?.length) {
    return selectedOptions.map(item => item.value).filter(isValidValue)
  }
  if (Array.isArray(value)) {
    return value.filter(isValidValue)
  }
  return isValidValue(value) ? [value] : []
}

function shouldEmitPath() {
  const ruleProps = props.rule.props || {}
  const emitPath = ruleProps.emitPath ?? ruleProps.props?.emitPath
  return emitPath !== false
}

function getComponentProps() {
  const {
    checkStrictly: _checkStrictly,
    childrenKey: _childrenKey,
    data: _data,
    emitPath: _emitPath,
    fieldNames: _fieldNames,
    labelKey: _labelKey,
    modelValue: _modelValue,
    options: _options,
    props: _props,
    textKey: _textKey,
    treeData: _treeData,
    valueKey: _valueKey,
    ...restProps
  } = props.rule.props || {}

  return restProps
}

function isValidValue(value: any): value is CascaderValue {
  return (typeof value === 'string' || typeof value === 'number') && value !== ''
}

function isSameValue(left: any, right: CascaderValue) {
  return left === right || String(left) === String(right)
}
</script>
