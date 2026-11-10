<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="售后管理"
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
      empty-view-text="暂无售后数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-30rpx text-[#333] font-semibold">
              {{ item.no || '-' }}
            </view>
            <dict-tag v-if="item.status != null" :type="DICT_TYPE.TRADE_AFTER_SALE_STATUS" :value="item.status" />
          </view>

          <view class="mb-16rpx text-36rpx text-[#fa8c16] font-semibold">
            {{ formatDisplayMoney(item.refundPrice) }}
          </view>

          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">售后方式：</text>
            <text>{{ item.way != null ? getDictLabel(DICT_TYPE.TRADE_AFTER_SALE_WAY, item.way) : '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">售后商品：</text>
            <text class="truncate">{{ item.spuName || item.orderNo || '-' }}</text>
          </view>
          <view class="flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">申请时间：</text>
            <text>{{ formatDateTime(item.createTime) || '-' }}</text>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { TradeAfterSale } from '@/api/mall/trade/after-sale'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getTradeAfterSalePage } from '@/api/mall/trade/after-sale'
import { getDictLabel } from '@/hooks/useDict'
import { formatDisplayMoney } from '@/utils/format'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const list = ref<TradeAfterSale[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询售后列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getTradeAfterSalePage({ ...queryParams.value, pageNo, pageSize })
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

/** 查看详情 */
function handleDetail(item: TradeAfterSale) {
  uni.navigateTo({ url: `/pages-mall/trade/after-sale/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:trade-after-sale:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:trade-after-sale:reload', reload)
})
</script>
