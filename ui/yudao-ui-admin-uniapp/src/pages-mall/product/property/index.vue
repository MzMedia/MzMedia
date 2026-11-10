<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="商品属性"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 分页列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无属性数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-12rpx truncate text-32rpx text-[#333] font-semibold">
            {{ item.name || '-' }}
          </view>
          <view class="mb-12rpx truncate text-26rpx text-[#999]">
            {{ item.remark || '暂无备注' }}
          </view>
          <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">创建时间：</text>
            <text>{{ formatDateTime(item.createTime) || '-' }}</text>
          </view>
          <!-- 属性值入口 -->
          <view class="mt-16rpx flex items-center justify-end border-t border-[#f0f0f0] pt-16rpx">
            <view class="flex items-center" @click.stop="handleViewValues(item)">
              <text class="text-26rpx text-[#1890ff]">属性值</text>
              <wd-icon name="arrow-right" size="12px" color="#1890ff" />
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['product:property:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { ProductProperty } from '@/api/mall/product/property'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getProductPropertyPage } from '@/api/mall/product/property'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<ProductProperty[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询属性列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getProductPropertyPage({ ...queryParams.value, pageNo, pageSize })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 搜索按钮操作 */
function handleQuery(data?: Record<string, any>) {
  queryParams.value = { ...data }
  reload()
}

/** 重置按钮操作 */
function handleReset() {
  handleQuery()
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增属性 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mall/product/property/form/index' })
}

/** 查看详情 */
function handleDetail(item: ProductProperty) {
  uni.navigateTo({ url: `/pages-mall/product/property/detail/index?id=${item.id}` })
}

/** 查看该属性的属性值列表 */
function handleViewValues(item: ProductProperty) {
  uni.navigateTo({ url: `/pages-mall/product/property/value/index?propertyId=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:product-property:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:product-property:reload', reload)
})
</script>
