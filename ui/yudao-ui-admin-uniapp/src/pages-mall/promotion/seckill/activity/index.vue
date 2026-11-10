<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="秒杀活动"
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
      empty-view-text="暂无秒杀活动"
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
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name || `活动 #${item.id}` }}
            </view>
            <view class="flex shrink-0 items-center gap-12rpx">
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
              <text class="text-26rpx text-[#fa8c16]">{{ seckillPriceText(item) }}</text>
            </view>
          </view>
          <view class="text-26rpx text-[#666]">
            <text class="text-[#999]">开始时间：</text>{{ formatDateTime(item.startTime) || '-' }}
          </view>
          <view class="mt-4rpx text-26rpx text-[#666]">
            <text class="text-[#999]">结束时间：</text>{{ formatDateTime(item.endTime) || '-' }}
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['promotion:seckill-activity:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { PromotionSeckillActivity } from '@/api/mall/promotion/seckill'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getPromotionSeckillActivityPage } from '@/api/mall/promotion/seckill'
import { useAccess } from '@/hooks/useAccess'
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

const { hasAccessByCodes } = useAccess()
const list = ref<PromotionSeckillActivity[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 秒杀价：取商品中最低秒杀价（活动级 seckillPrice 列表接口不返回） */
function seckillPriceText(item: PromotionSeckillActivity) {
  const prices = (item.products || []).map(product => product.seckillPrice).filter((price): price is number => price != null)
  return prices.length ? formatDisplayMoney(Math.min(...prices)) : '-'
}

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询秒杀活动列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPromotionSeckillActivityPage({ ...queryParams.value, pageNo, pageSize })
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

/** 新增秒杀活动 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mall/promotion/seckill/activity/form/index' })
}

/** 查看详情 */
function handleDetail(item: PromotionSeckillActivity) {
  uni.navigateTo({ url: `/pages-mall/promotion/seckill/activity/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:promotion-seckill-activity:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:promotion-seckill-activity:reload', reload)
})
</script>
