<template>
  <!-- 总结 grid：两列排布多个总结卡片 -->
  <view class="grid grid-cols-2 gap-16rpx">
    <SummaryCard
      v-for="item in items"
      :key="item.label"
      :item="item"
      :reference-label="referenceLabel"
    />
  </view>
</template>

<script lang="ts" setup>
import SummaryCard from './summary-card.vue'

export interface SummaryItem {
  label: string
  value: number
  reference?: number // 对比参考值，存在时展示环比
  prefix?: string // 前缀，如金额 ￥
}

withDefaults(defineProps<{
  items: SummaryItem[]
  referenceLabel?: string // 对比说明文案
}>(), {
  referenceLabel: '较昨日',
})
</script>
