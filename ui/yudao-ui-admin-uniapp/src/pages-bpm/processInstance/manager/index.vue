<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="流程管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 流程实例列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无流程实例"
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
                  {{ item.name }}
                </view>
                <view class="mt-8rpx text-24rpx text-[#999]">
                  {{ item.categoryName || '-' }}
                </view>
              </view>
              <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center">
              <view class="mr-8rpx h-48rpx w-48rpx flex items-center justify-center rounded-full bg-[#1890ff] text-20rpx text-white">
                {{ item.startUser?.nickname?.[0] || '?' }}
              </view>
              <view class="flex-1">
                <view class="text-28rpx text-[#333]">
                  {{ item.startUser?.nickname || '-' }}
                </view>
                <view class="text-24rpx text-[#999]">
                  {{ item.startUser?.deptName || '-' }}
                </view>
              </view>
            </view>
            <view class="mb-12rpx rounded-8rpx bg-[#f7f8f9] p-16rpx">
              <view class="mb-8rpx flex items-center justify-between text-26rpx">
                <text class="text-[#999]">发起时间</text>
                <text class="text-[#333]">{{ formatDateTime(item.startTime) }}</text>
              </view>
              <view v-if="item.endTime" class="flex items-center justify-between text-26rpx">
                <text class="text-[#999]">结束时间</text>
                <text class="text-[#333]">{{ formatDateTime(item.endTime) }}</text>
              </view>
            </view>
            <view v-if="item.tasks && item.tasks.length > 0" class="mb-12rpx">
              <view class="mb-8rpx text-26rpx text-[#999]">
                当前审批任务
              </view>
              <view class="flex flex-wrap gap-8rpx">
                <wd-tag
                  v-for="task in item.tasks"
                  :key="task.id"
                  type="primary"
                  variant="plain"
                  @click.stop="handleTaskDetail(item, task)"
                >
                  {{ task.name }}
                </wd-tag>
              </view>
            </view>
            <view
              v-if="item.status === BpmProcessInstanceStatus.RUNNING"
              class="flex items-center justify-end border-t border-[#f0f0f0] -mt-8"
            >
              <wd-button
                v-if="hasAccessByCodes(['bpm:process-instance:cancel-by-admin'])"
                size="small" type="danger" variant="plain"
                @click.stop="handleCancel(item)"
              >
                取消流程
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ProcessInstance } from '@/api/bpm/processInstance'
import { onUnload } from '@dcloudio/uni-app'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, ref } from 'vue'
import {
  cancelProcessInstanceByAdmin,
  getProcessInstanceManagerPage,
} from '@/api/bpm/processInstance'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { BpmProcessInstanceStatus, DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const list = ref<ProcessInstance[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询流程实例列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getProcessInstanceManagerPage(params)
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
function handleDetail(item: ProcessInstance) {
  uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${item.id}` })
}

/** 查看任务详情 */
function handleTaskDetail(row: ProcessInstance, task: { id: string, name: string }) {
  uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${row.id}&taskId=${task.id}` })
}

/** 取消流程实例 */
function handleCancel(item: ProcessInstance) {
  uni.showModal({
    title: '取消流程',
    editable: true,
    placeholderText: '请输入取消原因',
    success: async (res) => {
      if (!res.confirm) {
        return
      }
      const reason = res.content?.trim()
      if (!reason) {
        toast.error('请输入取消原因')
        return
      }
      await cancelProcessInstanceByAdmin(item.id, reason)
      toast.success('取消成功')
      // 刷新流程列表，并通知任务列表（待办/已办可能随之变化）
      reload()
      uni.$emit('bpm:task:reload')
    },
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('bpm:processInstance:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('bpm:processInstance:reload', reload)
})
</script>
