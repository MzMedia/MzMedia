<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="砍价记录"
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
      empty-view-text="暂无砍价记录"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="mb-12rpx flex items-center justify-between gap-16rpx">
            <text class="text-30rpx text-[#333] font-semibold">砍价记录 #{{ item.id }}</text>
            <dict-tag v-if="item.status != null" :type="DICT_TYPE.PROMOTION_BARGAIN_RECORD_STATUS" :value="item.status" />
          </view>
          <view class="mb-8rpx flex items-center justify-between text-26rpx text-[#666]">
            <text>用户：{{ item.userId ?? '-' }}</text>
            <text>商品：{{ item.spuId ?? '-' }}</text>
          </view>
          <view class="flex items-center justify-between text-26rpx text-[#666]">
            <text>起始价：{{ formatDisplayMoney(item.bargainFirstPrice) }}</text>
            <text>已砍至：{{ formatDisplayMoney(item.bargainPrice) }}</text>
          </view>
          <view class="mt-8rpx text-24rpx text-[#999]">
            结束时间：{{ formatDateTime(item.endTime) || '-' }}
          </view>
          <view v-if="hasAccessByCodes(['promotion:bargain-help:query'])" class="mt-12rpx flex justify-end">
            <wd-button size="small" type="primary" variant="plain" @click="handleHelp(item)">
              查看助力
            </wd-button>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { PromotionBargainRecord } from '@/api/mall/promotion/bargain'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getPromotionBargainRecordPage } from '@/api/mall/promotion/bargain'
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
const list = ref<PromotionBargainRecord[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查看砍价助力 */
function handleHelp(item: PromotionBargainRecord) {
  uni.navigateTo({ url: `/pages-mall/promotion/bargain/help/index?recordId=${item.id}` })
}

/** 查询砍价记录列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPromotionBargainRecordPage({ ...queryParams.value, pageNo, pageSize })
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

/** 初始化 */
onMounted(() => {
  uni.$on('mall:promotion-bargain-record:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:promotion-bargain-record:reload', reload)
})
</script>
