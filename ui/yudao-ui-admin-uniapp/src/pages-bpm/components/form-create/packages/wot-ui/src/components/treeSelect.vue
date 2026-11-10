<template>
  <view class="fc-tree-select">
    <yd-tree-select
      :model-value="modelValue"
      :data="treeOptions"
      :props="treeProps"
      :label="rule.title"
      :label-width="treeLabelWidth"
      :prop="rule.field"
      :placeholder="placeholder"
      :title="rule.title || '请选择'"
      :disabled="disabled"
      :check-strictly="checkStrictly"
      :show-checkbox="isMultiple"
      :multiple="isMultiple"
      :filterable="isMultiple"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @cancel="emit('cancel')"
      @confirm="handleConfirm"
      @open="emit('open')"
      @close="emit('close')"
    />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'
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

const placeholder = computed(() => getPlaceholder(props.rule, '请选择')) // 占位文案
const isMultiple = computed(() => { // 是否多选
  return props.rule.type === 'treeSelectMultiple'
    || !!props.rule.props?.multiple
    || !!props.rule.props?.showCheckbox
    || props.rule.props?.mode === 'multiple'
})
const checkStrictly = computed(() => props.rule.props?.checkStrictly !== false) // 是否父子不联动
const treeOptions = computed(() => getTreeData()) // 树形选项
const treeProps = computed(() => getFieldNames()) // 树字段映射
const treeLabelWidth = computed(() => props.titleWidth === undefined ? '180rpx' : String(props.titleWidth)) // 表单标题宽度

/** 更新字段值 */
function handleUpdate(value: any) {
  emit('update:modelValue', value)
}

/** 触发变更事件 */
function handleChange(value: any) {
  emit('change', value)
}

/** 确认选择 */
function handleConfirm({ value }: { value: any }) {
  // form-create 字段值保持 checkedKeys，半选父节点由具体业务按需处理。
  emit('confirm', value)
}

/** 获取树形数据 */
function getTreeData() {
  const ruleProps = props.rule.props || {}
  const candidates = [
    ruleProps.data,
    ruleProps.options,
    ruleProps.treeData,
    props.rule.options,
  ]
  return candidates.find(item => Array.isArray(item)) || []
}

/** 获取树字段映射 */
function getFieldNames() {
  const fieldProps = props.rule.props?.props || props.rule.props?.fieldNames || {}
  return {
    children: fieldProps.children || props.rule.props?.childrenKey || 'children',
    label: fieldProps.label || props.rule.props?.labelKey || props.rule.props?.textKey || 'label',
    value: fieldProps.value || props.rule.props?.nodeKey || props.rule.props?.valueKey || 'value',
  }
}
</script>
