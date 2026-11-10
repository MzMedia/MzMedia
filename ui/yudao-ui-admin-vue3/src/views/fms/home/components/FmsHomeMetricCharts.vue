<template>
  <div v-loading="loading" class="grid grid-cols-1 gap-16px xl:grid-cols-5">
    <div class="xl:col-span-3">
      <div class="mb-16px text-16px font-600 text-[var(--el-text-color-primary)]">
        {{ metricDetail ? `${metricDetail.name}变化趋势（单位：元）` : '财务指标趋势（单位：元）' }}
      </div>
      <Echart :height="360" :options="trendChartOptions" :not-merge="true" />
    </div>
    <div class="xl:col-span-2">
      <div class="mb-16px text-16px font-600 text-[var(--el-text-color-primary)]">
        {{
          metricDetail
            ? `${formatCurrentMonth()} ${metricDetail.name}结构分析（单位：元）`
            : '本期指标结构（单位：元）'
        }}
      </div>
      <Echart
        v-if="structureChartData.length > 0"
        :height="360"
        :options="structureChartOptions"
        :not-merge="true"
      />
      <el-empty v-else class="h-360px justify-center" description="暂无科目构成数据" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { EChartsOption } from 'echarts'
import type { FmsHomeMetricDetailVO, FmsHomeVO } from '@/api/fms/home'
import { formatAmount } from '@/views/fms/utils/format'
import { FMS_HOME_METRIC_COLORS } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsHomeMetricCharts' })

const props = defineProps<{
  home?: FmsHomeVO
  metricDetail?: FmsHomeMetricDetailVO
  selectedMetricKey?: string
  loading: boolean
}>()

const trendChartOptions = computed<EChartsOption>(() => {
  const commonOptions: EChartsOption = {
    color: FMS_HOME_METRIC_COLORS,
    grid: { left: 16, right: 24, top: 48, bottom: 12, containLabel: true },
    legend: { top: 0 },
    tooltip: { trigger: 'axis', valueFormatter: (value) => formatAmount(Number(value)) },
    yAxis: {
      type: 'value',
      axisLabel: { formatter: (value: number) => formatCompactAmount(value) }
    }
  }
  if (props.metricDetail) {
    return {
      ...commonOptions,
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: props.metricDetail.trends.map((item) => item.month)
      },
      series: [
        {
          name: props.metricDetail.name,
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.12 },
          data: props.metricDetail.trends.map((item) => item.amount)
        }
      ]
    }
  }
  return {
    ...commonOptions,
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.home?.trends.map((item) => item.month) || []
    },
    series: (props.home?.metrics || []).map((metric) => ({
      name: metric.name,
      type: 'line',
      smooth: true,
      data: (props.home?.trends || []).map(
        (trend) => trend.metrics.find((item) => item.key === metric.key)?.amount || 0
      )
    }))
  }
}) // 趋势图配置
const structureChartData = computed(() => buildStructureChartData()) // 科目构成图数据
const structureChartOptions = computed<EChartsOption>(() => ({
  color: FMS_HOME_METRIC_COLORS,
  tooltip: { trigger: 'item', valueFormatter: (value) => formatAmount(Number(value)) },
  legend: { bottom: 0 },
  series: [
    {
      name: '本期指标',
      type: 'pie',
      radius: ['46%', '72%'],
      center: ['50%', '44%'],
      label: { formatter: '{b}\n{d}%' },
      data: structureChartData.value
    }
  ]
})) // 科目构成图配置

/** 构建科目构成图数据 */
function buildStructureChartData() {
  if (!props.metricDetail) {
    return (props.home?.metrics || [])
      .filter((metric) => Number(metric.amount) > 0)
      .map((metric) => ({
        name: metric.name,
        value: Number(metric.amount)
      }))
  }
  const structure = props.metricDetail.structure
    .filter((item) => Number(item.amount) > 0)
    .map((item) => ({
      name: `${item.subjectCode} ${item.subjectName}`,
      value: Number(item.amount)
    }))
  if (structure.length === 0) {
    const metric = props.home?.metrics.find((item) => item.key === props.selectedMetricKey)
    return metric && Number(metric.amount) > 0
      ? [{ name: metric.name, value: Number(metric.amount) }]
      : []
  }
  const result = structure.slice(0, 5)
  if (structure.length > 5) {
    result.push({
      name: '其他',
      value: structure.slice(5).reduce((total, item) => total + item.value, 0)
    })
  }
  return result
}

/** 格式化坐标轴金额 */
function formatCompactAmount(amount: number) {
  const value = Number(amount || 0)
  if (Math.abs(value) >= 10000) return `${(value / 10000).toFixed(1)}万`
  return value.toFixed(0)
}

/** 格式化当前会计期间的月份文案 */
function formatCurrentMonth() {
  const month = Number(props.home?.currentMonth?.slice(5, 7))
  return month ? `${month}月` : ''
}
</script>
