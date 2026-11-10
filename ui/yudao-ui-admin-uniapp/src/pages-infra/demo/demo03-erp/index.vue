<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="学生（主子表 ERP）"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无学生数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-12rpx flex items-center justify-between">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name }}
            </view>
            <dict-tag class="ml-16rpx shrink-0" :type="DICT_TYPE.SYSTEM_USER_SEX" :value="item.sex" />
          </view>
          <view class="mb-6rpx truncate text-26rpx text-[#999]">
            {{ item.description || '-' }}
          </view>
          <view class="flex items-center justify-between text-24rpx text-[#999]">
            <text>出生：{{ item.birthday ? formatDate(item.birthday) : '-' }}</text>
            <text>{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:demo03-student:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Demo03Student } from '@/api/infra/demo/demo03/erp'
import { onMounted, onUnmounted, ref } from 'vue'
import { getDemo03StudentPage } from '@/api/infra/demo/demo03/erp'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate, formatDateTime } from '@/utils/date'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<Demo03Student[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getDemo03StudentPage({ pageNo, pageSize })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-infra/demo/demo03-erp/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: Demo03Student) {
  uni.navigateTo({
    url: `/pages-infra/demo/demo03-erp/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('infra:demo03-erp:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:demo03-erp:reload', reload)
})
</script>
