<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="加群申请"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 加群申请列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无加群申请数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="flex items-center justify-between">
            <text class="line-clamp-1 flex-1 text-30rpx text-[#333] font-semibold">{{ item.groupName || `群 ${item.groupId}` }}</text>
            <dict-tag :type="DICT_TYPE.IM_GROUP_REQUEST_HANDLE_RESULT" :value="item.handleResult" />
          </view>
          <view class="mt-10rpx text-26rpx text-[#666]">
            申请人：{{ item.userNickname || `用户 ${item.userId}` }}
          </view>
          <view class="mt-6rpx text-26rpx text-[#999]">
            邀请人：{{ item.inviterUserId ? (item.inviterNickname || `用户 ${item.inviterUserId}`) : '主动申请' }}
            <text v-if="item.addSource != null" class="ml-16rpx">来源：{{ getDictLabel(DICT_TYPE.IM_GROUP_ADD_SOURCE, item.addSource) }}</text>
          </view>
          <view v-if="item.applyContent" class="line-clamp-2 mt-6rpx text-26rpx text-[#666]">
            申请理由：{{ item.applyContent }}
          </view>
          <view v-if="item.handleContent" class="mt-6rpx text-24rpx text-[#999]">
            处理理由：{{ item.handleContent }}
          </view>
          <view class="mt-12rpx flex items-center justify-between text-24rpx text-[#999]">
            <text>申请 {{ formatDateTime(item.createTime) }}</text>
            <text v-if="item.handleTime">处理 {{ formatDateTime(item.handleTime) }}</text>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ImManagerGroupRequestVO } from '@/api/im/manager/group/request'
import { ref } from 'vue'
import { getManagerGroupRequestPage } from '@/api/im/manager/group/request'
import { getDictLabel } from '@/hooks/useDict'
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

const list = ref<ImManagerGroupRequestVO[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询加群申请列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getManagerGroupRequestPage({
      ...queryParams.value,
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
</script>
