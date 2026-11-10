<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <TokenSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 令牌列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无令牌数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.accessToken"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
        >
          <view class="p-24rpx">
            <view class="mb-16rpx flex items-center justify-between">
              <view class="text-28rpx text-[#333] font-semibold">
                用户编号: {{ item.userId }}
              </view>
              <dict-tag :type="DICT_TYPE.USER_TYPE" :value="item.userType" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">访问令牌：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.accessToken }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">刷新令牌：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.refreshToken }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">客户端编号：</text>
              <text>{{ item.clientId }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">过期时间：</text>
              <text>{{ formatDateTime(item.expiresTime) }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
            <!-- 删除按钮 -->
            <view
              v-if="hasAccessByCodes(['system:oauth2-token:delete'])"
              class="flex justify-end -mt-8"
            >
              <wd-button size="small" type="danger" @click="handleDelete(item)">
                强退
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { OAuth2Token } from '@/api/system/oauth2/token'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import { deleteOAuth2Token, getOAuth2TokenPage } from '@/api/system/oauth2/token'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import TokenSearchForm from './token-search-form.vue'

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const list = ref<OAuth2Token[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询令牌列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getOAuth2TokenPage(params)
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

/** 强制退出用户 */
async function handleDelete(item: OAuth2Token) {
  try {
    await dialog.confirm({
      title: '提示',
      msg: '是否要强制退出用户？',
    })
  } catch {
    return
  }
  // 执行强退
  await deleteOAuth2Token(item.accessToken)
  toast.success('强退成功')
  // 刷新列表
  handleQuery()
}
</script>
