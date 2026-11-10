<!-- 数据统计 - 业绩目标完成情况 -->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="选择年份" prop="year">
        <el-date-picker
          v-model="queryParams.year"
          class="!w-240px"
          type="year"
          value-format="YYYY"
          @change="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标类型" prop="bizType">
        <el-select
          v-model="queryParams.bizType"
          class="!w-240px"
          placeholder="目标类型"
          @change="handleQuery"
        >
          <el-option
            v-for="item in bizTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="归属部门" prop="deptId">
        <el-tree-select
          v-model="queryParams.deptId"
          :data="deptList"
          :props="defaultProps"
          check-strictly
          class="!w-240px"
          node-key="id"
          placeholder="请选择归属部门"
          @change="handleDeptChange"
        />
      </el-form-item>
      <el-form-item label="员工" prop="userId">
        <el-select
          v-model="queryParams.userId"
          class="!w-240px"
          clearable
          placeholder="员工"
          @change="handleQuery"
        >
          <el-option
            v-for="user in userListByDeptId"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          查询
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- Echarts图 -->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" />
    </el-skeleton>
  </el-card>

  <!-- 统计列表 -->
  <el-card shadow="never" class="mt-16px">
    <el-table v-loading="loading" :data="list" show-summary :summary-method="getSummaries">
      <el-table-column label="月份" align="center" prop="month" min-width="120">
        <template #default="{ row }">
          {{ formatMonth(row.month) }}
        </template>
      </el-table-column>
      <el-table-column
        label="目标金额（元）"
        align="right"
        prop="targetPrice"
        min-width="160"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="完成金额（元）"
        align="right"
        prop="currentPrice"
        min-width="160"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column label="完成率" align="right" prop="completionRate" min-width="120">
        <template #default="{ row }"> {{ row.completionRate }}% </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script lang="ts" setup>
import { EChartsOption } from 'echarts'
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import {
  CrmStatisticsPerformanceTargetRespVO,
  StatisticsPerformanceTargetApi
} from '@/api/crm/statistics/performanceTarget'
import { BizTypeEnum } from '@/api/crm/permission'
import { useUserStore } from '@/store/modules/user'
import { defaultProps, handleTree } from '@/utils/tree'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmStatisticsPerformanceTarget' })

const queryParams = reactive({
  deptId: useUserStore().getUser.deptId,
  userId: undefined,
  year: String(new Date().getFullYear()),
  bizType: BizTypeEnum.CRM_CONTRACT
})

// 目标类型选项
const bizTypeOptions = [
  { label: '销售目标', value: BizTypeEnum.CRM_CONTRACT },
  { label: '回款目标', value: BizTypeEnum.CRM_RECEIVABLE }
]

const queryFormRef = ref() // 搜索的表单
const loading = ref(false) // 加载中
const list = ref<CrmStatisticsPerformanceTargetRespVO[]>([]) // 列表的数据
const deptList = ref<Tree[]>([]) // 部门树形结构
const userList = ref<UserApi.UserVO[]>([]) // 全量用户清单

/** 根据选择的部门筛选员工清单 */
const userListByDeptId = computed(() =>
  queryParams.deptId
    ? userList.value.filter((user: UserApi.UserVO) => user.deptId === queryParams.deptId)
    : []
)

/** 柱状图配置：纵向 */
const echartsOption = reactive<EChartsOption>({
  grid: {
    left: 20,
    right: 40,
    bottom: 72,
    containLabel: true
  },
  legend: {
    bottom: 8
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  toolbox: {
    feature: {
      dataZoom: {
        xAxisIndex: false
      },
      brush: {
        type: ['lineX', 'clear']
      },
      saveAsImage: { show: true, name: '业绩目标完成情况' }
    }
  },
  xAxis: {
    type: 'category',
    name: '月份',
    data: []
  },
  yAxis: [
    {
      type: 'value',
      name: '金额（元）'
    },
    {
      type: 'value',
      name: '完成率',
      axisLabel: {
        formatter: '{value}%'
      }
    }
  ],
  series: [
    {
      name: '目标金额（元）',
      type: 'bar',
      data: []
    },
    {
      name: '完成金额（元）',
      type: 'bar',
      data: []
    },
    {
      name: '完成率（%）',
      type: 'line',
      yAxisIndex: 1,
      data: []
    }
  ]
}) as EChartsOption

/** 获取接口参数 */
const getApiParams = () => ({
  ...queryParams,
  year: Number(queryParams.year)
})

/** 格式化月份 */
const formatMonth = (month: number) => `${queryParams.year}-${String(month).padStart(2, '0')}`

/** 获取业绩目标完成情况 */
const loadData = async () => {
  loading.value = true
  try {
    // 1. 加载统计数据
    const data = await StatisticsPerformanceTargetApi.getPerformanceTargetSummary(getApiParams())
    // 2.1 更新列表数据
    list.value = data
    // 2.2 更新 Echarts 数据
    if (echartsOption.xAxis && echartsOption.xAxis['data']) {
      echartsOption.xAxis['data'] = data.map((item: CrmStatisticsPerformanceTargetRespVO) =>
        formatMonth(item.month)
      )
    }
    if (echartsOption.series) {
      echartsOption.series[0]['data'] = data.map(
        (item: CrmStatisticsPerformanceTargetRespVO) => item.targetPrice
      )
      echartsOption.series[1]['data'] = data.map(
        (item: CrmStatisticsPerformanceTargetRespVO) => item.currentPrice
      )
      echartsOption.series[2]['data'] = data.map(
        (item: CrmStatisticsPerformanceTargetRespVO) => item.completionRate
      )
    }
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  loadData()
}

/** 部门变化时重置员工 */
const handleDeptChange = () => {
  queryParams.userId = undefined
  handleQuery()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 统计合计行 */
const getSummaries = ({ columns, data }: { columns: any[]; data: any[] }) => {
  const targetTotal = data.reduce((sum, item) => sum + Number(item.targetPrice || 0), 0)
  const currentTotal = data.reduce((sum, item) => sum + Number(item.currentPrice || 0), 0)
  const completionRate = targetTotal > 0 ? ((currentTotal / targetTotal) * 100).toFixed(2) : '0.00'
  return columns.map((column, index) => {
    if (index === 0) {
      return '合计'
    }
    if (column.property === 'targetPrice') {
      return erpPriceInputFormatter(targetTotal)
    }
    if (column.property === 'currentPrice') {
      return erpPriceInputFormatter(currentTotal)
    }
    if (column.property === 'completionRate') {
      return `${completionRate}%`
    }
    return ''
  })
}

/** 初始化 */
onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
  userList.value = await UserApi.getSimpleUserList()
  await loadData()
})
</script>
