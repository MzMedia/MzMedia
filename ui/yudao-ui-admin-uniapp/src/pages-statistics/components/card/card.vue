<template>
  <!-- 基础卡片壳：白底圆角阴影 + 内边距，可选标题行（右侧 extra 插槽） -->
  <view class="rounded-12rpx bg-white p-24rpx shadow-sm">
    <view v-if="title || $slots.extra" class="mb-20rpx flex items-center justify-between">
      <text class="text-30rpx text-[#333] font-semibold">{{ title }}</text>
      <slot name="extra" />
    </view>
    <slot />
  </view>
</template>

<script lang="ts" setup>
defineProps<{
  title?: string // 卡片标题，缺省则不渲染标题行
}>()
</script>
