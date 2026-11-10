<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="商品分类"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 面包屑导航（按层级钻取） -->
    <Breadcrumb ref="breadcrumbRef" v-model="currentParentId" root-name="全部分类" />

    <!-- 当前层级分类列表 -->
    <scroll-view scroll-y class="min-h-0 flex-1">
      <view class="p-24rpx">
        <view
          v-for="item in currentList"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="flex items-start gap-20rpx">
            <view v-if="item.picUrl" class="shrink-0">
              <wd-img :src="item.picUrl" width="112rpx" height="112rpx" radius="8rpx" mode="aspectFill" />
            </view>
            <view class="min-w-0 flex-1">
              <view class="mb-12rpx flex items-start justify-between gap-16rpx">
                <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
                  {{ item.name || '-' }}
                </view>
                <dict-tag v-if="item.status != null" :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
              </view>
              <view class="text-26rpx text-[#666]">
                排序：{{ item.sort ?? '-' }}
              </view>
            </view>
          </view>

          <!-- 子分类 / 查看商品 入口 -->
          <view class="mt-16rpx flex items-center justify-end gap-32rpx border-t border-[#f0f0f0] pt-16rpx">
            <view v-if="item.children?.length" class="flex items-center" @click.stop="handleEnterChildren(item)">
              <text class="text-26rpx text-[#1890ff]">子分类 ({{ item.children.length }})</text>
              <wd-icon name="arrow-right" size="12px" color="#1890ff" />
            </view>
            <!-- 只有叶子分类（无子分类）才有商品 -->
            <view v-else class="flex items-center" @click.stop="handleViewSpu(item)">
              <text class="text-26rpx text-[#1890ff]">查看商品</text>
              <wd-icon name="arrow-right" size="12px" color="#1890ff" />
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="!loading && currentList.length === 0" class="py-100rpx text-center">
          <wd-empty icon="content" tip="暂无分类数据" />
        </view>
      </view>
    </scroll-view>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['product:category:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { ProductCategory } from '@/api/mall/product/category'
import { onUnload } from '@dcloudio/uni-app'
import { computed, onMounted, ref } from 'vue'
import { getProductCategoryList } from '@/api/mall/product/category'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { findChildren, handleTree } from '@/utils/tree'
import Breadcrumb from './components/breadcrumb.vue'
import SearchForm from './components/search-form.vue'

// 前端 handleTree 构造的分类树节点（children 为前端拼接，非后端字段）
interface CategoryTreeNode extends ProductCategory {
  children?: CategoryTreeNode[]
}

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const loading = ref(false) // 列表加载状态
const list = ref<CategoryTreeNode[]>([]) // 完整分类列表（树形）
const queryParams = ref<Record<string, any>>({}) // 查询参数
const currentParentId = ref(0) // 当前层级父节点编号
const breadcrumbRef = ref<InstanceType<typeof Breadcrumb>>()
const currentList = computed(() => currentParentId.value === 0
  ? list.value.filter(item => item.parentId === 0)
  : findChildren(list.value, currentParentId.value)) // 当前层级分类

/** 返回上一页或上一层级 */
function handleBack() {
  if (!breadcrumbRef.value?.back()) {
    navigateBackPlus()
  }
}

/** 进入子分类层级 */
function handleEnterChildren(item: ProductCategory) {
  breadcrumbRef.value?.enter({ id: item.id!, name: item.name })
}

/** 查询分类列表 */
async function getList() {
  loading.value = true
  try {
    const data = await getProductCategoryList({ ...queryParams.value })
    list.value = handleTree(data) as CategoryTreeNode[]
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
function handleQuery(data?: Record<string, any>) {
  queryParams.value = { ...data }
  currentParentId.value = 0
  breadcrumbRef.value?.reset()
  getList()
}

/** 重置按钮操作 */
function handleReset() {
  handleQuery()
}

/** 新增分类 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mall/product/category/form/index' })
}

/** 查看详情 */
function handleDetail(item: ProductCategory) {
  uni.navigateTo({ url: `/pages-mall/product/category/detail/index?id=${item.id}` })
}

/** 查看该分类下的商品 */
function handleViewSpu(item: ProductCategory) {
  uni.navigateTo({ url: `/pages-mall/product/spu/index?categoryId=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  getList()
  uni.$on('mall:product-category:reload', getList)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:product-category:reload', getList)
})
</script>
