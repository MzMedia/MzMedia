<!-- 产品分类销售分析 -->
<template>
  <!-- Echarts图 -->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" />
    </el-skeleton>
  </el-card>

  <!-- 统计列表 -->
  <el-card shadow="never" class="mt-16px">
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true">
      <el-table-column label="序号" align="center" type="index" width="80" />
      <el-table-column label="产品分类" align="center" prop="categoryName" min-width="180" />
      <el-table-column label="合同数量" align="center" prop="contractCount" min-width="120" />
      <el-table-column label="销售数量" align="right" prop="productCount" min-width="120" />
      <el-table-column
        label="销售金额（元）"
        align="right"
        prop="productTotalPrice"
        min-width="160"
        :formatter="erpPriceTableColumnFormatter"
      />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { EChartsOption } from 'echarts'
import {
  CrmStatisticsProductCategoryRespVO,
  StatisticsProductApi
} from '@/api/crm/statistics/product'
import { erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmStatisticsProductCategorySummary' })

const props = defineProps<{ queryParams: any }>() // 搜索参数

const loading = ref(false) // 加载中
const list = ref<CrmStatisticsProductCategoryRespVO[]>([]) // 列表的数据

/** 饼图配置 */
const echartsOption = reactive<EChartsOption>({
  title: {
    text: '产品分类销量占比',
    left: 'center',
    bottom: 10
  },
  legend: {
    type: 'scroll',
    orient: 'vertical',
    left: 10,
    top: 20,
    bottom: 20
  },
  tooltip: {
    trigger: 'item',
    formatter: '{b}<br/>销售数量：{c}'
  },
  toolbox: {
    feature: {
      saveAsImage: { show: true, name: '产品分类销量占比' }
    }
  },
  series: [
    {
      name: '销售数量',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['55%', '48%'],
      data: []
    }
  ]
}) as EChartsOption

/** 获取产品分类销售分析 */
const loadData = async () => {
  loading.value = true
  try {
    // 1. 加载统计数据
    const data = await StatisticsProductApi.getProductCategorySummary(props.queryParams)
    // 2.1 更新列表数据
    list.value = data
    // 2.2 更新 Echarts 数据
    if (echartsOption.legend) {
      ;(echartsOption.legend as Record<string, unknown>)['data'] = data.map(
        (item: CrmStatisticsProductCategoryRespVO) => item.categoryName
      )
    }
    if (echartsOption.series) {
      echartsOption.series[0]['data'] = data.map((item: CrmStatisticsProductCategoryRespVO) => ({
        name: item.categoryName,
        value: item.productCount
      }))
    }
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
