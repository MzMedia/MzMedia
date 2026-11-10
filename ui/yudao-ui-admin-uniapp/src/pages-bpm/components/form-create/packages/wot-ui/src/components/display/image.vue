<template>
  <view v-if="images.length > 0" class="fc-display-images">
    <image
      v-for="(src, index) in images"
      :key="index"
      :src="src"
      :mode="rule.props?.mode || 'aspectFill'"
      class="fc-display-images__item"
      @click="preview(index)"
    />
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import { computed } from 'vue'

defineOptions({
  name: 'FcImage',
})

const props = defineProps<{
  rule: NormalizedFormCreateRule
}>()

const images = computed(() => {
  const value = props.rule.value ?? props.rule.props?.src ?? props.rule.props?.url ?? props.rule.props?.previewSrcList
  const list = Array.isArray(value)
    ? value
    : typeof value === 'string' && value.includes(',')
      ? value.split(',')
      : value
        ? [value]
        : []
  return list.map(item => String(item)).filter(Boolean)
})

function preview(index: number) {
  uni.previewImage({
    current: images.value[index],
    urls: images.value,
  })
}
</script>

<style lang="scss" scoped>
.fc-display-images {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 20rpx 24rpx;
}

.fc-display-images__item {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  background: #f2f3f5;
}
</style>
