<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="转账单"
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
      empty-view-text="暂无转账单数据"
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
            <view class="min-w-0 flex-1">
              <view class="truncate text-32rpx text-[#333] font-semibold">
                {{ item.subject || item.merchantTransferId || `转账 #${item.id}` }}
              </view>
              <view v-if="item.no" class="mt-6rpx truncate text-24rpx text-[#999]">
                转账单号：{{ item.no }}
              </view>
            </view>
            <dict-tag v-if="item.status != null" :type="DICT_TYPE.PAY_TRANSFER_STATUS" :value="item.status" />
          </view>

          <view class="mb-16rpx text-36rpx text-[#fa8c16] font-semibold">
            {{ formatDisplayMoney(item.price) }}
          </view>

          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">转账渠道：</text>
            <dict-tag v-if="item.channelCode" :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="item.channelCode" />
            <text v-else>-</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">收款人：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.userName || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">收款账号：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.userAccount || '-' }}</text>
          </view>
          <view class="flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">成功时间：</text>
            <text>{{ formatDateTime(item.successTime) || '-' }}</text>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { PayTransfer } from '@/api/pay/transfer'
import { ref } from 'vue'
import { getPayTransferPage } from '@/api/pay/transfer'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import { formatDisplayMoney } from '@/utils/format'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const list = ref<PayTransfer[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询转账单列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPayTransferPage({ ...queryParams.value, pageNo, pageSize })
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
function handleDetail(item: PayTransfer) {
  uni.navigateTo({ url: `/pages-pay/transfer/detail/index?id=${item.id}` })
}
</script>
