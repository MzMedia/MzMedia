<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <DoneSearchForm @search="handleSearch" @reset="handleReset" />

    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无已办任务"
      @query="queryList"
    >
      <view class="bpm-list">
        <!-- 已完成列表 -->
        <view
          v-for="item in list"
          :key="item.id"
          class="bpm-card"
          @click="handleDetail(item)"
        >
          <view class="bpm-card-content">
            <view class="bpm-card-header">
              <view class="bpm-card-title">
                {{ item.processInstance?.name }}
              </view>
              <dict-tag :type="DICT_TYPE.BPM_TASK_STATUS" :value="item.status" />
            </view>
            <view v-if="item.processInstance?.summary?.length" class="bpm-summary">
              <view v-for="(s, idx) in item.processInstance.summary" :key="idx" class="bpm-summary-item">
                <text class="text-[#999]">{{ s.key }}：</text>
                <text>{{ s.value }}</text>
              </view>
            </view>
            <view class="bpm-summary-item">
              <text class="text-[#999]">审批意见：</text>
              <text>{{ item.reason || '-' }}</text>
            </view>
            <view class="bpm-card-info">
              <view class="bpm-user">
                <view class="bpm-avatar">
                  {{ item.processInstance?.startUser?.nickname?.[0] || '?' }}
                </view>
                <text class="bpm-nickname">{{ item.processInstance?.startUser?.nickname }}</text>
              </view>
              <text class="bpm-time">{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
          <view class="bpm-actions">
            <view class="bpm-action-btn" @click.stop="handleWithdraw(item)">
              <text>撤回</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { Task } from '@/api/bpm/task'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, onUnmounted, ref } from 'vue'
import { getTaskDonePage, withdrawTask } from '@/api/bpm/task'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import DoneSearchForm from './done-search-form.vue'
import '../styles/index.scss'

const dialog = useDialog()
const toast = useToast()
const list = ref<Task[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询已办任务列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getTaskDonePage(params)
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 搜索按钮操作 */
function handleSearch(data?: Record<string, any>) {
  queryParams.value = { ...data }
  reload()
}

/** 重置按钮操作 */
function handleReset() {
  handleSearch()
}

/** 查看详情 */
function handleDetail(item: Task) {
  uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${item.processInstance.id}&taskId=${item.id}` })
}

/** 撤回任务 */
async function handleWithdraw(item: Task) {
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定要撤回该任务吗？',
    })
  } catch {
    return
  }
  await withdrawTask(item.id)
  toast.success('撤回成功')
  uni.$emit('bpm:task:reload')
}

/** 初始化 */
onMounted(() => {
  uni.$on('bpm:task:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('bpm:task:reload', reload)
})
</script>
