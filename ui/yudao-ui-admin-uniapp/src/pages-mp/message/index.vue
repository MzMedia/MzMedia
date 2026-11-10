<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号消息"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 公众号选择 -->
    <AccountPicker v-model="accountId" @change="handleAccountChange" />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 消息列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号消息数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="mb-16rpx flex items-center justify-between gap-16rpx">
            <view class="min-w-0 flex-1">
              <view class="truncate text-30rpx text-[#333] font-semibold">
                {{ item.openid || '-' }}
              </view>
              <view class="text-24rpx text-[#999]">
                {{ formatDateTime(item.createTime) || '-' }}
              </view>
            </view>
            <view class="flex flex-col items-end gap-8rpx">
              <dict-tag :type="DICT_TYPE.MP_MESSAGE_TYPE" :value="item.type" />
              <wd-tag :type="item.sendFrom === 1 ? 'success' : 'info'" plain>
                {{ item.sendFrom === 1 ? '粉丝' : '公众号' }}
              </wd-tag>
            </view>
          </view>
          <view class="mb-20rpx rounded-8rpx bg-[#f8f8f8] p-20rpx text-28rpx text-[#333]">
            <ReplyContent
              :type="item.type"
              :content="item.content"
              :media-url="item.mediaUrl"
              :recognition="item.recognition"
              :format="item.format"
              :title="item.title"
              :description="item.description"
              :url="item.url"
              :event="item.event"
              :event-key="item.eventKey"
              :articles="item.articles"
              :thumb-media-url="item.thumbMediaUrl"
              :music-url="item.musicUrl"
              :hq-music-url="item.hqMusicUrl"
              :location-x="item.locationX"
              :location-y="item.locationY"
              :scale="item.scale"
              :label="item.label"
            />
          </view>
          <wd-button
            v-if="hasAccessByCodes(['mp:message:send']) && item.userId"
            size="small"
            type="primary"
            block
            @click.stop="handleOpenChat(item)"
          >
            消息会话
          </wd-button>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { MpMessage } from '@/api/mp/message'
import { ref } from 'vue'
import { getMessagePage } from '@/api/mp/message'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import AccountPicker from '@/pages-mp/account/components/account-picker.vue'
import ReplyContent from '../components/reply-content.vue'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const accountId = ref<number>() // 当前公众号编号
const list = ref<MpMessage[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  reload()
}

/** 查询消息列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!accountId.value) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const data = await getMessagePage({
      ...queryParams.value,
      accountId: accountId.value,
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

/** 进入消息会话 */
function handleOpenChat(item: MpMessage) {
  const params = [
    `userId=${item.userId}`,
    `accountId=${item.accountId || accountId.value || ''}`,
    `openid=${encodeURIComponent(item.openid || '')}`,
  ].join('&')
  uni.navigateTo({ url: `/pages-mp/message/chat/index?${params}` })
}
</script>
