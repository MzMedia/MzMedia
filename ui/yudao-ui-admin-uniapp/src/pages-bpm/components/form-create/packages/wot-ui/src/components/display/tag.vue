<template>
  <view class="fc-display-tags">
    <wd-tag
      v-for="(item, index) in tags"
      :key="index"
      :type="tagType"
      :variant="tagVariant"
      class="fc-display-tags__item"
    >
      {{ item }}
    </wd-tag>
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const tags = computed(() => {
  const value = props.rule.value ?? props.rule.props?.text ?? props.rule.props?.label ?? props.rule.title
  return Array.isArray(value) ? value.map(item => String(item)) : value ? [String(value)] : []
})
const tagType = computed(() => {
  const type = props.rule.props?.type || 'primary'
  return type === 'info' ? 'default' : type
})
const tagVariant = computed(() => {
  const effectMap: Record<string, string> = {
    dark: 'dark',
    light: 'light',
    plain: 'plain',
  }
  const effect = props.rule.props?.effect
  return effectMap[effect] || props.rule.props?.variant || 'light'
})
</script>

<style lang="scss" scoped>
.fc-display-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 20rpx 24rpx;
}
</style>
