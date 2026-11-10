<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <JobSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 同步任务 -->
    <view v-if="hasAccessByCodes(['infra:job:create'])" class="flex justify-end bg-white px-24rpx py-16rpx">
      <wd-button type="success" size="small" :loading="syncing" @click="handleSync">
        同步任务
      </wd-button>
    </view>

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
      empty-view-text="暂无定时任务数据"
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
                {{ item.name }}
              </view>
              <dict-tag :type="DICT_TYPE.INFRA_JOB_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">处理器名称：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.handlerName }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">处理器参数：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.handlerParam || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">CRON 表达式：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.cronExpression }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
            <!-- 查看日志按钮 -->
            <view class="flex justify-end -mt-8">
              <wd-button size="small" type="info" @click.stop="handleViewLog(item)">
                调度日志
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:job:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Job } from '@/api/infra/job'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, onUnmounted, ref } from 'vue'
import { getJobPage, syncJob } from '@/api/infra/job'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import JobSearchForm from './job-search-form.vue'

const emit = defineEmits<{
  viewLog: [jobId: number]
}>()

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const list = ref<Job[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数
const syncing = ref(false) // 同步任务状态

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getJobPage(params)
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

/** 新增 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-infra/job/job/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: Job) {
  uni.navigateTo({
    url: `/pages-infra/job/job/detail/index?id=${item.id}`,
  })
}

/** 查看调度日志 */
function handleViewLog(item: Job) {
  emit('viewLog', item.id)
}

/** 同步任务 */
async function handleSync() {
  syncing.value = true
  try {
    await syncJob()
    toast.success('同步成功')
    reload()
  } finally {
    syncing.value = false
  }
}

/** 初始化：监听增删改后刷新 */
onMounted(() => {
  uni.$on('infra:job:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:job:reload', reload)
})
</script>
