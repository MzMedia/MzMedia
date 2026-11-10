<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <MySearchForm @search="handleSearch" @reset="handleReset" />

    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无发起的流程"
      @query="queryList"
    >
      <view class="bpm-list">
        <!-- 我的列表 -->
        <view
          v-for="item in list"
          :key="item.id"
          class="bpm-card"
          @click="handleDetail(item)"
        >
          <view class="bpm-card-content">
            <view class="bpm-card-header">
              <view class="bpm-card-title">
                {{ item.name }}
              </view>
              <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="item.status" />
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
                  {{ userNickname?.[0] }}
                </view>
                <text class="bpm-nickname">{{ userNickname }}</text>
              </view>
              <text class="bpm-time">{{ formatDateTime(item.startTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮（工作台为 tabbar 页，底部留出 tabbar 高度避免遮挡） -->
    <wd-fab
      v-if="hasAccessByCodes(['bpm:process-instance:create'])"
      position="right-bottom"
      type="primary"
      :gap="{ bottom: 100 }"
      :expandable="false"
      @click="handleCreate"
    />
  </view>
</template>

<script lang="ts" setup>
import type { ProcessInstance } from '@/api/bpm/processInstance'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { getProcessInstanceMyPage } from '@/api/bpm/processInstance'
import { useAccess } from '@/hooks/useAccess'
import { useUserStore } from '@/store'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import MySearchForm from './my-search-form.vue'
import '../styles/index.scss'

const { hasAccessByCodes } = useAccess()
const userStore = useUserStore()
const userNickname = computed(() => userStore.userInfo?.nickname || '')

const list = ref<ProcessInstance[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询我的流程列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getProcessInstanceMyPage(params)
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
function handleDetail(item: ProcessInstance) {
  uni.navigateTo({ url: `/pages-bpm/processInstance/detail/index?id=${item.id}` })
}

/** 发起流程 */
function handleCreate() {
  uni.navigateTo({ url: '/pages-bpm/processInstance/create/index' })
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
