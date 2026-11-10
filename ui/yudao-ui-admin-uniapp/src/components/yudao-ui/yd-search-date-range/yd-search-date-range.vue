<template>
  <view class="yd-search-form-item">
    <view class="yd-search-form-label">
      {{ label }}
    </view>
    <view class="yd-search-form-date-range-container">
      <view class="flex-1" @click="openPicker(0)">
        <view class="yd-search-form-date-range-picker">
          {{ formatDate(currentValue[0]) || '开始日期' }}
        </view>
      </view>
      -
      <view class="flex-1" @click="openPicker(1)">
        <view class="yd-search-form-date-range-picker">
          {{ formatDate(currentValue[1]) || '结束日期' }}
        </view>
      </view>
    </view>
    <wd-datetime-picker-view v-if="visible[0]" v-model="tempValue[0]" type="date" />
    <view v-if="visible[0]" class="yd-search-form-date-range-actions">
      <wd-button size="small" variant="plain" @click="visible[0] = false">
        取消
      </wd-button>
      <wd-button size="small" type="primary" @click="handleStartConfirm">
        确定
      </wd-button>
    </view>
    <wd-datetime-picker-view v-if="visible[1]" v-model="tempValue[1]" type="date" />
    <view v-if="visible[1]" class="yd-search-form-date-range-actions">
      <wd-button size="small" variant="plain" @click="visible[1] = false">
        取消
      </wd-button>
      <wd-button size="small" type="primary" @click="handleEndConfirm">
        确定
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { formatDate } from '@/utils/date'

const props = defineProps<{
  label: string
  modelValue?: [number | undefined, number | undefined]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: [number | undefined, number | undefined]]
}>()

const visible = ref<[boolean, boolean]>([false, false]) // 日期选择器状态
const currentValue = ref<[number | undefined, number | undefined]>([undefined, undefined]) // 日期范围值
const tempValue = ref<[number, number]>([Date.now(), Date.now()]) // 临时日期值

/** 打开日期选择器：临时值定位到当前值（支持默认值 / 已选值回显） */
function openPicker(index: 0 | 1) {
  tempValue.value[index] = currentValue.value[index] ?? Date.now()
  visible.value[index] = true
}

/** 确认开始日期 */
function handleStartConfirm() {
  currentValue.value = [tempValue.value[0], currentValue.value[1]]
  visible.value[0] = false
  emit('update:modelValue', currentValue.value)
}

/** 确认结束日期 */
function handleEndConfirm() {
  currentValue.value = [currentValue.value[0], tempValue.value[1]]
  visible.value[1] = false
  emit('update:modelValue', currentValue.value)
}

watch(
  () => props.modelValue,
  (value) => {
    currentValue.value = value || [undefined, undefined]
  },
  { immediate: true },
)
</script>
