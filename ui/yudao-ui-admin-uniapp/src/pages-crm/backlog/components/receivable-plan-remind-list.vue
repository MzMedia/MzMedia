<template>
  <view class="min-h-0 flex flex-1 flex-col">
    <BacklogFilter v-model="filterValues" :filters="filters" @change="reload" />
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无待回款提醒"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.customerName || '-' }}
            </view>
            <dict-tag v-if="item.returnType != null" :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE" :value="item.returnType" />
          </view>
          <view v-if="item.contractNo" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">合同编号：</text>{{ item.contractNo }}
          </view>
          <view v-if="item.period != null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">期数：</text>{{ item.period }}
          </view>
          <view v-if="item.price != null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.price) }}
          </view>
          <view v-if="item.returnTime" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">日期：</text>{{ formatDate(item.returnTime) }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { getReceivablePlanPage } from '@/api/crm/receivable/plan'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate } from '@/utils/date'
import { formatMoney } from '@/utils/format'
import BacklogFilter from './backlog-filter.vue'
import { buildDefaultFilters, RECEIVABLE_REMIND_TYPE } from './common'

const filters = [
  { prop: 'remindType', label: '提醒类型', options: RECEIVABLE_REMIND_TYPE },
]
const list = ref<Record<string, any>[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const filterValues = ref<Record<string, any>>(buildDefaultFilters(filters)) // 筛选值

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getReceivablePlanPage({ pageNo, pageSize, ...filterValues.value })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 查看详情 */
function handleDetail(item: Record<string, any>) {
  uni.navigateTo({ url: `/pages-crm/receivable-plan/detail/index?id=${item.id}` })
}
</script>
