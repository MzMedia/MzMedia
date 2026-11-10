<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="CRM 待办"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 待办分类 -->
    <view class="bg-white">
      <wd-tabs v-model="tabIndex">
        <wd-tab v-for="item in tabs" :key="item.key" :title="getTabTitle(item)" />
      </wd-tabs>
    </view>

    <!-- 当前待办列表 -->
    <CustomerTodayContactList v-if="currentTabKey === 'customerTodayContact'" class="min-h-0 flex-1" />
    <ClueFollowList v-else-if="currentTabKey === 'clueFollow'" class="min-h-0 flex-1" />
    <CustomerFollowList v-else-if="currentTabKey === 'customerFollow'" class="min-h-0 flex-1" />
    <CustomerPutPoolRemindList v-else-if="currentTabKey === 'customerPutPoolRemind'" class="min-h-0 flex-1" />
    <ContractAuditList v-else-if="currentTabKey === 'contractAudit'" class="min-h-0 flex-1" />
    <ReceivableAuditList v-else-if="currentTabKey === 'receivableAudit'" class="min-h-0 flex-1" />
    <ReceivablePlanRemindList v-else-if="currentTabKey === 'receivablePlanRemind'" class="min-h-0 flex-1" />
    <ContractRemindList v-else-if="currentTabKey === 'contractRemind'" class="min-h-0 flex-1" />
  </view>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { getFollowClueCount } from '@/api/crm/clue'
import { getAuditContractCount, getRemindContractCount } from '@/api/crm/contract'
import { getFollowCustomerCount, getPutPoolRemindCustomerCount, getTodayContactCustomerCount } from '@/api/crm/customer'
import { getAuditReceivableCount } from '@/api/crm/receivable'
import { getReceivablePlanRemindCount } from '@/api/crm/receivable/plan'
import { navigateBackPlus } from '@/utils'
import ClueFollowList from './components/clue-follow-list.vue'
import ContractAuditList from './components/contract-audit-list.vue'
import ContractRemindList from './components/contract-remind-list.vue'
import CustomerFollowList from './components/customer-follow-list.vue'
import CustomerPutPoolRemindList from './components/customer-put-pool-remind-list.vue'
import CustomerTodayContactList from './components/customer-today-contact-list.vue'
import ReceivableAuditList from './components/receivable-audit-list.vue'
import ReceivablePlanRemindList from './components/receivable-plan-remind-list.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const tabs = [
  { key: 'customerTodayContact', title: '今日需联系客户', getCount: getTodayContactCustomerCount },
  { key: 'clueFollow', title: '分配给我的线索', getCount: getFollowClueCount },
  { key: 'customerFollow', title: '分配给我的客户', getCount: getFollowCustomerCount },
  { key: 'customerPutPoolRemind', title: '待进入公海的客户', getCount: getPutPoolRemindCustomerCount },
  { key: 'contractAudit', title: '待审核合同', getCount: getAuditContractCount },
  { key: 'receivableAudit', title: '待审核回款', getCount: getAuditReceivableCount },
  { key: 'receivablePlanRemind', title: '待回款提醒', getCount: getReceivablePlanRemindCount },
  { key: 'contractRemind', title: '即将到期的合同', getCount: getRemindContractCount },
] // 待办分类
const tabIndex = ref(0) // 当前待办分类
const counts = ref<Record<string, number>>({}) // 待办数量
const currentTabKey = computed(() => tabs[tabIndex.value]?.key) // 当前待办分类标识

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 获取标签标题（带数量） */
function getTabTitle(item: { key: string, title: string }) {
  const count = counts.value[item.key] || 0
  return count > 0 ? `${item.title}(${count})` : item.title
}

/** 加载各待办数量 */
async function loadCounts() {
  // 用 allSettled 并发拉取，单个分类的数量接口失败（如无权限/超时）不影响其它分类
  const result = await Promise.allSettled(
    tabs.map(async item => [item.key, await item.getCount()] as const),
  )
  const next: Record<string, number> = {}
  result.forEach((item) => {
    // allSettled 的每项为 { status, value | reason }，仅成功项（fulfilled）才有 value：[key, 数量]
    if (item.status === 'fulfilled') {
      next[item.value[0]] = item.value[1]
    }
  })
  counts.value = next
}

/** 初始化 */
onMounted(() => {
  loadCounts()
})
</script>
