<template>
  <view style="--wot-grid-item-padding: 12rpx 8rpx; --wot-grid-item-bg: transparent">
    <wd-grid :column="column" clickable :border="false">
      <wd-grid-item
        v-for="menu in menus"
        :key="menu.key"
        :text="menu.name"
        @click="navigateToMenu(menu)"
      >
        <template #icon>
          <view
            class="h-80rpx w-80rpx flex items-center justify-center rounded-16rpx"
            :style="getIconStyle(menu)"
          >
            <wd-icon :name="menu.icon" size="50rpx" :color="menu.iconColor" />
          </view>
        </template>
      </wd-grid-item>
    </wd-grid>
  </view>
</template>

<script lang="ts" setup>
import type { MenuItem } from '../index'
import { useMenuNavigate } from '../index'

defineOptions({
  name: 'MenuGrid',
})

withDefaults(defineProps<{
  menus: MenuItem[] // 菜单列表
  column?: number // 每行列数
}>(), {
  column: 4,
})

const { navigateToMenu } = useMenuNavigate()

/** 获取图标样式 */
function getIconStyle(menu: MenuItem) {
  return {
    backgroundColor: menu.iconColor ? `${menu.iconColor}20` : '#f5f5f5',
    color: menu.iconColor || '#666',
  }
}
</script>

<style lang="scss" scoped>
:deep(.wd-grid-item__text) {
  width: 100%;
  min-height: 40rpx;
  font-size: 24rpx;
  line-height: 32rpx;
  color: #333;
  overflow: hidden;
  display: -webkit-box;
  white-space: normal;
  word-break: break-all;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
