<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="优惠券"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 状态筛选 -->
    <view class="bg-white">
      <wd-tabs v-model="statusTabIndex" @change="reload">
        <wd-tab title="全部" />
        <wd-tab
          v-for="dict in statusOptions"
          :key="dict.value"
          :title="dict.label"
        />
      </wd-tabs>
    </view>

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
      empty-view-text="暂无优惠券"
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
              {{ item.name || `优惠券 #${item.id}` }}
            </view>
            <dict-tag v-if="item.status != null" :type="DICT_TYPE.PROMOTION_COUPON_STATUS" :value="item.status" />
          </view>

          <view class="mb-12rpx flex items-center justify-between text-26rpx text-[#666]">
            <text>门槛：{{ formatDisplayMoney(item.usePrice) }}</text>
            <text>优惠：{{ formatDisplayMoney(item.discountPrice) }}</text>
          </view>
          <view class="flex items-center text-26rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">用户编号：</text>
            <text>{{ item.userId ?? '-' }}</text>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { PromotionCoupon } from '@/api/mall/promotion/coupon/coupon'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getPromotionCouponPage } from '@/api/mall/promotion/coupon/coupon'
import { getIntDictOptions } from '@/hooks/useDict'
import { formatDisplayMoney } from '@/utils/format'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const list = ref<PromotionCoupon[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数（昵称 + 领取时间）
const statusOptions = getIntDictOptions(DICT_TYPE.PROMOTION_COUPON_STATUS) // 优惠券状态选项
const statusTabIndex = ref(0) // 状态筛选 tab 下标（0 全部，其余对应 statusOptions）

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询优惠券列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPromotionCouponPage({
      ...queryParams.value,
      status: statusTabIndex.value === 0 ? undefined : statusOptions[statusTabIndex.value - 1]?.value,
      pageNo,
      pageSize,
    })
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
function handleDetail(item: PromotionCoupon) {
  uni.navigateTo({ url: `/pages-mall/promotion/coupon/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:promotion-coupon:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:promotion-coupon:reload', reload)
})
</script>
