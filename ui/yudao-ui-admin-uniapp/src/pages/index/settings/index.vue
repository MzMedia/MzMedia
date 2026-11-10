<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="编辑工作台"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索框 -->
    <view>
      <wd-search v-model="searchKeyword" placeholder="搜索" hide-cancel />
    </view>

    <!-- 工作台布局 -->
    <view class="mx-20rpx mt-20rpx overflow-hidden rounded-16rpx bg-white">
      <view class="section-header">
        <text class="text-28rpx text-#333 font-500">工作台布局</text>
      </view>
      <view class="flex gap-16rpx px-30rpx pb-24rpx">
        <view
          v-for="opt in layoutOptions"
          :key="opt.value"
          class="flex-1 rounded-12rpx py-16rpx text-center text-26rpx"
          :class="menuLayout === opt.value ? 'bg-#3370ff text-white' : 'bg-#f5f5f5 text-#666'"
          @click="setMenuLayout(opt.value)"
        >
          {{ opt.label }}
        </view>
      </view>
    </view>

    <!-- 我的常用区域 -->
    <view class="mx-20rpx mt-20rpx overflow-hidden rounded-16rpx bg-white">
      <view class="section-header">
        <text class="text-28rpx text-#333 font-500">我的常用</text>
      </view>
      <!-- 常用分组 -->
      <view v-if="favoriteMenus.length > 0" class="menu-list">
        <view v-for="(menu, idx) in favoriteMenus" :key="menu.key" class="menu-item">
          <view class="menu-item__left">
            <view class="menu-item__icon" :style="{ backgroundColor: menu.iconColor ? `${menu.iconColor}20` : '#f5f5f5' }">
              <wd-icon :name="menu.icon" size="40rpx" :color="menu.iconColor" />
            </view>
            <text class="menu-item__name">{{ menu.name }}</text>
          </view>
          <view class="menu-item__right">
            <view class="p-6rpx" @click="moveFavorite(menu, -1)">
              <wd-icon name="arrow-up" size="36rpx" :color="idx === 0 ? '#dcdcdc' : '#999'" />
            </view>
            <view class="p-6rpx" @click="moveFavorite(menu, 1)">
              <wd-icon name="arrow-down" size="36rpx" :color="idx === favoriteMenus.length - 1 ? '#dcdcdc' : '#999'" />
            </view>
            <view class="toggle-btn toggle-remove" @click="handleRemoveFavorite(menu)">
              <view class="toggle-bar-h" />
            </view>
          </view>
        </view>
      </view>
      <view v-else class="flex flex-col items-center justify-center py-60rpx">
        <wd-button type="primary" variant="plain" @click="scrollToGroups">
          <wd-icon name="plus" size="28rpx" />
          添加我的常用
        </wd-button>
      </view>
    </view>

    <!-- 菜单分组 -->
    <view id="menuGroups">
      <view v-for="group in filteredMenuGroups" :key="group.key" class="mx-20rpx mt-20rpx overflow-hidden rounded-16rpx bg-white">
        <view class="section-header">
          <text class="text-28rpx text-#333 font-500">{{ group.name }}</text>
        </view>
        <view class="menu-list">
          <view v-for="menu in group.menus" :key="menu.key" class="menu-item">
            <view class="menu-item__left">
              <view class="menu-item__icon" :style="{ backgroundColor: menu.iconColor ? `${menu.iconColor}20` : '#f5f5f5' }">
                <wd-icon :name="menu.icon" size="40rpx" :color="menu.iconColor" />
              </view>
              <text class="menu-item__name">{{ menu.name }}</text>
            </view>
            <view class="menu-item__right">
              <view v-if="isInFavorites(menu)" class="toggle-btn toggle-remove" @click="handleRemoveFavorite(menu)">
                <view class="toggle-bar-h" />
              </view>
              <view v-else class="toggle-btn toggle-add" @click="handleAddFavorite(menu)">
                <view class="toggle-bar-h" />
                <view class="toggle-bar-v" />
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部安全区域 -->
    <view class="h-40rpx" />
  </view>
</template>

<script lang="ts" setup>
import type { MenuGroup, MenuItem, MenuLayout } from '../index'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { useUserStore } from '@/store/user'
import { navigateBackPlus } from '@/utils'
import { getMenuGroups, getMenuItemByKey, menuLayout, setMenuLayout } from '../index'

defineOptions({
  name: 'FavoriteSettings',
})

definePage({
  style: {
    navigationStyle: 'custom',
  },
})

const userStore = useUserStore()
const toast = useToast()

const layoutOptions: { value: MenuLayout, label: string }[] = [ // 工作台布局选项
  { value: 'section', label: '小标题' },
  { value: 'accordion', label: '折叠' },
  { value: 'sidebar', label: '纵向分类' },
  { value: 'tabs', label: '横向分类' },
]

const searchKeyword = ref('') // 搜索关键词
const menuGroups = ref<MenuGroup[]>([]) // 菜单分组列表
/** 常用服务菜单（从 store 中计算得出） */
const favoriteMenus = computed<MenuItem[]>(() => {
  const keys = userStore.favoriteMenus
  if (!keys || keys.length === 0) {
    return []
  }
  return keys.map(key => getMenuItemByKey(key)).filter(Boolean) as MenuItem[]
})

/** 过滤后的菜单分组 */
const filteredMenuGroups = computed(() => {
  if (!searchKeyword.value) {
    return menuGroups.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return menuGroups.value
    .map(group => ({
      ...group,
      menus: group.menus.filter(menu => menu.name.toLowerCase().includes(keyword)),
    }))
    .filter(group => group.menus.length > 0)
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 初始化 */
function initData() {
  menuGroups.value = getMenuGroups()
}

/** 处理添加常用服务 */
function handleAddFavorite(menu: MenuItem) {
  const keys = [...userStore.favoriteMenus]
  if (!keys.includes(menu.key)) {
    keys.push(menu.key)
    userStore.setFavoriteMenus(keys)
  }
  toast.success('已添加')
}

/** 处理移除常用服务 */
function handleRemoveFavorite(menu: MenuItem) {
  const keys = [...userStore.favoriteMenus]
  const index = keys.indexOf(menu.key)
  if (index > -1) {
    keys.splice(index, 1)
    userStore.setFavoriteMenus(keys)
  }
  toast.success('已移除')
}

/** 上下移动常用菜单 */
function moveFavorite(menu: MenuItem, dir: number) {
  const keys = [...userStore.favoriteMenus]
  const i = keys.indexOf(menu.key)
  const j = i + dir
  if (i < 0 || j < 0 || j >= keys.length) {
    return
  }
  const tmp = keys[i]
  keys[i] = keys[j]
  keys[j] = tmp
  userStore.setFavoriteMenus(keys)
}

/** 检查菜单是否已添加到常用服务 */
function isInFavorites(menu: MenuItem): boolean {
  return favoriteMenus.value.some(m => m.key === menu.key)
}

/** 滚动到菜单分组区域 */
function scrollToGroups() {
  uni.pageScrollTo({
    selector: '#menuGroups',
    duration: 300,
  })
}

onLoad(() => {
  initData()
})
</script>

<style lang="scss" scoped>
.section-header {
  padding: 24rpx 30rpx 16rpx;
}

.menu-list {
  padding: 0 30rpx 20rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  &__left {
    display: flex;
    align-items: center;
  }

  &__icon {
    width: 80rpx;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16rpx;
    margin-right: 24rpx;
  }

  &__name {
    font-size: 30rpx;
    color: #333;
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }
}

/* 圆形 加号/减号 切换按钮 */
.toggle-btn {
  position: relative;
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
}

.toggle-add {
  background: #3370ff;
}

.toggle-remove {
  background: #f2f3f5;
}

.toggle-bar-h,
.toggle-bar-v {
  position: absolute;
  top: 50%;
  left: 50%;
  border-radius: 2rpx;
}

.toggle-bar-h {
  width: 22rpx;
  height: 4rpx;
  transform: translate(-50%, -50%);
}

.toggle-bar-v {
  width: 4rpx;
  height: 22rpx;
  transform: translate(-50%, -50%);
}

.toggle-add .toggle-bar-h,
.toggle-add .toggle-bar-v {
  background: #fff;
}

.toggle-remove .toggle-bar-h {
  background: #bbb;
}
</style>
