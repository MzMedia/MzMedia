<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="示例联系人"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

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
      empty-view-text="暂无示例联系人数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx flex items-center rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <wd-img
            v-if="item.avatar"
            :src="item.avatar"
            width="88rpx" height="88rpx"
            round
            custom-class="shrink-0"
          />
          <view class="ml-20rpx min-w-0 flex-1">
            <view class="mb-8rpx flex items-center justify-between">
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
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:demo01-contact:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Demo01Contact } from '@/api/infra/demo/demo01'
import { onMounted, onUnmounted, ref } from 'vue'
import { getDemo01ContactPage } from '@/api/infra/demo/demo01'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate, formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<Demo01Contact[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getDemo01ContactPage(params)
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
    url: '/pages-infra/demo/demo01/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: Demo01Contact) {
  uni.navigateTo({
    url: `/pages-infra/demo/demo01/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('infra:demo01-contact:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:demo01-contact:reload', reload)
})
</script>
