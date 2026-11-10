<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="产品管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 产品列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无产品数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name }}
            </view>
            <view class="flex flex-wrap justify-end gap-8rpx">
              <dict-tag v-if="item.status !== undefined && item.status !== null" :type="DICT_TYPE.CRM_PRODUCT_STATUS" :value="item.status" />
              <dict-tag v-if="item.unit !== undefined && item.unit !== null" :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="item.unit" />
            </view>
          </view>
          <view v-if="item.no" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">编码：</text>{{ item.no }}
          </view>
          <view v-if="item.categoryName" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">分类：</text>{{ item.categoryName }}
          </view>
          <view v-if="item.price !== undefined && item.price !== null" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">价格：</text>{{ formatMoney(item.price) }}
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab v-if="hasAccessByCodes(['crm:product:create'])" position="right-bottom" type="primary" :expandable="false" @click="handleAdd" />
  </view>
</template>

<script lang="ts" setup>
import type { Product } from '@/api/crm/product'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getProductPage } from '@/api/crm/product'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatMoney } from '@/utils/format'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<Product[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询产品列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getProductPage({ ...queryParams.value, pageNo, pageSize })
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

/** 新增产品 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-crm/product/form/index' })
}

/** 查看详情 */
function handleDetail(item: Product) {
  uni.navigateTo({ url: `/pages-crm/product/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('crm:product:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('crm:product:reload', reload)
})
</script>
