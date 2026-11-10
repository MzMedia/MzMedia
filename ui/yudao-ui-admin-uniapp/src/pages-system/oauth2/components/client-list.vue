<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <ClientSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 应用列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无应用数据"
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
              <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
                {{ item.name }}
              </view>
              <dict-tag class="ml-16rpx shrink-0" :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">客户端编号：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.clientId }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">授权类型：</text>
              <dict-tag :type="DICT_TYPE.SYSTEM_OAUTH2_GRANT_TYPE" :value="item.authorizedGrantTypes" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">访问令牌有效期：</text>
              <text>{{ item.accessTokenValiditySeconds }} 秒</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">刷新令牌有效期：</text>
              <text>{{ item.refreshTokenValiditySeconds }} 秒</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['system:oauth2-client:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { OAuth2Client } from '@/api/system/oauth2/client'
import { onMounted, onUnmounted, ref } from 'vue'
import { getOAuth2ClientPage } from '@/api/system/oauth2/client'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import ClientSearchForm from './client-search-form.vue'

const { hasAccessByCodes } = useAccess()
const list = ref<OAuth2Client[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询应用列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getOAuth2ClientPage(params)
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

/** 新增应用 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-system/oauth2/client/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: OAuth2Client) {
  uni.navigateTo({
    url: `/pages-system/oauth2/client/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('system:oauth2-client:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('system:oauth2-client:reload', reload)
})
</script>
