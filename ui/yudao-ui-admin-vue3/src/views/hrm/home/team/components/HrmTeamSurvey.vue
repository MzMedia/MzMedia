<template>
  <ContentWrap title="团队概况">
    <div class="grid grid-cols-2 gap-x-16px gap-y-8px lt-lg:grid-cols-1">
      <div v-for="chart in charts" :key="chart.title" class="min-h-260px">
        <div class="mb-4px text-center text-14px text-[var(--el-text-color-regular)]">
          {{ chart.title }}
        </div>
        <Echart
          v-if="hasData(chart.data)"
          :height="230"
          :options="buildChartOptions(chart.data, chart.formatType)"
        />
        <el-empty v-else :image-size="64" description="暂无数据" />
      </div>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { EChartsOption } from 'echarts'
import type { HrmTeamHomeAnalysisItemVO, HrmTeamHomeSurveyVO } from '@/api/hrm/home'
import { DICT_TYPE } from '@/utils/dict'
import { formatHrmAnalysisDictType, formatHrmAnalysisRangeType } from '@/views/hrm/utils/format'
import {
  HrmTeamHomeAgeRangeType,
  HrmTeamHomeCompanyAgeRangeType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmTeamHomeSurvey' })

const props = defineProps<{
  survey?: HrmTeamHomeSurveyVO
}>()

const ageRangeNames: Record<number, string> = {
  [HrmTeamHomeAgeRangeType.UNDER_18]: '17以下',
  [HrmTeamHomeAgeRangeType.AGE_18_TO_25]: '18-25',
  [HrmTeamHomeAgeRangeType.AGE_26_TO_35]: '26-35',
  [HrmTeamHomeAgeRangeType.AGE_36_TO_45]: '36-45',
  [HrmTeamHomeAgeRangeType.AGE_46_TO_55]: '46-55',
  [HrmTeamHomeAgeRangeType.AGE_56_AND_ABOVE]: '56以上'
}
const companyAgeRangeNames: Record<number, string> = {
  [HrmTeamHomeCompanyAgeRangeType.WITHIN_3_MONTHS]: '3个月内',
  [HrmTeamHomeCompanyAgeRangeType.MONTHS_3_TO_6]: '3-6个月',
  [HrmTeamHomeCompanyAgeRangeType.MONTHS_6_TO_1_YEAR]: '6个月-1年',
  [HrmTeamHomeCompanyAgeRangeType.YEARS_1_TO_3]: '1-3年',
  [HrmTeamHomeCompanyAgeRangeType.YEARS_3_TO_5]: '3-5年',
  [HrmTeamHomeCompanyAgeRangeType.YEARS_5_TO_10]: '5-10年',
  [HrmTeamHomeCompanyAgeRangeType.YEARS_10_AND_ABOVE]: '10年以上'
}

const charts = computed(() => [
  {
    title: '员工状态占比',
    data: props.survey?.statusAnalysis || [],
    formatType: (type: number | null) =>
      formatHrmAnalysisDictType(DICT_TYPE.HRM_EMPLOYEE_STATUS, type)
  },
  {
    title: '男女性别占比',
    data: props.survey?.sexAnalysis || [],
    formatType: (type: number | null) => formatHrmAnalysisDictType(DICT_TYPE.SYSTEM_USER_SEX, type)
  },
  {
    title: '成员年龄占比',
    data: props.survey?.ageAnalysis || [],
    formatType: (type: number | null) => formatHrmAnalysisRangeType(ageRangeNames, type)
  },
  {
    title: '成员司龄占比',
    data: props.survey?.companyAgeAnalysis || [],
    formatType: (type: number | null) => formatHrmAnalysisRangeType(companyAgeRangeNames, type)
  }
])

/** 是否存在统计数据 */
function hasData(data: HrmTeamHomeAnalysisItemVO[]) {
  return data.some((item) => item.count > 0)
}

/** 构建团队概况饼图 */
function buildChartOptions(
  data: HrmTeamHomeAnalysisItemVO[],
  formatType: (type: number | null) => string
): EChartsOption {
  const chartData = data.filter((item) => item.count > 0)
  return {
    color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#00a6a6', '#7b61ff', '#d97706'],
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 人（{d}%）'
    },
    legend: {
      bottom: 0,
      type: 'scroll'
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '43%'],
        avoidLabelOverlap: true,
        label: {
          formatter: '{b}\n{c} 人'
        },
        data: chartData.map((item) => ({ name: formatType(item.type), value: item.count }))
      }
    ]
  }
}
</script>
