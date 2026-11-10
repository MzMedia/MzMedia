<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="API 错误日志"
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
            <view class="mb-16rpx flex items-center justify-between">
              <view class="line-clamp-1 mr-16rpx flex-1 text-28rpx text-[#333] font-semibold">
                {{ item.exceptionName || '-' }}
              </view>
              <dict-tag :type="DICT_TYPE.INFRA_API_ERROR_LOG_PROCESS_STATUS" :value="item.processStatus" />
            </view>
            <view class="mb-12rpx flex text-26rpx text-[#666]">
              <text class="mr-8rpx flex-shrink-0 text-[#999]">请求：</text>
              <text class="line-clamp-2 break-all">{{ item.requestMethod }} {{ item.requestUrl }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">应用名：</text>
              <text>{{ item.applicationName || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-26rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">用户编号：</text>
              <text>{{ item.userId ?? '-' }}</text>
            </view>
            <view class="flex items-center text-24rpx text-[#999]">
              <text>{{ formatDateTime(item.exceptionTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ApiErrorLog } from '@/api/infra/api-error-log'
import { onMounted, onUnmounted, ref } from 'vue'
import { getApiErrorLogPage } from '@/api/infra/api-error-log'
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

const list = ref<ApiErrorLog[]>([]) // 列表数据
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
    const data = await getApiErrorLogPage(params)
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
function handleDetail(item: ApiErrorLog) {
  uni.navigateTo({
    url: `/pages-infra/api-error-log/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('infra:api-error-log:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:api-error-log:reload', reload)
})
</script>
