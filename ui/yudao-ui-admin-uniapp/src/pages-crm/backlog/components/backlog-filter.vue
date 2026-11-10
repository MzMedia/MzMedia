<template>
  <!-- 待办筛选条（各待办列表共用） -->
  <view v-if="filters.length" class="flex items-center gap-16rpx bg-white px-24rpx pb-16rpx">
    <view
      v-for="filter in filters"
      :key="filter.prop"
      class="flex items-center gap-4rpx rounded-8rpx bg-[#f7f8fa] px-20rpx py-10rpx"
      @click="openFilter(filter)"
    >
      <text class="text-26rpx text-[#333]">{{ getFilterLabel(filter) }}</text>
      <wd-icon name="caret-down" size="28rpx" color="#999" />
    </view>
    <wd-picker
      v-model:visible="pickerVisible"
      :model-value="activeFilter ? modelValue[activeFilter.prop] : undefined"
      :columns="activeFilter?.options || []"
      label-key="label"
      value-key="value"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { BacklogFilter } from './common'
import { ref } from 'vue'
import { getWotPickerDisplay } from '@/utils/wot'

defineProps<{ filters: BacklogFilter[] }>()
const emit = defineEmits<{ change: [] }>()
const modelValue = defineModel<Record<string, any>>({ required: true })
const pickerVisible = ref(false) // 选择器显示状态
const activeFilter = ref<BacklogFilter>() // 当前操作的筛选项

/** 获取筛选项展示文案 */
function getFilterLabel(filter: BacklogFilter) {
  return getWotPickerDisplay(filter.options, modelValue.value[filter.prop], { placeholder: filter.label })
}

/** 打开筛选选择器 */
function openFilter(filter: BacklogFilter) {
  activeFilter.value = filter
  pickerVisible.value = true
}

/** 确认筛选 */
function handleConfirm({ value }: { value: any[] }) {
  if (!activeFilter.value) {
    return
  }
  modelValue.value = { ...modelValue.value, [activeFilter.value.prop]: value[0] }
  emit('change')
}
</script>
