<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="交易统计"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <scroll-view scroll-y class="min-h-0 flex-1">
      <view class="p-24rpx">
        <!-- 交易概览 -->
        <SummaryGrid class="mb-24rpx" :items="summaryItems" reference-label="较前一周期" />

        <!-- 搜索组件 -->
        <SearchForm
          class="mb-24rpx"
          :initial-start-time="defaultStartTime"
          :initial-end-time="defaultEndTime"
          @search="handleQuery"
          @reset="handleReset"
        />

        <view class="mb-24rpx flex justify-end">
          <wd-button size="small" type="primary" variant="plain" :loading="loading" @click="loadData">
            刷新
          </wd-button>
        </view>

        <!-- 分组切换 -->
        <wd-tabs v-model="activeTab" @change="handleTabChange">
          <wd-tab title="交易状况" />
          <wd-tab title="交易趋势" />
        </wd-tabs>
        <!-- 交易状况 -->
        <view v-if="activeTab === 0" class="mt-24rpx rounded-12rpx bg-white p-24rpx shadow-sm">
          <text class="mb-20rpx block text-30rpx text-[#333] font-semibold">交易状况</text>
          <SummaryGrid :items="trendItems" reference-label="较前一周期" />
        </view>
        <!-- 交易趋势折线图 -->
        <view v-if="activeTab === 1" class="pt-24rpx">
          <StatisticsCard :section="trendSection" :rows="trendRows" />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { SummaryItem } from '@/pages-statistics/components/card/summary-grid.vue'
import type { StatisticsSection } from '@/pages-statistics/utils/statistics'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  getTradeStatisticsAnalyse,
  getTradeStatisticsList,
  getTradeStatisticsSummary,
} from '@/api/mall/statistics'
import { navigateBackPlus } from '@/utils'
import { formatDateRange } from '@/utils/date'
import SearchForm from '../components/search-form.vue'
import { fenToYuan } from '@/utils/format'
import { normalizeRows } from '@/pages-statistics/utils/statistics'
import StatisticsCard from '@/pages-statistics/components/card/statistics-card.vue'
import SummaryGrid from '@/pages-statistics/components/card/summary-grid.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const now = new Date()
const defaultStartTime = now.getTime() - 3600 * 1000 * 24 * 30 // 默认开始日期
const defaultEndTime = now.getTime() // 默认结束日期
const filters = reactive({
  startTime: defaultStartTime,
  endTime: defaultEndTime,
}) // 筛选条件
const loading = ref(false) // 统计加载状态
const activeTab = ref(0) // 当前分组：0 交易状况 / 1 交易趋势
const summary = ref<Record<string, any>>({ value: {}, reference: {} }) // 交易概览（昨日/本月）
const cache = reactive<Record<string, any>>({}) // 分组数据缓存：key=`分组@时间区间`

const times = computed(() => formatDateRange([filters.startTime, filters.endTime])) // 查询时间区间
function tabCacheKey(tab: number) {
  return `${tab}@${times.value}`
}
const trend = computed<Record<string, any>>(() => cache[tabCacheKey(0)] ?? { value: {}, reference: {} }) // 交易状况概览
const trendRows = computed<Record<string, any>[]>(() => cache[tabCacheKey(1)] ?? []) // 交易趋势明细（已转元）

const summaryItems = computed<SummaryItem[]>(() => [
  { label: '昨日订单数', value: summary.value.value?.yesterdayOrderCount || 0, reference: summary.value.reference?.yesterdayOrderCount },
  { label: '本月订单数', value: summary.value.value?.monthOrderCount || 0, reference: summary.value.reference?.monthOrderCount },
  { label: '昨日支付金额', value: fenToYuan(summary.value.value?.yesterdayPayPrice), reference: fenToYuan(summary.value.reference?.yesterdayPayPrice), prefix: '￥' },
  { label: '本月支付金额', value: fenToYuan(summary.value.value?.monthPayPrice), reference: fenToYuan(summary.value.reference?.monthPayPrice), prefix: '￥' },
]) // 交易概览卡片
const trendItems = computed<SummaryItem[]>(() => {
  const value = trend.value.value || {}
  const reference = trend.value.reference || {}
  // 营业额、商品支付、充值、支出、余额支付、支付佣金、商品退款
  const props = ['turnoverPrice', 'orderPayPrice', 'rechargePrice', 'expensePrice', 'walletPayPrice', 'brokerageSettlementPrice', 'afterSaleRefundPrice']
  const labels = ['营业额', '商品支付', '充值', '支出', '余额支付', '支付佣金', '商品退款']
  return props.map((prop, index) => ({
    label: labels[index],
    value: fenToYuan(value[prop]),
    reference: fenToYuan(reference[prop]),
    prefix: '￥',
  }))
}) // 交易状况卡片

const trendSection: StatisticsSection = {
  title: '交易趋势',
  columns: [
    { prop: 'time', label: '时间' },
    { prop: 'turnoverPrice', label: '营业额', type: 'money' },
    { prop: 'orderPayPrice', label: '商品支付', type: 'money' },
    { prop: 'rechargePrice', label: '充值', type: 'money' },
    { prop: 'expensePrice', label: '支出', type: 'money' },
  ],
  chart: {
    type: 'line',
    categoryProp: 'time',
    money: true,
    series: [
      { name: '营业额', prop: 'turnoverPrice' },
      { name: '商品支付', prop: 'orderPayPrice' },
      { name: '充值', prop: 'rechargePrice' },
      { name: '支出', prop: 'expensePrice' },
    ],
  },
} // 交易趋势分组配置

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 加载交易概览 */
async function loadSummary() {
  const data = await getTradeStatisticsSummary().catch(() => ({ value: {}, reference: {} }))
  summary.value = data || { value: {}, reference: {} }
}

/** 加载指定分组数据（按时间区间缓存，命中则跳过） */
async function loadTab(tab: number) {
  const key = tabCacheKey(tab)
  if (cache[key] !== undefined) {
    return
  }
  const params = { times: times.value }
  if (tab === 0) {
    cache[key] = await getTradeStatisticsAnalyse(params).catch(() => ({ value: {}, reference: {} })) || { value: {}, reference: {} }
    return
  }
  // 明细金额由分转元，避免折线图金额 ×100
  const listData = await getTradeStatisticsList(params).catch(() => [])
  cache[key] = normalizeRows(listData).map(item => ({
    ...item,
    turnoverPrice: fenToYuan(item.turnoverPrice),
    orderPayPrice: fenToYuan(item.orderPayPrice),
    rechargePrice: fenToYuan(item.rechargePrice),
    expensePrice: fenToYuan(item.expensePrice),
  }))
}

/** 切换分组 */
async function handleTabChange({ index }: { index: number }) {
  activeTab.value = index
  loading.value = true
  try {
    await loadTab(index)
  } finally {
    loading.value = false
  }
}

/** 搜索 / 刷新：强制刷新当前分组并重载概览 */
async function loadData() {
  delete cache[tabCacheKey(activeTab.value)]
  loading.value = true
  try {
    await Promise.all([loadSummary(), loadTab(activeTab.value)])
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
function handleQuery(data: { endTime: number, startTime: number }) {
  filters.startTime = data.startTime
  filters.endTime = data.endTime
  loadData()
}

/** 重置按钮操作 */
function handleReset() {
  filters.startTime = defaultStartTime
  filters.endTime = defaultEndTime
  loadData()
}

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>
