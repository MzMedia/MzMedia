<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <CopySearchForm @search="handleSearch" @reset="handleReset" />

    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无抄送任务"
      @query="queryList"
    >
      <view class="bpm-list">
        <!-- 抄送列表 -->
        <view
          v-for="item in list"
          :key="item.id"
          class="bpm-card"
          @click="handleDetail(item)"
        >
          <view class="bpm-card-content">
            <view class="bpm-card-header">
              <view class="bpm-card-title">
                {{ item.processInstanceName }}
              </view>
              <wd-tag type="primary" variant="plain">
                查看详情
              </wd-tag>
            </view>
            <view v-if="item.summary?.length" class="bpm-summary">
              <view v-for="(s, idx) in item.summary" :key="idx" class="bpm-summary-item">
                <text class="text-[#999]">{{ s.key }}：</text>
                <text>{{ s.value }}</text>
              </view>
            </view>
            <view class="bpm-card-info">
              <view class="bpm-user">
                <view class="bpm-avatar">
                  {{ item.startUser.nickname?.[0] || '?' }}
                </view>
                <text class="bpm-nickname">{{ item.startUser.nickname }}</text>
              </view>
              <text class="bpm-time">{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { ProcessInstanceCopy } from '@/api/bpm/processInstance'
import { onMounted, onUnmounted, ref } from 'vue'
import { getProcessInstanceCopyPage } from '@/api/bpm/processInstance'
import { formatDateTime } from '@/utils/date'
import CopySearchForm from './copy-search-form.vue'
import '../styles/index.scss'

const list = ref<ProcessInstanceCopy[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询抄送任务列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getProcessInstanceCopyPage(params)
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
function handleDetail(item: ProcessInstanceCopy) {
  uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${item.processInstanceId}` })
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
