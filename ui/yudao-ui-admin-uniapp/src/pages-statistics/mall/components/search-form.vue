<template>
  <!-- 搜索框入口 -->
  <view @click="visible = true">
    <wd-search :placeholder="placeholder" hide-cancel disabled />
  </view>

  <!-- 搜索弹窗 -->
  <wd-popup v-model="visible" position="top" :custom-style="getTopPopupStyle()" :modal-style="getTopPopupModalStyle()" @close="visible = false">
    <view class="yd-search-form-container">
      <yd-search-date-range v-model="dateRange" label="统计时间" />
      <view class="yd-search-form-actions">
        <wd-button class="flex-1" variant="plain" @click="handleReset">
          重置
        </wd-button>
        <wd-button class="flex-1" type="primary" @click="handleSearch">
          搜索
        </wd-button>
      </view>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { getTopPopupModalStyle, getTopPopupStyle } from '@/utils'
import { formatDate } from '@/utils/date'

const props = defineProps<{
  initialEndTime: number
  initialStartTime: number
}>()

const emit = defineEmits<{
  search: [data: { endTime: number, startTime: number }]
  reset: []
}>()

const visible = ref(false) // 搜索弹窗显示状态
const dateRange = ref<[number | undefined, number | undefined]>([props.initialStartTime, props.initialEndTime]) // 统计时间区间（默认值由父级传入）

const placeholder = computed(() => `${formatDate(dateRange.value[0])} 至 ${formatDate(dateRange.value[1])}`) // 搜索入口展示文案

watch(
  () => [props.initialStartTime, props.initialEndTime],
  () => {
    if (visible.value) {
      return
    }
    dateRange.value = [props.initialStartTime, props.initialEndTime]
  },
)

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    startTime: dateRange.value[0] ?? props.initialStartTime,
    endTime: dateRange.value[1] ?? props.initialEndTime,
  })
}

/** 重置按钮操作 */
function handleReset() {
  dateRange.value = [props.initialStartTime, props.initialEndTime]
  visible.value = false
  emit('reset')
}
</script>
