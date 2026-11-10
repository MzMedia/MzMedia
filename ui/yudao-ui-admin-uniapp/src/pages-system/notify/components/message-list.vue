<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <MessageSearchForm @search="handleQuery" @reset="handleReset" />

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
      empty-view-text="暂无站内信消息数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="p-24rpx">
            <view class="mb-16rpx flex items-center justify-between">
              <view class="text-32rpx text-[#333] font-semibold">
                {{ item.templateNickname }}
              </view>
              <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="item.readStatus" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">用户类型：</text>
              <dict-tag :type="DICT_TYPE.USER_TYPE" :value="item.userType" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">用户编号：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.userId }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">模板编码：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.templateCode }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">模版类型：</text>
              <dict-tag :type="DICT_TYPE.SYSTEM_NOTIFY_TEMPLATE_TYPE" :value="item.templateType" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">模版内容：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.templateContent }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">模版参数：</text>
              <text class="min-w-0 flex-1 truncate">{{ formatTemplateParams(item.templateParams) }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">阅读时间：</text>
              <text>{{ formatDateTime(item.readTime) }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { NotifyMessage } from '@/api/system/notify/message'
import { ref } from 'vue'
import { getNotifyMessagePage } from '@/api/system/notify/message'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import MessageSearchForm from './message-search-form.vue'

const list = ref<NotifyMessage[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getNotifyMessagePage(params)
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

/** 格式化模版参数 */
function formatTemplateParams(params: any) {
  if (!params) {
    return '-'
  }
  try {
    return typeof params === 'string' ? params : JSON.stringify(params)
  } catch {
    return '-'
  }
}

/** 查看详情 */
function handleDetail(item: NotifyMessage) {
  uni.navigateTo({
    url: `/pages-system/notify/message/detail/index?id=${item.id}`,
  })
}
</script>
