<template>
  <view class="fc-display-html">
    <rich-text :nodes="html" />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const html = computed(() => String(props.rule.props?.html || props.rule.props?.content || props.rule.value || ''))
</script>

<style lang="scss" scoped>
.fc-display-html {
  color: #333;
  font-size: 28rpx;
  line-height: 1.7;
  padding: 20rpx 24rpx;
  word-break: break-all;
}
</style>
