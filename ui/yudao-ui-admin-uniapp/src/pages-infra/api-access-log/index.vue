<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="API 访问日志"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 日志列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无日志数据"
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
            <view class="mb-16rpx flex items-center justify-between gap-16rpx">
              <view class="min-w-0 flex-1 truncate text-28rpx text-[#333] font-semibold">
                {{ item.requestMethod }} {{ item.requestUrl }}
              </view>
              <view class="flex-shrink-0">
                <wd-tag v-if="item.resultCode === 0" type="success" variant="plain">
                  成功
                </wd-tag>
                <wd-tag v-else type="danger" variant="plain">
                  失败
                </wd-tag>
              </view>
            </view>
            <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">应用名：</text>
              <text>{{ item.applicationName || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">用户编号：</text>
              <text>{{ item.userId ?? '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">执行时长：</text>
              <text>{{ item.duration != null ? `${item.duration} ms` : '-' }}</text>
            </view>
            <view v-if="item.operateName" class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">操作名：</text>
              <text class="line-clamp-1">{{ item.operateName }}</text>
            </view>
            <view class="flex items-center text-24rpx text-[#999]">
              <text>{{ formatDateTime(item.beginTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ApiAccessLog } from '@/api/infra/api-access-log'
import { ref } from 'vue'
import { getApiAccessLogPage } from '@/api/infra/api-access-log'
import { navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const list = ref<ApiAccessLog[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询日志列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getApiAccessLogPage(params)
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
function handleDetail(item: ApiAccessLog) {
  uni.navigateTo({
    url: `/pages-infra/api-access-log/detail/index?id=${item.id}`,
  })
}
</script>
