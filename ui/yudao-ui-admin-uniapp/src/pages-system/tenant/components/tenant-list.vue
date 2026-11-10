<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <TenantSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 租户列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无租户数据"
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
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">联系人：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.contactName || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">租户套餐：</text>
              <text>{{ getPackageName(item.packageId) }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">联系手机：</text>
              <text>{{ item.contactMobile || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">账号额度：</text>
              <text>{{ item.accountCount }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">过期时间：</text>
              <text>{{ formatDateTime(item.expireTime) }}</text>
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
      v-if="hasAccessByCodes(['system:tenant:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Tenant } from '@/api/system/tenant'
import type { TenantPackage } from '@/api/system/tenant/package'
import { onMounted, onUnmounted, ref } from 'vue'
import { getTenantPage } from '@/api/system/tenant'
import { getTenantPackageList } from '@/api/system/tenant/package'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import TenantSearchForm from './tenant-search-form.vue'

const { hasAccessByCodes } = useAccess()
const list = ref<Tenant[]>([]) // 列表数据
const packageList = ref<TenantPackage[]>([])
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 获取套餐名称 */
function getPackageName(packageId?: number) {
  if (packageId === 0) {
    return '系统租户'
  }
  const pkg = packageList.value.find(item => item.id === packageId)
  return pkg?.name || '-'
}

/** 加载租户套餐列表 */
async function loadPackageList() {
  packageList.value = await getTenantPackageList()
}

/** 查询租户列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getTenantPage(params)
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

/** 新增租户 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-system/tenant/tenant/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: Tenant) {
  uni.navigateTo({
    url: `/pages-system/tenant/tenant/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(async () => {
  await loadPackageList()
  uni.$on('system:tenant:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('system:tenant:reload', reload)
})
</script>
