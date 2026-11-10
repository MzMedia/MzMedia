<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="示例分类"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索 -->
    <wd-search
      v-model="searchName"
      placeholder="搜索分类名称"
      hide-cancel
      @search="handleSearch"
      @clear="handleClear"
    />

    <!-- 面包屑导航 -->
    <Breadcrumb ref="breadcrumbRef" v-model="currentParentId" />

    <!-- 分类列表 -->
    <view class="p-24rpx">
      <view
        v-for="item in currentList"
        :key="item.id"
        class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
      >
        <view class="flex items-center p-24rpx" @click="handleDetail(item)">
          <view class="mr-16rpx h-48rpx w-48rpx flex flex-shrink-0 items-center justify-center rounded-8rpx bg-[#1890ff]">
            <wd-icon name="folder" size="20px" color="#fff" />
          </view>
          <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
            {{ item.name }}
          </view>
          <view
            v-if="item.children && item.children.length > 0"
            class="ml-16rpx flex flex-shrink-0 items-center"
            @click.stop="handleEnterChildren(item)"
          >
            <text class="text-24rpx text-[#1890ff]">子分类 ({{ item.children.length }})</text>
            <wd-icon name="arrow-right" size="12px" color="#1890ff" />
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && currentList.length === 0" class="py-100rpx text-center">
        <wd-empty icon="content" tip="暂无示例分类数据" />
      </view>
    </view>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:demo02-category:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Demo02Category } from '@/api/infra/demo/demo02'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { getDemo02CategoryList } from '@/api/infra/demo/demo02'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { findChildren, handleTree } from '@/utils/tree'
import Breadcrumb from './components/breadcrumb.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const loading = ref(false) // 加载状态
const searchName = ref('') // 名称搜索
const list = ref<Demo02Category[]>([]) // 完整分类列表（树形结构）
const currentParentId = ref(0) // 当前层级的父节点编号
const breadcrumbRef = ref<InstanceType<typeof Breadcrumb>>()

/** 当前层级的分类列表 */
const currentList = computed(() => {
  if (currentParentId.value === 0) {
    return list.value.filter(item => item.parentId === 0)
  }
  return findChildren(list.value, currentParentId.value)
})

/** 返回上一页或上一层级 */
function handleBack() {
  if (!breadcrumbRef.value?.back()) {
    navigateBackPlus()
  }
}

/** 查询分类列表 */
async function getList() {
  loading.value = true
  try {
    const data = await getDemo02CategoryList({ name: searchName.value || undefined })
    list.value = handleTree(data)
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleSearch() {
  currentParentId.value = 0
  breadcrumbRef.value?.reset()
  getList()
}

/** 清空搜索 */
function handleClear() {
  searchName.value = ''
  handleSearch()
}

/** 进入子层级 */
function handleEnterChildren(item: Demo02Category) {
  breadcrumbRef.value?.enter({ id: item.id!, name: item.name })
}

/** 新增 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-infra/demo/demo02/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: Demo02Category) {
  uni.navigateTo({
    url: `/pages-infra/demo/demo02/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  getList()
  uni.$on('infra:demo02-category:reload', getList)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:demo02-category:reload', getList)
})
</script>
