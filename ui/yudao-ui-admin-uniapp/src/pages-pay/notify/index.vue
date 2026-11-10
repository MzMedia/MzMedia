<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="支付通知"
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
      empty-view-text="暂无支付通知数据"
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
                通知任务 #{{ item.id }}
              </view>
              <view v-if="item.appName" class="mt-6rpx truncate text-24rpx text-[#999]">
                支付应用：{{ item.appName }}
              </view>
            </view>
            <dict-tag v-if="item.status != null" :type="DICT_TYPE.PAY_NOTIFY_STATUS" :value="item.status" />
          </view>

          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">通知类型：</text>
            <dict-tag v-if="item.type != null" :type="DICT_TYPE.PAY_NOTIFY_TYPE" :value="item.type" />
            <text v-else>-</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">关联编号：</text>
            <text>{{ item.dataId ?? '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">商户单号：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.merchantOrderId || item.merchantRefundId || item.merchantTransferId || '-' }}</text>
          </view>
          <view class="flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">下次通知：</text>
            <text>{{ formatDateTime(item.nextNotifyTime) || '-' }}</text>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { PayNotifyTask } from '@/api/pay/notify'
import { ref } from 'vue'
import { getPayNotifyTaskPage } from '@/api/pay/notify'
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

const list = ref<PayNotifyTask[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询支付通知列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPayNotifyTaskPage({ ...queryParams.value, pageNo, pageSize })
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
function handleDetail(item: PayNotifyTask) {
  uni.navigateTo({ url: `/pages-pay/notify/detail/index?id=${item.id}` })
}
</script>
