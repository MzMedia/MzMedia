<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="产品分类"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索框 -->
    <view @click="searchVisible = true">
      <wd-search placeholder="搜索产品分类" hide-cancel disabled />
    </view>

    <!-- 搜索弹窗 -->
    <wd-popup v-model="searchVisible" position="top" :custom-style="getTopPopupStyle()" :modal-style="getTopPopupModalStyle()" @close="searchVisible = false">
      <view class="yd-search-form-container">
        <view class="yd-search-form-item">
          <view class="yd-search-form-label">
            分类名称
          </view>
          <wd-input v-model="searchName" placeholder="请输入分类名称" clearable />
        </view>
        <view class="yd-search-form-actions">
          <wd-button class="flex-1" variant="plain" @click="handleReset">
            重置
          </wd-button>
          <wd-button class="flex-1" type="primary" @click="handleSearch">
            搜索
          </wd-button>
        </view>
      </view>
    </wd-popup>

    <!-- 面包屑导航（浏览态下钻显示） -->
    <Breadcrumb v-if="!queryName" ref="breadcrumbRef" v-model="currentParentId" />

    <!-- 产品分类列表 -->
    <view class="p-24rpx">
      <view
        v-for="item in currentList"
        :key="item.id"
        class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
      >
        <view class="p-24rpx" @click="handleDetail(item)">
          <view class="flex items-center justify-between gap-16rpx">
            <view class="min-w-0 flex items-center">
              <view class="mr-16rpx h-48rpx w-48rpx flex shrink-0 items-center justify-center rounded-8rpx bg-[#1890ff]">
                <wd-icon name="folder" size="20px" color="#fff" />
              </view>
              <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
                {{ item.name }}
              </view>
            </view>
            <!-- 子分类入口（浏览态且有子级时展示） -->
            <view
              v-if="!queryName && item.children && item.children.length > 0"
              class="flex shrink-0 items-center"
              @click.stop="handleEnterChildren(item)"
            >
              <text class="text-24rpx text-[#1890ff]">子分类 ({{ item.children.length }})</text>
              <wd-icon name="arrow-right" size="12px" color="#1890ff" />
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && currentList.length === 0" class="py-100rpx text-center">
        <wd-empty icon="content" tip="暂无产品分类数据" />
      </view>
    </view>

    <!-- 新增按钮 -->
    <wd-fab v-if="hasAccessByCodes(['crm:product-category:create'])" position="right-bottom" type="primary" :expandable="false" @click="handleAdd" />
  </view>
</template>

<script lang="ts" setup>
import type { ProductCategory } from '@/api/crm/product/category'
import { onUnload } from '@dcloudio/uni-app'
import { computed, onMounted, ref } from 'vue'
import { getProductCategoryList } from '@/api/crm/product/category'
import { useAccess } from '@/hooks/useAccess'
import { getTopPopupModalStyle, getTopPopupStyle, navigateBackPlus } from '@/utils'
import { findChildren, handleTree } from '@/utils/tree'
import Breadcrumb from './components/breadcrumb.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const loading = ref(false) // 列表加载状态
const flatList = ref<ProductCategory[]>([]) // 完整扁平分类列表
const list = ref<ProductCategory[]>([]) // 树形分类列表
const currentParentId = ref(0) // 当前层级的父节点编号
const breadcrumbRef = ref<InstanceType<typeof Breadcrumb>>() // 面包屑引用
const searchVisible = ref(false) // 搜索弹窗显示状态
const searchName = ref('') // 搜索输入
const queryName = ref('') // 已生效的搜索条件

const currentList = computed(() => {
  // 搜索态：扁平展示所有名称匹配的分类
  if (queryName.value) {
    return flatList.value.filter(item => item.name?.includes(queryName.value))
  }
  // 浏览态：按层级下钻
  if (currentParentId.value === 0) {
    return list.value.filter(item => (item.parentId ?? 0) === 0)
  }
  return findChildren(list.value, currentParentId.value)
}) // 当前层级展示的分类列表

/** 返回上一页或上一层级 */
function handleBack() {
  if (queryName.value || !breadcrumbRef.value?.back()) {
    navigateBackPlus()
  }
}

/** 进入子分类层级 */
function handleEnterChildren(item: ProductCategory) {
  breadcrumbRef.value?.enter({ id: item.id!, name: item.name })
}

/** 查询产品分类列表（无分页，一次性返回全部，前端构树） */
async function getList() {
  loading.value = true
  try {
    const data = await getProductCategoryList()
    flatList.value = data
    list.value = handleTree(data)
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
function handleSearch() {
  queryName.value = searchName.value
  searchVisible.value = false
  // 重置层级与面包屑
  currentParentId.value = 0
}

/** 重置按钮操作 */
function handleReset() {
  searchName.value = ''
  queryName.value = ''
  searchVisible.value = false
  currentParentId.value = 0
}

/** 新增产品分类 */
function handleAdd() {
  uni.navigateTo({ url: `/pages-crm/product/category/form/index?parentId=${currentParentId.value}` })
}

/** 查看详情 */
function handleDetail(item: ProductCategory) {
  uni.navigateTo({ url: `/pages-crm/product/category/detail/index?id=${item.id}` })
}

/** 重新加载 */
function reload() {
  getList()
}

/** 初始化 */
onMounted(() => {
  getList()
  uni.$on('crm:productCategory:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('crm:productCategory:reload', reload)
})
</script>
