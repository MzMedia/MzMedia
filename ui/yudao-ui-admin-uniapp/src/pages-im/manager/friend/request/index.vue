<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="好友申请"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 好友申请列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无好友申请数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="flex items-center justify-between">
            <view class="line-clamp-1 flex-1 text-30rpx text-[#333] font-semibold">
              {{ item.fromNickname || `用户 ${item.fromUserId}` }}
              <text class="mx-8rpx text-[#bbb]">→</text>
              {{ item.toNickname || `用户 ${item.toUserId}` }}
            </view>
            <dict-tag :type="DICT_TYPE.IM_FRIEND_REQUEST_HANDLE_RESULT" :value="item.handleResult" />
          </view>
          <view v-if="item.applyContent" class="line-clamp-2 mt-10rpx text-26rpx text-[#666]">
            申请理由：{{ item.applyContent }}
          </view>
          <view class="mt-8rpx text-24rpx text-[#999]">
            <text v-if="item.addSource != null">来源：{{ getDictLabel(DICT_TYPE.IM_FRIEND_ADD_SOURCE, item.addSource) }}</text>
            <text v-if="item.displayName" class="ml-16rpx">备注：{{ item.displayName }}</text>
          </view>
          <view v-if="item.handleContent" class="mt-8rpx text-24rpx text-[#999]">
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
import type { ImManagerFriendRequestVO } from '@/api/im/manager/friend/request'
import { ref } from 'vue'
import { getManagerFriendRequestPage } from '@/api/im/manager/friend/request'
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

const list = ref<ImManagerFriendRequestVO[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询好友申请列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getManagerFriendRequestPage({
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
