<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="商城首页"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <scroll-view scroll-y class="min-h-0 flex-1">
      <view class="p-24rpx">
        <!-- 核心数据：今日销售额 / 访问量 / 订单量 / 新增用户（今日 vs 昨日） -->
        <SummaryGrid class="mb-24rpx" :items="comparisonItems" reference-label="较昨日" />

        <!-- 运营数据：待办与商品概况，点击下钻至对应列表 -->
        <view class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm">
          <text class="mb-20rpx block text-30rpx text-[#333] font-semibold">运营数据</text>
          <view class="grid grid-cols-2 gap-20rpx">
            <view
              v-for="item in operationCards"
              :key="item.key"
              class="rounded-8rpx bg-[#f7f8fa] px-20rpx py-18rpx"
              @click="handleOpen(item.route)"
            >
              <view class="text-24rpx text-[#999]">
                {{ item.label }}
              </view>
              <view class="mt-8rpx text-34rpx text-[#333] font-semibold">
                {{ item.value }}
              </view>
            </view>
          </view>
        </view>

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
          <wd-tab title="会员概览" />
          <wd-tab title="会员终端" />
          <wd-tab title="交易量趋势" />
          <wd-tab title="用户统计" />
        </wd-tabs>

        <!-- 会员概览 -->
        <view v-if="activeTab === 0" class="mt-24rpx rounded-12rpx bg-white p-24rpx shadow-sm">
          <view class="mb-20rpx flex items-center justify-between">
            <text class="text-30rpx text-[#333] font-semibold">会员概览</text>
            <text class="text-26rpx text-[#999]">客单价 ￥{{ atvText }}</text>
          </view>
          <view class="mb-20rpx flex gap-16rpx">
            <view
              v-for="item in overviewItems"
              :key="item.label"
              class="flex-1 rounded-12rpx bg-[#f7f8fa] px-16rpx py-20rpx"
            >
              <text class="block text-24rpx text-[#999]">{{ item.label }}</text>
              <text class="mt-8rpx block text-36rpx text-[#333] font-semibold">{{ item.value }}</text>
              <text class="mt-4rpx block text-22rpx" :class="item.rate >= 0 ? 'text-[#f5222d]' : 'text-[#52c41a]'">
                环比 {{ item.rate >= 0 ? '+' : '' }}{{ item.rate }}%
              </text>
            </view>
          </view>
          <YdChart v-if="funnelOption" :option="funnelOption" height="480rpx" />
          <wd-empty v-else icon="content" tip="暂无统计数据" />
        </view>

        <!-- 会员终端 -->
        <view v-if="activeTab === 1" class="pt-24rpx">
          <StatisticsCard :section="terminalSection" :rows="terminalRows" />
        </view>

        <!-- 交易量趋势 -->
        <view v-if="activeTab === 2" class="pt-24rpx">
          <StatisticsCard :section="trendSection" :rows="trendRows" />
        </view>

        <!-- 用户统计 -->
        <view v-if="activeTab === 3" class="pt-24rpx">
          <StatisticsCard :section="registerSection" :rows="registerRows" />
        </view>

        <view class="h-40rpx" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { SummaryItem } from '@/pages-statistics/components/card/summary-grid.vue'
import type { StatisticsSection } from '@/pages-statistics/utils/statistics'
import { computed, onMounted, reactive, ref } from 'vue'
import { getProductSpuTabsCount } from '@/api/mall/product/spu'
import {
  getMemberAnalyse,
  getMemberRegisterCountList,
  getMemberTerminalStatisticsList,
  getMemberUserCountComparison,
  getTradeOrderComparison,
  getTradeOrderCount,
  getTradeStatisticsList,
  getWalletRechargePrice,
} from '@/api/mall/statistics'
import { getDictLabel } from '@/hooks/useDict'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateRange } from '@/utils/date'
import { fenToYuan, formatDisplayMoney } from '@/utils/format'
import { buildFunnelOption, calculateRelativeRate, normalizeRows } from '@/pages-statistics/utils/statistics'
import YdChart from '@/pages-statistics/components/yd-chart/yd-chart.vue'
import StatisticsCard from '@/pages-statistics/components/card/statistics-card.vue'
import SummaryGrid from '@/pages-statistics/components/card/summary-grid.vue'
import SearchForm from '../components/search-form.vue'

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
const activeTab = ref(0) // 当前分组：0 会员概览 / 1 会员终端 / 2 交易趋势 / 3 用户统计
const orderComparison = ref<Record<string, any>>({ value: {}, reference: {} }) // 交易对照（今日 vs 昨日）
const userComparison = ref<Record<string, any>>({ value: {}, reference: {} }) // 会员对照（今日 vs 昨日）
const cache = reactive<Record<string, any>>({}) // 分组数据缓存：key=`分组@时间区间`

const times = computed(() => formatDateRange([filters.startTime, filters.endTime])) // 查询时间区间
function tabCacheKey(tab: number) {
  return `${tab}@${times.value}`
}
const analyse = computed<Record<string, any>>(() => cache[tabCacheKey(0)] ?? {}) // 会员漏斗分析
const terminalRows = computed<Record<string, any>[]>(() => cache[tabCacheKey(1)] ?? []) // 终端分布（含字典标签）
const trendRows = computed<Record<string, any>[]>(() => cache[tabCacheKey(2)] ?? []) // 交易趋势明细（已转元）
const registerRows = computed<Record<string, any>[]>(() => cache[tabCacheKey(3)] ?? []) // 会员注册趋势
const atvText = computed(() => fenToYuan(analyse.value.atv).toFixed(2)) // 客单价（分转元）
const overviewItems = computed(() => {
  const comparison = analyse.value.comparison || {}
  const value = comparison.value || {}
  const reference = comparison.reference || {}
  return [
    { label: '注册用户数', value: value.registerUserCount || 0, rate: calculateRelativeRate(value.registerUserCount, reference.registerUserCount) },
    { label: '活跃用户数', value: value.visitUserCount || 0, rate: calculateRelativeRate(value.visitUserCount, reference.visitUserCount) },
    { label: '充值用户数', value: value.rechargeUserCount || 0, rate: calculateRelativeRate(value.rechargeUserCount, reference.rechargeUserCount) },
  ]
}) // 会员概览：注册/活跃/充值用户数 + 环比

const comparisonItems = computed<SummaryItem[]>(() => [
  { label: '今日销售额', value: fenToYuan(orderComparison.value.value?.orderPayPrice), reference: fenToYuan(orderComparison.value.reference?.orderPayPrice), prefix: '￥' },
  { label: '用户访问量', value: Number(userComparison.value.value?.visitUserCount || 0), reference: Number(userComparison.value.reference?.visitUserCount || 0) },
  { label: '订单量', value: orderComparison.value.value?.orderPayCount || 0, reference: orderComparison.value.reference?.orderPayCount || 0 },
  { label: '新增用户', value: userComparison.value.value?.registerUserCount || 0, reference: userComparison.value.reference?.registerUserCount || 0 },
]) // 核心数据卡片：环比展示由 SummaryCard 自行计算

const operationCards = ref([
  { key: 'undelivered', label: '待发货订单', value: '-', route: '/pages-mall/trade/order/index' },
  { key: 'afterSaleApply', label: '退款中订单', value: '-', route: '/pages-mall/trade/after-sale/index' },
  { key: 'pickUp', label: '待核销订单', value: '-', route: '/pages-mall/trade/delivery/pick-up-order/index' },
  { key: 'productForSale', label: '上架商品', value: '-', route: '/pages-mall/product/spu/index' },
  { key: 'productWarehouse', label: '仓库商品', value: '-', route: '/pages-mall/product/spu/index' },
  { key: 'productAlertStock', label: '库存预警', value: '-', route: '/pages-mall/product/spu/index' },
  { key: 'withdrawAuditing', label: '提现待审核', value: '-', route: '/pages-mall/trade/brokerage/withdraw/index' },
  { key: 'rechargePrice', label: '账户充值', value: '-', route: '/pages-mall/trade/order/index' },
]) // 运营数据

const terminalSection: StatisticsSection = {
  title: '终端分布',
  columns: [
    { prop: 'terminalName', label: '终端' },
    { prop: 'userCount', label: '会员数' },
  ],
  chart: { type: 'pie', categoryProp: 'terminalName', valueProp: 'userCount' },
} // 会员终端分组配置

const trendSection: StatisticsSection = {
  title: '交易量趋势',
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

const registerSection: StatisticsSection = {
  title: '用户统计',
  columns: [
    { prop: 'date', label: '日期' },
    { prop: 'count', label: '注册量' },
  ],
  chart: {
    type: 'line',
    categoryProp: 'date',
    series: [{ name: '注册量', prop: 'count', type: 'line' }],
  },
} // 会员注册趋势分组配置

const funnelOption = computed(() => {
  const value = analyse.value
  if (!value || value.visitUserCount === undefined) {
    return undefined
  }
  // 访客→下单→成交
  return buildFunnelOption([
    { name: '访客', value: value.visitUserCount || 0 },
    { name: '下单', value: value.orderUserCount || 0 },
    { name: '成交', value: value.payUserCount || 0 },
  ], '会员漏斗')
}) // 会员漏斗图配置

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 打开下钻列表 */
function handleOpen(route: string) {
  uni.navigateTo({ url: route })
}

/** 更新运营数据 */
function updateOperationCard(key: string, value: string | number) {
  const item = operationCards.value.find(card => card.key === key)
  if (item) {
    item.value = String(value)
  }
}

/** 加载仪表盘顶部：核心数据 + 运营数据 */
async function loadBase() {
  const [order, user, orderCount, productCount, paySummary] = await Promise.allSettled([
    getTradeOrderComparison(),
    getMemberUserCountComparison(),
    getTradeOrderCount(),
    getProductSpuTabsCount(),
    getWalletRechargePrice(),
  ])
  if (order.status === 'fulfilled') {
    orderComparison.value = order.value || { value: {}, reference: {} }
  }
  if (user.status === 'fulfilled') {
    userComparison.value = user.value || { value: {}, reference: {} }
  }
  if (orderCount.status === 'fulfilled') {
    updateOperationCard('undelivered', orderCount.value.undelivered || 0)
    updateOperationCard('afterSaleApply', orderCount.value.afterSaleApply || 0)
    updateOperationCard('pickUp', orderCount.value.pickUp || 0)
    updateOperationCard('withdrawAuditing', orderCount.value.auditingWithdraw || 0)
  }
  if (productCount.status === 'fulfilled') {
    updateOperationCard('productForSale', productCount.value['0'] || 0)
    updateOperationCard('productWarehouse', productCount.value['1'] || 0)
    updateOperationCard('productAlertStock', productCount.value['3'] || 0)
  }
  if (paySummary.status === 'fulfilled') {
    updateOperationCard('rechargePrice', formatDisplayMoney(paySummary.value.rechargePrice || 0))
  }
}

/** 加载指定分组数据（按时间区间缓存，命中则跳过） */
async function loadTab(tab: number) {
  const key = tabCacheKey(tab)
  if (cache[key] !== undefined) {
    return
  }
  if (tab === 0) {
    cache[key] = await getMemberAnalyse({ times: times.value }).catch(() => ({})) || {}
    return
  }
  if (tab === 1) {
    const terminalData = await getMemberTerminalStatisticsList().catch(() => [])
    // 终端用字典标签
    cache[key] = normalizeRows(terminalData).map(item => ({
      ...item,
      terminalName: getDictLabel(DICT_TYPE.TERMINAL, item.terminal) || '未知',
    }))
    return
  }
  if (tab === 2) {
    const trendList = await getTradeStatisticsList({ times: times.value }).catch(() => [])
    // 明细金额由分转元，避免折线图金额 ×100
    cache[key] = normalizeRows(trendList).map(item => ({
      ...item,
      turnoverPrice: fenToYuan(item.turnoverPrice),
      orderPayPrice: fenToYuan(item.orderPayPrice),
      rechargePrice: fenToYuan(item.rechargePrice),
      expensePrice: fenToYuan(item.expensePrice),
    }))
    return
  }
  const registerList = await getMemberRegisterCountList({ times: times.value }).catch(() => [])
  cache[key] = normalizeRows(registerList)
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

/** 搜索 / 刷新：强制刷新当前分组并重载顶部 */
async function loadData() {
  delete cache[tabCacheKey(activeTab.value)]
  loading.value = true
  try {
    await Promise.all([loadBase(), loadTab(activeTab.value)])
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
