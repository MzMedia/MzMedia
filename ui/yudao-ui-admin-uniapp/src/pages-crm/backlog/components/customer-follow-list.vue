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
      empty-view-text="暂无待跟进客户"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name || '-' }}
            </view>
            <view class="flex flex-wrap justify-end gap-8rpx">
              <dict-tag v-if="item.level != null" :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="item.level" />
              <dict-tag v-if="item.source != null" :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="item.source" />
            </view>
          </view>
          <view v-if="item.mobile" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">手机：</text>{{ item.mobile }}
          </view>
          <view v-if="item.ownerUserName" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">负责人：</text>{{ item.ownerUserName }}
          </view>
          <view v-if="item.contactNextTime" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">下次联系：</text>{{ formatDateTime(item.contactNextTime) }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { getCustomerPage } from '@/api/crm/customer'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import BacklogFilter from './backlog-filter.vue'
import { buildDefaultFilters, FOLLOWUP_STATUS } from './common'

const filters = [
  { prop: 'followUpStatus', label: '状态', options: FOLLOWUP_STATUS },
]
const list = ref<Record<string, any>[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const filterValues = ref<Record<string, any>>(buildDefaultFilters(filters)) // 筛选值

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getCustomerPage({ pageNo, pageSize, sceneType: 1, ...filterValues.value })
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
  uni.navigateTo({ url: `/pages-crm/customer/detail/index?id=${item.id}` })
}
</script>
