<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号统计"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 统计分类 -->
    <view class="bg-white">
      <wd-tabs v-model="tabIndex" @change="handleTabChange">
        <wd-tab v-for="section in sections" :key="section.title" :title="section.tabTitle" />
      </wd-tabs>
    </view>

    <scroll-view scroll-y class="min-h-0 flex-1">
      <view class="p-24rpx">
        <!-- 查询条件 -->
        <view class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm">
          <AccountPicker v-model="accountId" @change="handleAccountChange" />
          <wd-cell
            title="开始日期"
            is-link
            :value="formatDate(dateRange[0])"
            @click="pickerVisible.start = true"
          />
          <wd-datetime-picker
            v-model="dateRange[0]"
            v-model:visible="pickerVisible.start"
            title="请选择开始日期"
            type="date"
            @confirm="handleQueryChange"
          />
          <wd-cell
            title="结束日期"
            is-link
            :value="formatDate(dateRange[1])"
            @click="pickerVisible.end = true"
          />
          <wd-datetime-picker
            v-model="dateRange[1]"
            v-model:visible="pickerVisible.end"
            title="请选择结束日期"
            type="date"
            @confirm="handleQueryChange"
          />
        </view>

        <!-- 统计数据 -->
        <view v-if="loading" class="py-60rpx text-center">
          <wd-loading />
        </view>
        <template v-else>
          <view
            v-if="activeSection"
            class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
          >
            <YdChart
              :option="activeSection.chartOption"
              :empty="activeSection.empty"
              :height="activeSection.chartHeight"
              class="mb-8rpx"
            />
          </view>
        </template>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { computed, reactive, ref } from 'vue'
import {
  getInterfaceSummary,
  getUpstreamMessage,
  getUserCumulate,
  getUserSummary,
} from '@/api/mp/statistics'
import { navigateBackPlus } from '@/utils'
import { formatDate, formatDateTime } from '@/utils/date'
import AccountPicker from '@/pages-mp/account/components/account-picker.vue'
import YdChart from '../components/yd-chart/yd-chart.vue'

const CHART_COLORS = ['#67C23A', '#E5323E', '#E6A23C', '#409EFF'] // 图表配色

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const accountId = ref<number>() // 当前公众号编号
const loading = ref(false) // 加载状态
const tabIndex = ref(0) // 当前统计分类下标
const loadedMap = ref({
  userSummary: false,
  userCumulate: false,
  upstreamMessage: false,
  interfaceSummary: false,
}) // 各分类加载状态
const pickerVisible = reactive({
  start: false,
  end: false,
}) // 日期选择器状态
const dateRange = ref<[number, number]>([
  dayjs().subtract(7, 'day').startOf('day').valueOf(),
  dayjs().subtract(1, 'day').endOf('day').valueOf(),
]) // 查询日期范围
const userSummaryList = ref<any[]>([]) // 用户增减数据
const userCumulateList = ref<any[]>([]) // 累计用户数据
const upstreamMessageList = ref<any[]>([]) // 消息概况数据
const interfaceSummaryList = ref<any[]>([]) // 接口分析数据
const dateLabels = computed(() => { // 横轴日期范围
  const start = dayjs(dateRange.value[0]).startOf('day')
  const end = dayjs(dateRange.value[1]).startOf('day')
  const days = Math.max(end.diff(start, 'day'), 0)
  return Array.from({ length: days + 1 }, (_, index) => start.add(index, 'day').format('YYYY-MM-DD'))
})
const axisDateLabels = computed(() => dateLabels.value.map(date => formatDate(date, 'MM-DD'))) // 横轴日期展示

/** 统一坐标轴样式（窄屏小字号 + 隐藏轴刻度） */
function axisLabelStyle() {
  return { color: '#999', fontSize: 10 }
}

/** 按日期取统计值 */
function getDateValue(list: any[], date: string, field: string) {
  const item = list.find(row => formatDate(row.refDate) === date)
  return Number(item?.[field] || 0)
}

const userSummaryOption = computed(() => { // 用户增减：新增用户 / 取消关注 双柱
  const list = userSummaryList.value
  const dates = dateLabels.value
  return {
    color: [CHART_COLORS[0], CHART_COLORS[1]],
    tooltip: { trigger: 'axis', confine: true },
    legend: { top: 0, data: ['新增用户', '取消关注'], textStyle: { color: '#666', fontSize: 11 } },
    grid: { left: 8, right: 12, top: 48, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      data: axisDateLabels.value,
      axisLabel: { ...axisLabelStyle(), rotate: 30 },
      axisTick: { show: false },
    },
    yAxis: { type: 'value', minInterval: 1, axisLabel: axisLabelStyle(), splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [
      { name: '新增用户', type: 'bar', barGap: 0, barMaxWidth: 24, data: dates.map(date => getDateValue(list, date, 'newUser')) },
      { name: '取消关注', type: 'bar', barMaxWidth: 24, data: dates.map(date => getDateValue(list, date, 'cancelUser')) },
    ],
  }
})

const userCumulateOption = computed(() => { // 累计用户：累计用户量 折线
  const list = userCumulateList.value
  const dates = dateLabels.value
  return {
    color: [CHART_COLORS[3]],
    tooltip: { trigger: 'axis', confine: true },
    legend: { top: 0, data: ['累计用户量'], textStyle: { color: '#666', fontSize: 11 } },
    grid: { left: 8, right: 12, top: 48, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      data: axisDateLabels.value,
      axisLabel: { ...axisLabelStyle(), rotate: 30 },
      axisTick: { show: false },
    },
    yAxis: { type: 'value', minInterval: 1, axisLabel: axisLabelStyle(), splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [
      { name: '累计用户量', type: 'line', smooth: true, showSymbol: false, areaStyle: { opacity: 0.12 }, data: dates.map(date => getDateValue(list, date, 'cumulateUser')) },
    ],
  }
})

const upstreamMessageOption = computed(() => { // 消息概况：发送人数 / 发送条数 双折线
  const list = upstreamMessageList.value
  const dates = dateLabels.value
  return {
    color: [CHART_COLORS[0], CHART_COLORS[1]],
    tooltip: { trigger: 'axis', confine: true },
    legend: { top: 0, data: ['发送人数', '发送条数'], textStyle: { color: '#666', fontSize: 11 } },
    grid: { left: 8, right: 12, top: 48, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      data: axisDateLabels.value,
      axisLabel: { ...axisLabelStyle(), rotate: 30 },
      axisTick: { show: false },
    },
    yAxis: { type: 'value', minInterval: 1, axisLabel: axisLabelStyle(), splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [
      { name: '发送人数', type: 'line', smooth: true, showSymbol: false, data: dates.map(date => getDateValue(list, date, 'messageUser')) },
      { name: '发送条数', type: 'line', smooth: true, showSymbol: false, data: dates.map(date => getDateValue(list, date, 'messageCount')) },
    ],
  }
})

const interfaceSummaryOption = computed(() => { // 接口分析：回复 / 失败 / 最大耗时 / 总耗时 四柱
  const list = interfaceSummaryList.value
  const dates = dateLabels.value
  return {
    color: CHART_COLORS,
    tooltip: { trigger: 'axis', confine: true },
    legend: {
      top: 0,
      type: 'scroll',
      data: ['回复次数', '失败次数', '最大耗时', '总耗时'],
      textStyle: { color: '#666', fontSize: 10 },
      itemWidth: 14,
      itemGap: 8,
    },
    grid: { left: 8, right: 12, top: 52, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      data: axisDateLabels.value,
      axisLabel: { ...axisLabelStyle(), rotate: 40, interval: 0 },
      axisTick: { show: false },
    },
    yAxis: { type: 'value', axisLabel: axisLabelStyle(), splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [
      { name: '回复次数', type: 'bar', barGap: 0, barMaxWidth: 14, data: dates.map(date => getDateValue(list, date, 'callbackCount')) },
      { name: '失败次数', type: 'bar', barMaxWidth: 14, data: dates.map(date => getDateValue(list, date, 'failCount')) },
      { name: '最大耗时', type: 'bar', barMaxWidth: 14, data: dates.map(date => getDateValue(list, date, 'maxTimeCost')) },
      { name: '总耗时', type: 'bar', barMaxWidth: 14, data: dates.map(date => getDateValue(list, date, 'totalTimeCost')) },
    ],
  }
})

const sections = computed(() => [
  {
    title: '用户增减数据',
    tabTitle: '用户增减',
    chartOption: userSummaryOption.value,
    chartHeight: '520rpx',
    empty: loadedMap.value.userSummary && userSummaryList.value.length === 0,
    loaded: loadedMap.value.userSummary,
    load: getUserSummary,
    setData: (data: any[]) => userSummaryList.value = data,
    setLoaded: () => loadedMap.value.userSummary = true,
  },
  {
    title: '累计用户数据',
    tabTitle: '累计用户',
    chartOption: userCumulateOption.value,
    chartHeight: '520rpx',
    empty: loadedMap.value.userCumulate && userCumulateList.value.length === 0,
    loaded: loadedMap.value.userCumulate,
    load: getUserCumulate,
    setData: (data: any[]) => userCumulateList.value = data,
    setLoaded: () => loadedMap.value.userCumulate = true,
  },
  {
    title: '消息概况数据',
    tabTitle: '消息概况',
    chartOption: upstreamMessageOption.value,
    chartHeight: '520rpx',
    empty: loadedMap.value.upstreamMessage && upstreamMessageList.value.length === 0,
    loaded: loadedMap.value.upstreamMessage,
    load: getUpstreamMessage,
    setData: (data: any[]) => upstreamMessageList.value = data,
    setLoaded: () => loadedMap.value.upstreamMessage = true,
  },
  {
    title: '接口分析数据',
    tabTitle: '接口分析',
    chartOption: interfaceSummaryOption.value,
    chartHeight: '520rpx',
    empty: loadedMap.value.interfaceSummary && interfaceSummaryList.value.length === 0,
    loaded: loadedMap.value.interfaceSummary,
    load: getInterfaceSummary,
    setData: (data: any[]) => interfaceSummaryList.value = data,
    setLoaded: () => loadedMap.value.interfaceSummary = true,
  },
])
const activeSection = computed(() => sections.value[tabIndex.value] || sections.value[0]) // 当前统计分类

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  handleQueryChange()
}

/** 校验日期范围 */
function validateDateRange() {
  const start = dayjs(dateRange.value[0])
  const end = dayjs(dateRange.value[1])
  if (end.diff(start, 'day') >= 7) {
    uni.showToast({ icon: 'none', title: '时间间隔需在 7 天以内' })
    return false
  }
  return true
}

/** 生成查询参数 */
function getSummaryParams() {
  if (!accountId.value) {
    return undefined
  }
  if (!validateDateRange()) {
    return undefined
  }
  return {
    accountId: accountId.value,
    date: [formatDateTime(dateRange.value[0]), formatDateTime(dateRange.value[1])],
  }
}

/** 重置统计缓存 */
function resetSummaryCache() {
  loadedMap.value = {
    userSummary: false,
    userCumulate: false,
    upstreamMessage: false,
    interfaceSummary: false,
  }
  userSummaryList.value = []
  userCumulateList.value = []
  upstreamMessageList.value = []
  interfaceSummaryList.value = []
}

/** 查询当前分类统计数据 */
async function getSummary(force = false) {
  const section = activeSection.value
  if (!force && section.loaded) {
    return
  }
  const params = getSummaryParams()
  if (!params) {
    return
  }
  loading.value = true
  try {
    section.setData(await section.load(params))
    section.setLoaded()
  } catch {
    // 请求层已提示错误，保留未加载状态，切回后可重试
    section.setData([])
  } finally {
    loading.value = false
  }
}

/** 查询条件变化 */
function handleQueryChange() {
  resetSummaryCache()
  getSummary(true)
}

/** 分类切换 */
function handleTabChange({ index }: { index: number }) {
  tabIndex.value = index
  getSummary()
}
</script>
