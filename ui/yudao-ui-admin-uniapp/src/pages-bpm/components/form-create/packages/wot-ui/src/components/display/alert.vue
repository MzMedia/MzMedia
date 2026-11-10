<template>
  <view class="fc-display-alert" :class="[`fc-display-alert--${alertType}`]">
    <view v-if="title" class="fc-display-alert__title">
      {{ title }}
    </view>
    <view v-if="content" class="fc-display-alert__content">
      {{ content }}
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const title = computed(() => props.rule.props?.title || props.rule.title || '')
const content = computed(() => props.rule.props?.description || props.rule.props?.content || props.rule.props?.message || props.rule.value || '')
const alertType = computed(() => {
  const type = props.rule.props?.type || 'info'
  return ['success', 'warning', 'error', 'danger'].includes(type) ? type : 'info'
})
</script>

<style lang="scss" scoped>
.fc-display-alert {
  margin: 20rpx 24rpx;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  background: #f2f3f5;
  color: #4e5969;
}

.fc-display-alert--success {
  background: #f0f9eb;
  color: #389e0d;
}

.fc-display-alert--warning {
  background: #fff7e8;
  color: #d46b08;
}

.fc-display-alert--error,
.fc-display-alert--danger {
  background: #fff1f0;
  color: #cf1322;
}

.fc-display-alert__title {
  font-size: 28rpx;
  font-weight: 600;
  line-height: 40rpx;
}

.fc-display-alert__content {
  margin-top: 8rpx;
  font-size: 26rpx;
  line-height: 38rpx;
}
</style>
