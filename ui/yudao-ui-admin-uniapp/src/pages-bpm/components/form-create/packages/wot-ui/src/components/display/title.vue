<template>
  <view class="fc-display-title">
    {{ text }}
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const text = computed(() => props.rule.props?.text || props.rule.props?.title || props.rule.title || props.rule.value || '')
</script>

<style lang="scss" scoped>
.fc-display-title {
  color: #1f2329;
  font-size: 32rpx;
  font-weight: 600;
  line-height: 46rpx;
  padding: 28rpx 24rpx 12rpx;
}
</style>
