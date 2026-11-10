<template>
  <view class="fc-area-select">
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
      :options="areaList"
      :title="rule.title || '请选择地区'"
      :check-strictly="checkStrictly"
      text-key="name"
      value-key="id"
      children-key="children"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import type { Area } from '@/api/system/area'
import { computed, onMounted, ref, watch } from 'vue'
import { getAreaTree } from '@/api/system/area'
import { getPlaceholder } from '../../core/utils'

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

const areaList = ref<Area[]>([])
const loading = ref(false)
const visible = ref(false)
const closingByConfirm = ref(false)

const placeholder = computed(() => getPlaceholder(props.rule, '请选择'))
const checkStrictly = computed(() => props.rule.props?.checkStrictly !== false)
const cascaderValue = computed(() => normalizeValue(props.modelValue))
const displayValue = computed(() => {
  const value = normalizeValue(props.modelValue)
  if (value === '') {
    return ''
  }
  const path = findAreaPath(areaList.value, value)
  return path.length ? path.map(item => item.name).join(' / ') : String(value)
})

onMounted(() => {
  void loadAreaList()
})

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

async function open() {
  if (props.disabled) {
    return
  }
  if (loading.value) {
    uni.showToast({
      icon: 'none',
      title: '地区数据加载中，请稍后',
    })
    return
  }
  if (areaList.value.length === 0) {
    await loadAreaList()
  }
  if (areaList.value.length === 0) {
    uni.showToast({
      icon: 'none',
      title: '地区数据加载失败，请稍后重试',
    })
    return
  }
  visible.value = true
}

function handleConfirm({ value }: { value: any }) {
  closingByConfirm.value = true
  const nextValue = value === '' ? undefined : value
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

async function loadAreaList() {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    areaList.value = await getAreaTree()
  } catch (error) {
    console.error('加载地区数据失败:', error)
    areaList.value = []
  } finally {
    loading.value = false
  }
}

function findAreaPath(list: Area[], targetId: number | string): Area[] {
  for (const area of list) {
    if (isSameValue(area.id, targetId)) {
      return [area]
    }
    const childPath = findAreaPath(area.children || [], targetId)
    if (childPath.length) {
      return [area, ...childPath]
    }
  }
  return []
}

function normalizeValue(value: any): number | string | '' {
  const nextValue = Array.isArray(value) ? value[value.length - 1] : value
  return typeof nextValue === 'number' || (typeof nextValue === 'string' && nextValue !== '') ? nextValue : ''
}

function isSameValue(left: number | string, right: number | string) {
  return left === right || String(left) === String(right)
}
</script>
