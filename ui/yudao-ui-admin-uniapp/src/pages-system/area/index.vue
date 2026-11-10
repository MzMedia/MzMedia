<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="地区管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 面包屑导航 -->
    <Breadcrumb ref="breadcrumbRef" v-model="currentParentId" />

    <!-- 地区列表 -->
    <view class="p-24rpx">
      <!-- 加载中 -->
      <view v-if="loading" class="py-100rpx text-center">
        <wd-loading />
      </view>
      <template v-else>
        <view
          v-for="item in currentList"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
          @click="handleEnter(item)"
        >
          <view class="flex items-center justify-between p-24rpx">
            <view class="flex items-center">
              <view class="mr-16rpx h-48rpx w-48rpx flex items-center justify-center rounded-8rpx bg-[#1890ff]">
                <wd-icon name="location" size="20px" color="#fff" />
              </view>
              <view>
                <view class="text-32rpx text-[#333] font-semibold">
                  {{ item.name }}
                </view>
                <view class="mt-4rpx text-24rpx text-[#999]">
                  编码：{{ item.id }}
                </view>
              </view>
            </view>
            <view
              v-if="item.children && item.children.length > 0"
              class="flex items-center"
            >
              <text class="text-24rpx text-[#1890ff]">下级 ({{ item.children.length }})</text>
              <wd-icon name="arrow-right" size="12px" color="#1890ff" />
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="currentList.length === 0" class="py-100rpx text-center">
          <wd-empty icon="content" tip="暂无地区数据" />
        </view>
      </template>
    </view>

    <!-- IP 查询按钮 -->
    <wd-fab
      position="right-bottom"
      type="primary"
      :expandable="false"
      icon="search"
      @click="handleOpenIpQuery"
    />

    <!-- IP 查询弹窗 -->
    <IpQueryForm v-model="showIpQuery" />
  </view>
</template>

<script lang="ts" setup>
import type { Area } from '@/api/system/area'
import { computed, onMounted, ref } from 'vue'
import { getAreaTree } from '@/api/system/area'
import { navigateBackPlus } from '@/utils'
import { findChildren } from '@/utils/tree'
import Breadcrumb from './components/breadcrumb.vue'
import IpQueryForm from './components/ip-query-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const loading = ref(false) // 地区树加载状态
const areaList = ref<Area[]>([]) // 完整地区树数据
const showIpQuery = ref(false) // 是否显示 IP 查询弹窗

const currentParentId = ref(0) // 当前层级的父节点编号
const currentList = computed(() => {
  if (currentParentId.value === 0) {
    return areaList.value
  }
  return findChildren(areaList.value, currentParentId.value)
}) // 当前层级的地区列表
const breadcrumbRef = ref<InstanceType<typeof Breadcrumb>>()

/** 返回上一页或上一层级 */
function handleBack() {
  if (!breadcrumbRef.value?.back()) {
    navigateBackPlus()
  }
}

/** 进入子层级 */
function handleEnter(item: Area) {
  if (!item.children || item.children.length === 0) {
    return
  }
  breadcrumbRef.value?.enter({ id: item.id, name: item.name })
}

/** 获取地区树 */
async function getList() {
  loading.value = true
  try {
    areaList.value = await getAreaTree()
  } finally {
    loading.value = false
  }
}

/** 打开 IP 查询弹窗 */
function handleOpenIpQuery() {
  showIpQuery.value = true
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
