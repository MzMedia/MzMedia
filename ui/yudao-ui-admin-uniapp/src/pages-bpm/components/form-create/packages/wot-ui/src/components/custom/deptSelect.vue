<template>
  <view class="fc-dept-select-field">
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
      v-if="!isMultiple"
      v-model="selectedValue"
      v-model:visible="cascaderVisible"
      :options="deptOptions"
      value-key="value"
      text-key="name"
      children-key="children"
      check-strictly
      :title="rule.title || '选择部门'"
      @update:model-value="handleSingleChange"
    />

    <wd-popup
      v-else
      v-model="multiVisible"
      position="bottom"
      round
      safe-area-inset-bottom
      custom-style="height: 78vh; overflow: hidden;"
    >
      <view class="fc-dept-select">
        <view class="fc-dept-select__header">
          <wd-button size="small" variant="plain" @click="clearMultiValue">
            清空
          </wd-button>
          <text class="fc-dept-select__title">{{ rule.title || '选择部门' }}</text>
          <wd-icon name="close" size="20px" color="#666" @click="cancelMultiPicker" />
        </view>

        <wd-search
          v-model="keyword"
          :placeholder="rule.props?.filterPlaceholder || '搜索部门'"
          hide-cancel
          placeholder-left
          custom-class="fc-dept-select__search"
        />

        <view v-if="!keyword.trim()" class="fc-dept-select__breadcrumb">
          <text
            v-for="(item, index) in breadcrumbs"
            :key="index"
            class="fc-dept-select__breadcrumb-item"
            @click="activeDept = item.option"
          >
            {{ item.name }}
          </text>
        </view>

        <scroll-view scroll-y class="fc-dept-select__body">
          <view v-if="loading" class="fc-dept-select__empty">
            加载中...
          </view>
          <view v-else-if="visibleDeptOptions.length === 0" class="fc-dept-select__empty">
            暂无部门
          </view>
          <template v-else>
            <view
              v-for="option in visibleDeptOptions"
              :key="String(option.value)"
              class="fc-dept-select__item"
              :class="{ 'is-selected': isDeptSelected(option), 'is-disabled': option.disabled }"
            >
              <view class="fc-dept-select__item-main" @click="toggleDept(option)">
                <view class="fc-dept-select__check">
                  <wd-icon v-if="isDeptSelected(option)" name="check" size="14px" color="#fff" />
                </view>
                <view class="fc-dept-select__text">
                  <text class="fc-dept-select__name">{{ option.name }}</text>
                  <text v-if="keyword.trim() && option.path" class="fc-dept-select__path">
                    {{ option.path }}
                  </text>
                </view>
              </view>
              <view
                v-if="!keyword.trim() && hasChildren(option)"
                class="fc-dept-select__children"
                @click.stop="activeDept = option"
              >
                <text>下级</text>
                <wd-icon name="arrow-right" size="14px" color="#999" />
              </view>
            </view>
          </template>
        </scroll-view>

        <view class="fc-dept-select__footer">
          <text class="fc-dept-select__count">已选 {{ multiValue.length }} 个部门</text>
          <wd-button size="small" type="primary" @click="handleMultiConfirm">
            确定
          </wd-button>
        </view>
      </view>
    </wd-popup>
  </view>
</template>

<script lang="ts" setup>
import type { CascaderOption } from '@wot-ui/ui/components/wd-cascader/types'
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import type { CustomSelectOption, CustomSelectValue } from './types'
import type { Dept } from '@/api/system/dept'
import { computed, ref, watch } from 'vue'
import { getSimpleDeptList } from '@/api/system/dept'
import { getPlaceholder } from '../../core/utils'
import { isMultipleSelect, isSameValue, normalizeSelectValue } from './utils'

type DeptOption = CascaderOption & CustomSelectOption & {
  children?: DeptOption[]
  id?: number
  name: string
  parentId?: number
}

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

const activeDept = ref<DeptOption | null>(null)
const cascaderVisible = ref(false)
const deptOptions = ref<DeptOption[]>([])
const flatDeptOptions = ref<DeptOption[]>([])
const keyword = ref('')
const loading = ref(false)
const loadError = ref('')
const multiValue = ref<CustomSelectValue[]>([])
const multiVisible = ref(false)
const selectedValue = ref<CustomSelectValue | ''>('')
let loadPromise: Promise<void> | undefined

const isMultiple = computed(() => isMultipleSelect(props.rule))
const placeholder = computed(() => getPlaceholder(props.rule, '请选择'))
const returnType = computed(() => props.rule.props?.returnType === 'name' ? 'name' : 'id')
const displayValue = computed(() => formatDeptSummary(props.modelValue, isMultiple.value))
const activeDeptOptions = computed(() => activeDept.value?.children || deptOptions.value)
const visibleDeptOptions = computed(() => {
  const text = keyword.value.trim().toLowerCase()
  if (!text) {
    return activeDeptOptions.value
  }
  return flatDeptOptions.value.filter((item) => {
    const name = item.name.toLowerCase()
    const path = (item.path || '').toLowerCase()
    return name.includes(text) || path.includes(text)
  })
})
const breadcrumbs = computed(() => {
  if (!activeDept.value) {
    return [{ name: '全部部门', option: null as DeptOption | null }]
  }
  return [
    { name: '全部部门', option: null as DeptOption | null },
    ...findDeptPath(activeDept.value.value).map(option => ({
      name: option.name,
      option,
    })),
  ]
})

watch(
  () => [props.modelValue, isMultiple.value],
  () => {
    if (isMultiple.value) {
      multiValue.value = normalizeSelectValue(props.modelValue, true) as CustomSelectValue[]
      return
    }
    selectedValue.value = normalizeSelectValue(props.modelValue, false) as CustomSelectValue | ''
  },
  { deep: true, immediate: true },
)

watch(
  () => [props.rule.props, returnType.value],
  () => loadOptions(),
  { deep: true, immediate: true },
)

watch(cascaderVisible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
  }
})

watch(multiVisible, (value) => {
  if (value) {
    emit('open')
  } else {
    emit('close')
  }
})

async function open() {
  if (props.disabled) {
    return
  }
  if (loading.value && loadPromise) {
    await loadPromise
  }
  if (loadError.value || flatDeptOptions.value.length === 0) {
    await loadOptions()
  }
  if (flatDeptOptions.value.length === 0) {
    return
  }
  if (isMultiple.value) {
    keyword.value = ''
    activeDept.value = null
    multiValue.value = normalizeSelectValue(props.modelValue, true) as CustomSelectValue[]
    multiVisible.value = true
    return
  }
  selectedValue.value = normalizeSelectValue(props.modelValue, false) as CustomSelectValue | ''
  cascaderVisible.value = true
}

function handleSingleChange(value: string | number | Array<string | number>) {
  const selected = Array.isArray(value) ? value.at(-1) : value
  const nextValue = selected === '' || selected === undefined ? undefined : selected
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
}

function handleMultiConfirm() {
  const nextValue = [...multiValue.value]
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
  emit('confirm', nextValue)
  multiVisible.value = false
}

function cancelMultiPicker() {
  emit('cancel')
  multiVisible.value = false
}

function toggleDept(option: DeptOption) {
  if (option.disabled) {
    return
  }
  if (isDeptSelected(option)) {
    multiValue.value = multiValue.value.filter(value => !isSameValue(value, option.value))
    return
  }
  multiValue.value = [...multiValue.value, option.value]
}

function clearMultiValue() {
  multiValue.value = []
}

async function loadOptions() {
  if (loadPromise) {
    return loadPromise
  }
  loadPromise = doLoadOptions().finally(() => {
    loadPromise = undefined
  })
  return loadPromise
}

async function doLoadOptions() {
  loading.value = true
  loadError.value = ''
  try {
    const list = await getSimpleDeptList()
    deptOptions.value = buildDeptTree(list as Dept[])
    flatDeptOptions.value = flattenDeptOptions(deptOptions.value)
  } catch (error) {
    console.error('加载部门选项失败:', error)
    deptOptions.value = []
    flatDeptOptions.value = []
    loadError.value = '部门选项加载失败，请稍后重试'
    showLoadError(loadError.value)
  } finally {
    loading.value = false
  }
}

function buildDeptTree(list: Dept[]) {
  if (list.some(item => Array.isArray(item.children) && item.children.length > 0)) {
    return buildNestedDeptTree(list)
  }
  return buildFlatDeptTree(list)
}

function buildNestedDeptTree(list: Dept[], parentPath = '', level = 0): DeptOption[] {
  return list
    .map((dept) => {
      const option = toDeptOption(dept, parentPath, level)
      if (!option) {
        return undefined
      }
      const children = buildNestedDeptTree(dept.children || [], option.path || option.name, level + 1)
      return children.length > 0 ? { ...option, children } : option
    })
    .filter(Boolean) as DeptOption[]
}

function buildFlatDeptTree(list: Dept[]) {
  const nodeMap = new Map<number, Dept & { children: Dept[] }>()
  const roots: Array<Dept & { children: Dept[] }> = []

  list.forEach((dept) => {
    if (dept.id === undefined || dept.id === null) {
      return
    }
    const { children: _children, ...item } = dept
    nodeMap.set(dept.id, { ...item, children: [] })
  })

  nodeMap.forEach((dept) => {
    const parent = nodeMap.get(dept.parentId)
    if (parent && parent.id !== dept.id) {
      parent.children.push(dept)
    } else {
      roots.push(dept)
    }
  })

  return buildNestedDeptTree(roots.length ? roots : Array.from(nodeMap.values()))
}

function toDeptOption(dept: Dept, parentPath: string, level: number): DeptOption | undefined {
  const name = dept.name || String(dept.id || '')
  const value = returnType.value === 'name' ? name : dept.id
  if (value === undefined || value === null || value === '') {
    return undefined
  }
  const path = parentPath ? `${parentPath} / ${name}` : name
  const { children: _children, ...rawDept } = dept
  return {
    ...rawDept,
    displayLabel: name,
    label: path && path !== name ? `${name} ${path}` : name,
    level,
    name,
    path,
    raw: dept as unknown as Record<string, any>,
    value,
  }
}

function flattenDeptOptions(list: DeptOption[]): DeptOption[] {
  return list.flatMap(item => [item, ...flattenDeptOptions(item.children || [])])
}

function hasChildren(option: DeptOption) {
  return Array.isArray(option.children) && option.children.length > 0
}

function isDeptSelected(option: DeptOption) {
  return multiValue.value.some(value => isSameValue(value, option.value))
}

function findDeptPath(targetValue: CustomSelectValue) {
  const path: DeptOption[] = []
  const findPath = (list: DeptOption[]): boolean => {
    for (const item of list) {
      if (isSameValue(item.value, targetValue) || findPath(item.children || [])) {
        path.unshift(item)
        return true
      }
    }
    return false
  }
  findPath(deptOptions.value)
  return path
}

function formatDeptSummary(value: any, multiple: boolean) {
  if (multiple) {
    const values = normalizeSelectValue(value, true) as CustomSelectValue[]
    if (values.length === 0) {
      return ''
    }
    const labels = values.map(item => getDeptLabel(item)).filter(Boolean)
    const visibleLabels = labels.slice(0, 2).join('、')
    return values.length > 2 ? `${visibleLabels} 等 ${values.length}个部门` : visibleLabels
  }

  const nextValue = normalizeSelectValue(value, false)
  if (nextValue === '') {
    return ''
  }
  const option = findDeptOption(nextValue as CustomSelectValue)
  return option?.path || option?.name || String(nextValue)
}

function getDeptLabel(value: CustomSelectValue) {
  const option = findDeptOption(value)
  return option?.displayLabel || option?.name || String(value)
}

function findDeptOption(value: CustomSelectValue) {
  return flatDeptOptions.value.find(item =>
    isSameValue(item.value, value)
    || (item.id !== undefined && isSameValue(item.id, value))
    || isSameValue(item.name, value),
  )
}

function showLoadError(message: string) {
  uni.showToast({
    icon: 'none',
    title: message,
  })
}
</script>

<style lang="scss">
@use './style.scss';

.fc-dept-select {
  display: flex;
  flex-direction: column;
  height: 78vh;
  background: #f7f8fa;
}

.fc-dept-select__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 96rpx;
  padding: 0 28rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.fc-dept-select__title {
  max-width: 420rpx;
  overflow: hidden;
  color: #1f2937;
  font-size: 32rpx;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fc-dept-select__search {
  flex-shrink: 0;
}

.fc-dept-select__breadcrumb {
  display: flex;
  flex-shrink: 0;
  gap: 12rpx;
  overflow-x: auto;
  padding: 0 24rpx 16rpx;
  background: #fff;
  white-space: nowrap;
}

.fc-dept-select__breadcrumb-item {
  color: #1677ff;
  font-size: 26rpx;
}

.fc-dept-select__breadcrumb-item:not(:last-child)::after {
  margin-left: 12rpx;
  color: #999;
  content: '/';
}

.fc-dept-select__body {
  flex: 1;
  min-height: 0;
  padding: 16rpx 24rpx;
  box-sizing: border-box;
}

.fc-dept-select__item {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  min-height: 92rpx;
  margin-bottom: 12rpx;
  overflow: hidden;
  background: #fff;
  border: 1rpx solid transparent;
  border-radius: 12rpx;
}

.fc-dept-select__item.is-selected {
  border-color: #1677ff;
}

.fc-dept-select__item.is-disabled {
  opacity: 0.5;
}

.fc-dept-select__item-main {
  display: flex;
  flex: 1;
  align-items: center;
  min-width: 0;
  padding: 20rpx 24rpx;
}

.fc-dept-select__check {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 36rpx;
  height: 36rpx;
  margin-right: 20rpx;
  background: #fff;
  border: 2rpx solid #c8c9cc;
  border-radius: 50%;
  box-sizing: border-box;
}

.fc-dept-select__item.is-selected .fc-dept-select__check {
  background: #1677ff;
  border-color: #1677ff;
}

.fc-dept-select__text {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.fc-dept-select__name {
  overflow: hidden;
  color: #1f2937;
  font-size: 30rpx;
  line-height: 40rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fc-dept-select__path {
  margin-top: 6rpx;
  overflow: hidden;
  color: #8a8f99;
  font-size: 24rpx;
  line-height: 32rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fc-dept-select__children {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  gap: 4rpx;
  padding: 0 24rpx;
  color: #6b7280;
  font-size: 26rpx;
  border-left: 1rpx solid #f0f0f0;
}

.fc-dept-select__empty {
  padding: 80rpx 0;
  color: #999;
  font-size: 28rpx;
  text-align: center;
}

.fc-dept-select__footer {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 28rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}

.fc-dept-select__count {
  color: #6b7280;
  font-size: 26rpx;
}
</style>
