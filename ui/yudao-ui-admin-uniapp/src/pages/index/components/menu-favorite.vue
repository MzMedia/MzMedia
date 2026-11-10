<template>
  <view class="mx-20rpx mt-20rpx overflow-hidden rounded-16rpx bg-white">
    <view class="flex items-center justify-between px-24rpx py-20rpx">
      <text class="text-28rpx text-#333 font-500">我的常用</text>
      <view class="p-10rpx" @click="handleGotoFavoriteSettings">
        <wd-icon name="settings" size="32rpx" color="#999" />
      </view>
    </view>
    <MenuGrid v-if="menus.length > 0" :menus="menus" />
    <view
      v-else
      class="mx-24rpx mb-24rpx flex items-center border-1rpx border-#ddd rounded-12rpx border-dashed px-24rpx py-12rpx"
      @click="handleGotoFavoriteSettings"
    >
      <wd-icon name="plus" size="32rpx" color="#999" />
      <text class="pl-10rpx text-28rpx text-#999">添加我常用的</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { MenuItem } from '../index'
import MenuGrid from './menu-grid.vue'

defineOptions({
  name: 'MenuFavorite',
})

defineProps<{
  menus: MenuItem[] // 常用菜单列表
}>()

/** 跳转到常用服务设置页面 */
function handleGotoFavoriteSettings() {
  uni.navigateTo({
    url: '/pages/index/settings/index',
  })
}
</script>
