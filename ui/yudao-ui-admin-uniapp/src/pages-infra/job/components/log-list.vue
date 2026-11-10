<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <LogSearchForm :job-id="jobId" @search="handleQuery" @reset="handleReset" />

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
      empty-view-text="暂无调度日志数据"
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
                {{ item.handlerName }}
              </view>
              <dict-tag :type="DICT_TYPE.INFRA_JOB_LOG_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">任务编号：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.jobId }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">处理器参数：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.handlerParam || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">执行时长：</text>
              <text>{{ item.duration != null ? `${item.duration} ms` : '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">开始时间：</text>
              <text>{{ formatDateTime(item.beginTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { JobLog } from '@/api/infra/job/log'
import { ref, watch } from 'vue'
import { getJobLogPage } from '@/api/infra/job/log'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import LogSearchForm from './log-search-form.vue'

const props = defineProps<{
  jobId?: number
}>()

const list = ref<JobLog[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      jobId: queryParams.value.jobId || props.jobId,
      pageNo,
      pageSize,
    }
    const data = await getJobLogPage(params)
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
function handleDetail(item: JobLog) {
  uni.navigateTo({
    url: `/pages-infra/job/log/detail/index?id=${item.id}`,
  })
}

/** 监听外部 jobId 变化，同步到搜索态并重新查询 */
watch(
  () => props.jobId,
  () => {
    queryParams.value.jobId = props.jobId
    if (props.jobId) {
      reload()
    }
  },
)
</script>
