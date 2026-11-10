<template>
  <view class="fc-display-divider">
    <wd-divider
      :color="rule.props?.color"
      :content-position="rule.props?.contentPosition || rule.props?.position || 'center'"
      :dashed="rule.props?.dashed"
    >
      {{ text }}
    </wd-divider>
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const text = computed(() => props.rule.props?.text || props.rule.props?.title || props.rule.title || '')
</script>

<style lang="scss" scoped>
.fc-display-divider {
  padding: 8rpx 24rpx;
}
</style>
