<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="任务管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 任务列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无任务"
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
              <view class="mr-16rpx flex-1">
                <view class="line-clamp-1 text-32rpx text-[#333] font-semibold">
                  {{ item.processInstance?.name || '-' }}
                </view>
                <view class="mt-8rpx text-24rpx text-[#999]">
                  当前任务：{{ item.name }}
                </view>
              </view>
              <dict-tag :type="DICT_TYPE.BPM_TASK_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center">
              <view class="mr-8rpx h-48rpx w-48rpx flex items-center justify-center rounded-full bg-[#1890ff] text-20rpx text-white">
                {{ item.processInstance?.startUser?.nickname?.[0] || '?' }}
              </view>
              <view class="flex-1">
                <view class="text-28rpx text-[#333]">
                  发起人：{{ item.processInstance?.startUser?.nickname || '-' }}
                </view>
                <view class="text-24rpx text-[#999]">
                  审批人：{{ item.assigneeUser?.nickname || '-' }}
                </view>
              </view>
            </view>
            <view class="rounded-8rpx bg-[#f7f8f9] p-16rpx">
              <view class="mb-8rpx flex items-center justify-between text-26rpx">
                <text class="text-[#999]">任务开始时间</text>
                <text class="text-[#333]">{{ formatDateTime(item.createTime) }}</text>
              </view>
              <view v-if="item.endTime" class="mb-8rpx flex items-center justify-between text-26rpx">
                <text class="text-[#999]">任务结束时间</text>
                <text class="text-[#333]">{{ formatDateTime(item.endTime) }}</text>
              </view>
              <view v-if="item.reason" class="flex items-center justify-between text-26rpx">
                <text class="text-[#999]">审批建议</text>
                <text class="line-clamp-1 ml-16rpx flex-1 text-right text-[#333]">{{ item.reason }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { Task } from '@/api/bpm/task'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getTaskManagerPage } from '@/api/bpm/task'
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

const list = ref<Task[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询任务列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getTaskManagerPage(params)
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

/** 查看详情（历史） */
function handleDetail(item: Task) {
  if (item.processInstance?.id) {
    uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${item.processInstance.id}` })
  }
}

/** 初始化 */
onMounted(() => {
  uni.$on('bpm:task:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('bpm:task:reload', reload)
})
</script>
