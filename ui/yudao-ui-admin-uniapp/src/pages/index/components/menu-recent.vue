<template>
  <view v-if="menus.length > 0" class="mx-20rpx mt-20rpx overflow-hidden rounded-16rpx bg-white">
    <view class="flex items-center px-24rpx py-20rpx">
      <text class="text-28rpx text-#333 font-500">最近使用</text>
    </view>
    <MenuGrid :menus="menus" />
  </view>
</template>

<script lang="ts" setup>
import type { MenuItem } from '../index'
import MenuGrid from './menu-grid.vue'

defineOptions({
  name: 'MenuRecent',
})

defineProps<{
  menus: MenuItem[] // 最近使用菜单列表
}>()
</script>
