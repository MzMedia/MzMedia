<template>
  <ContentWrap title="上月薪资概况">
    <div class="flex items-stretch">
      <div class="w-34% grid grid-cols-2 items-center">
        <button
          v-for="(item, index) in surveyItems"
          :key="item.label"
          :disabled="!canOpenSalary"
          class="min-h-88px flex flex-col items-center justify-center border-0 bg-transparent"
          :class="[
            canOpenSalary ? 'group cursor-pointer' : 'cursor-default',
            index < surveyItems.length - 1
              ? 'border-r border-r-solid border-r-[var(--el-border-color-lighter)]'
              : ''
          ]"
          type="button"
          @click="goSalaryRecord"
        >
          <strong
            class="text-24px text-[var(--el-text-color-primary)] leading-32px group-hover:text-[var(--el-color-primary)]"
          >
            {{ item.value }}
          </strong>
          <span
            class="mt-8px text-13px text-[var(--el-text-color-secondary)] group-hover:text-[var(--el-color-primary)]"
          >
            {{ item.label }}
          </span>
        </button>
      </div>
      <div class="min-w-0 flex-1">
        <Echart
          v-if="survey?.deptProportions.length"
          :height="220"
          :options="salaryDeptChartOptions"
        />
        <el-empty v-else :image-size="72" description="暂无数据" />
      </div>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { EChartsOption } from 'echarts'
import type { HrmHrHomeSalarySurveyVO } from '@/api/hrm/home'
import { checkPermi } from '@/utils/permission'
import { formatHrmMoneyWithThousands } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmHomeSalarySurvey' })

const props = defineProps<{
  survey?: HrmHrHomeSalarySurveyVO
}>()

const router = useRouter() // 路由
const canQuerySalary = checkPermi(['hrm:salary:month-record:query']) // 是否拥有薪资查询权限
const canOpenSalary = computed(() => canQuerySalary && !!props.survey?.monthRecordId) // 是否可查看薪资详情
const surveyItems = computed(() => [
  {
    label: '计薪人员',
    value: props.survey?.employeeCount || 0
  },
  {
    label: '实发工资（元）',
    value: formatHrmMoneyWithThousands(props.survey?.realPaySalary)
  }
])

/** 部门薪资占比图表 */
const salaryDeptChartOptions = computed<EChartsOption>(() => ({
  title: {
    text: '部门薪资占比',
    left: 'center',
    textStyle: {
      fontSize: 14,
      fontWeight: 500
    }
  },
  tooltip: {
    trigger: 'item',
    formatter: '{b}：{c}%'
  },
  legend: {
    type: 'scroll',
    bottom: 0,
    left: 'center'
  },
  series: [
    {
      type: 'pie',
      radius: '48%',
      center: ['50%', '46%'],
      stillShowZeroSum: false,
      data: (props.survey?.deptProportions || []).map((item) => ({
        name: item.deptName,
        value: Number((item.proportion * 100).toFixed(2))
      }))
    }
  ]
}))

/** 打开上月工资表详情 */
function goSalaryRecord() {
  if (!canOpenSalary.value) {
    return
  }
  router.push({
    name: 'HrmSalaryHistoryDetail',
    params: { id: props.survey?.monthRecordId }
  })
}
</script>
