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
      empty-view-text="暂无待审核合同"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name || '-' }}
            </view>
            <dict-tag v-if="item.auditStatus != null" :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="item.auditStatus" />
          </view>
          <view v-if="item.no" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">编号：</text>{{ item.no }}
          </view>
          <view v-if="item.customerName" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">客户：</text>{{ item.customerName }}
          </view>
          <view v-if="item.totalPrice != null" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.totalPrice) }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { getContractPage } from '@/api/crm/contract'
import { DICT_TYPE } from '@/utils/constants'
import { formatMoney } from '@/utils/format'
import BacklogFilter from './backlog-filter.vue'
import { AUDIT_STATUS, buildDefaultFilters } from './common'

const list = ref<Record<string, any>[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const filters = [
  { prop: 'auditStatus', label: '合同状态', options: AUDIT_STATUS },
]
const filterValues = ref<Record<string, any>>(buildDefaultFilters(filters)) // 筛选值

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getContractPage({ pageNo, pageSize, sceneType: 1, ...filterValues.value })
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
  uni.navigateTo({ url: `/pages-crm/contract/detail/index?id=${item.id}` })
}
</script>
