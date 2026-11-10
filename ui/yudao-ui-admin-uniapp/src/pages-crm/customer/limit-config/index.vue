<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="客户限制配置"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 配置类型 -->
    <view class="bg-white">
      <wd-tabs v-model="tabIndex" shrink @change="handleTabChange">
        <wd-tab title="拥有客户数限制" />
        <wd-tab title="锁定客户数限制" />
      </wd-tabs>
    </view>

    <!-- 配置列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无客户限制配置"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="text-32rpx text-[#333] font-semibold">
              {{ formatConfigTitle(item) }}
            </view>
            <wd-tag type="primary" variant="plain">
              上限 {{ item.maxCount || 0 }}
            </wd-tag>
          </view>
          <view class="mb-12rpx text-28rpx text-[#666]">
            <text class="text-[#999]">适用人群：</text>{{ formatUserNames(item) }}
          </view>
          <view class="mb-12rpx text-28rpx text-[#666]">
            <text class="text-[#999]">适用部门：</text>{{ formatDeptNames(item) }}
          </view>
          <view
            v-if="item.type === LimitConfType.CUSTOMER_QUANTITY_LIMIT"
            class="mb-16rpx text-28rpx text-[#666]"
          >
            <text class="text-[#999]">成交占用：</text>{{ item.dealCountEnabled ? '是' : '否' }}
          </view>
          <view class="flex gap-16rpx">
            <wd-button v-if="hasAccessByCodes(['crm:customer-limit-config:update'])" size="small" type="primary" variant="plain" @click="handleEdit(item)">
              编辑
            </wd-button>
            <wd-button v-if="hasAccessByCodes(['crm:customer-limit-config:delete'])" size="small" type="danger" variant="plain" :loading="deletingId === item.id" @click="handleDelete(item)">
              删除
            </wd-button>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['crm:customer-limit-config:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { CustomerLimitConfig } from '@/api/crm/customer/limitConfig'
import { onUnload } from '@dcloudio/uni-app'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, nextTick, onMounted, ref } from 'vue'
import {
  deleteCustomerLimitConfig,
  getCustomerLimitConfigPage,
  LimitConfType,
} from '@/api/crm/customer/limitConfig'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const dialog = useDialog()
const toast = useToast()
const { hasAccessByCodes } = useAccess()
const tabIndex = ref(0) // 当前配置类型
const list = ref<CustomerLimitConfig[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const deletingId = ref<number>() // 删除状态
const activeType = computed(() => {
  return tabIndex.value === 0 ? LimitConfType.CUSTOMER_QUANTITY_LIMIT : LimitConfType.CUSTOMER_LOCK_LIMIT
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询配置列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getCustomerLimitConfigPage({
      pageNo,
      pageSize,
      type: activeType.value,
    })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 切换配置类型 */
function handleTabChange() {
  nextTick(() => {
    reload()
  })
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增配置 */
function handleAdd() {
  uni.navigateTo({ url: `/pages-crm/customer/limit-config/form/index?type=${activeType.value}` })
}

/** 编辑配置 */
function handleEdit(item: CustomerLimitConfig) {
  if (!item.id) {
    return
  }
  uni.navigateTo({ url: `/pages-crm/customer/limit-config/form/index?id=${item.id}` })
}

/** 删除配置 */
async function handleDelete(item: CustomerLimitConfig) {
  if (!item.id) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定要删除该客户限制配置吗？',
    })
  } catch {
    return
  }
  deletingId.value = item.id
  try {
    await deleteCustomerLimitConfig(item.id)
    toast.success('删除成功')
    reload()
  } finally {
    deletingId.value = undefined
  }
}

/** 格式化配置标题 */
function formatConfigTitle(item: CustomerLimitConfig) {
  return item.type === LimitConfType.CUSTOMER_QUANTITY_LIMIT ? '拥有客户数限制' : '锁定客户数限制'
}

/** 格式化适用人群（昵称，回退「全部」） */
function formatUserNames(item: CustomerLimitConfig) {
  const names = (item.users || []).map(user => user.nickname).filter(Boolean)
  return names.length > 0 ? names.join('、') : '全部'
}

/** 格式化适用部门（名称，回退「全部」） */
function formatDeptNames(item: CustomerLimitConfig) {
  const names = (item.depts || []).map(dept => dept.name).filter(Boolean)
  return names.length > 0 ? names.join('、') : '全部'
}

/** 初始化 */
onMounted(() => {
  uni.$on('crm:customer-limit-config:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('crm:customer-limit-config:reload', reload)
})
</script>
