<!-- 销售漏斗分析 -->
<template>
  <!-- Echarts图 -->
  <el-card shadow="never">
    <el-row>
      <el-col :span="24">
        <el-button-group class="mb-10px">
          <el-button :type="active ? 'primary' : 'default'" @click="handleActive(true)">
            阶段视角
          </el-button>
          <el-button :type="!active ? 'primary' : 'default'" @click="handleActive(false)">
            金额视角
          </el-button>
        </el-button-group>
        <el-skeleton :loading="loading" animated>
          <Echart :height="500" :options="echartsOption" />
        </el-skeleton>
      </el-col>
    </el-row>
  </el-card>

  <!-- 统计列表 -->
  <el-card class="mt-16px" shadow="never">
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="序号" type="index" width="80" />
      <el-table-column align="center" label="阶段" prop="statusName" min-width="160" />
      <el-table-column align="center" label="赢单率" prop="statusPercent" min-width="120">
        <template #default="{ row }">{{ row.statusPercent || 0 }}%</template>
      </el-table-column>
      <el-table-column align="center" label="商机数" min-width="200" prop="businessCount" />
      <el-table-column
        align="right"
        label="商机总金额(元)"
        min-width="200"
        prop="totalPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
    </el-table>
  </el-card>
</template>
<script lang="ts" setup>
import {
  CrmStatisticsBusinessSummaryByStatusRespVO,
  StatisticFunnelApi
} from '@/api/crm/statistics/funnel'
import { EChartsOption } from 'echarts'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'FunnelBusiness' })
const props = defineProps<{ queryParams: any }>() // 搜索参数

const active = ref(true) // 是否为阶段视角
const loading = ref(false) // 加载中
const list = ref<CrmStatisticsBusinessSummaryByStatusRespVO[]>([]) // 列表的数据

/** 销售漏斗 */
const echartsOption = reactive<EChartsOption>({
  title: {
    text: '销售漏斗'
  },
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      const data = params.data || {}
      return [
        data.statusName || params.name,
        `商机数：${data.businessCount || 0} 个`,
        `商机金额：${erpPriceInputFormatter(data.totalPrice || 0)} 元`,
        `赢单率：${data.statusPercent || 0}%`
      ].join('<br/>')
    }
  },
  toolbox: {
    feature: {
      dataView: { readOnly: false },
      restore: {},
      saveAsImage: {}
    }
  },
  legend: {
    data: []
  },
  series: [
    {
      name: '销售漏斗',
      type: 'funnel',
      left: '10%',
      top: 60,
      bottom: 60,
      width: '80%',
      min: 0,
      max: 100,
      minSize: '0%',
      maxSize: '100%',
      sort: 'none',
      gap: 2,
      label: {
        show: true,
        position: 'inside'
      },
      labelLine: {
        length: 10,
        lineStyle: {
          width: 1,
          type: 'solid'
        }
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        label: {
          fontSize: 20
        }
      },
      data: []
    }
  ]
}) as EChartsOption

const handleActive = async (val: boolean) => {
  active.value = val
  fillChart(list.value)
}

/** 填充漏斗图 */
const fillChart = (data: CrmStatisticsBusinessSummaryByStatusRespVO[]) => {
  if (!echartsOption.series || !echartsOption.series[0]) {
    return
  }
  const chartData = data.map((item) => ({
    value: active.value ? Number(item.businessCount || 0) : Number(item.totalPrice || 0),
    name: `${item.statusName}-${item.businessCount || 0}个`,
    statusName: item.statusName,
    statusPercent: item.statusPercent,
    businessCount: item.businessCount,
    totalPrice: item.totalPrice
  }))
  const maxValue = Math.max(...chartData.map((item) => Number(item.value || 0)), 1)
  echartsOption.legend = { data: chartData.map((item) => item.name) }
  echartsOption.series[0]['max'] = maxValue
  echartsOption.series[0]['data'] = chartData
}

/** 获取统计数据 */
const loadData = async () => {
  loading.value = true
  try {
    list.value = await StatisticFunnelApi.getBusinessSummaryByStatus(props.queryParams)
    fillChart(list.value)
  } finally {
    loading.value = false
  }
}
defineExpose({ loadData })

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>
