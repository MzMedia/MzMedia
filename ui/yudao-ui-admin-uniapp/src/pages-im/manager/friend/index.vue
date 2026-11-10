<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="好友关系"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 好友关系列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无好友关系数据"
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
              {{ item.userNickname || `用户 ${item.userId}` }}
              <text class="mx-8rpx text-[#bbb]">→</text>
              {{ item.friendNickname || `用户 ${item.friendUserId}` }}
            </view>
            <dict-tag :type="DICT_TYPE.IM_FRIEND_STATUS" :value="item.status" />
          </view>
          <view class="mt-10rpx text-26rpx text-[#999]">
            备注：{{ item.displayName || '-' }}
            <text v-if="item.addSource != null" class="ml-16rpx">来源：{{ getDictLabel(DICT_TYPE.IM_FRIEND_ADD_SOURCE, item.addSource) }}</text>
          </view>
          <view v-if="item.silent || item.pinned || item.blocked" class="mt-12rpx flex flex-wrap gap-12rpx">
            <text v-if="item.silent" class="rounded-6rpx bg-[#f0f2f5] px-12rpx py-4rpx text-22rpx text-[#666]">免打扰</text>
            <text v-if="item.pinned" class="rounded-6rpx bg-[#e6f4ff] px-12rpx py-4rpx text-22rpx text-[#1677ff]">置顶</text>
            <text v-if="item.blocked" class="rounded-6rpx bg-[#fff1f0] px-12rpx py-4rpx text-22rpx text-[#f5222d]">已拉黑</text>
          </view>
          <view class="mt-16rpx flex items-center justify-between">
            <text class="text-24rpx text-[#999]">{{ formatDateTime(item.addTime) || '-' }}</text>
            <wd-button
              v-if="hasAccessByCodes(['im:manager:message:query'])"
              size="small" variant="plain" @click="handleViewChat(item)"
            >
              查看对话
            </wd-button>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ImManagerFriendVO } from '@/api/im/manager/friend'
import { ref } from 'vue'
import { getManagerFriendPage } from '@/api/im/manager/friend'
import { getDictLabel } from '@/hooks/useDict'
import { useAccess } from '@/hooks/useAccess'
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
const list = ref<ImManagerFriendVO[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询好友关系列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getManagerFriendPage({
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

/** 查看对话 */
function handleViewChat(item: ImManagerFriendVO) {
  uni.navigateTo({
    url: `/pages-im/manager/message/private/index?senderId=${item.userId}&receiverId=${item.friendUserId}`,
  })
}
</script>
