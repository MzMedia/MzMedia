<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号账号"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 账号列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号账号数据"
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
            <view class="mb-16rpx flex items-center justify-between gap-16rpx">
              <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
                {{ item.name }}
              </view>
              <wd-tag v-if="item.qrCodeUrl" type="success" plain>
                已生成二维码
              </wd-tag>
            </view>
            <view class="mb-12rpx flex text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">微信号：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.account || '-' }}</text>
            </view>
            <view class="mb-12rpx flex text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">AppID：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.appId || '-' }}</text>
            </view>
            <view class="break-all text-24rpx text-[#999]">
              服务器地址：http://服务端地址/admin-api/mp/open/{{ item.appId || '-' }}
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['mp:account:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Account } from '@/api/mp/account'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getAccountPage } from '@/api/mp/account'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<Account[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询公众号账号列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getAccountPage({
      ...queryParams.value,
      pageNo,
      pageSize,
    })
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

/** 新增账号 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mp/account/form/index' })
}

/** 查看详情 */
function handleDetail(item: Account) {
  uni.navigateTo({ url: `/pages-mp/account/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mp:account:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mp:account:reload', reload)
})
</script>
