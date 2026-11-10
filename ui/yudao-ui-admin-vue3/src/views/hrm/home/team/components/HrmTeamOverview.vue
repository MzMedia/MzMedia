<template>
  <ContentWrap :title="`我的团队（${currentMonthRange}）`">
    <div class="grid grid-cols-4 lt-md:grid-cols-2">
      <button
        v-for="(item, index) in overviewItems"
        :key="item.label"
        :disabled="!canOpenEmployeeList"
        class="min-h-88px flex flex-col items-center justify-center border-0 bg-transparent"
        :class="[
          canOpenEmployeeList ? 'group cursor-pointer' : 'cursor-default',
          index < overviewItems.length - 1
            ? 'border-r border-r-solid border-r-[var(--el-border-color-lighter)]'
            : '',
          index === 0 ? 'mr-24px border-r-[var(--el-border-color)] lt-md:mr-0' : ''
        ]"
        type="button"
        @click="openEmployeeList(item.surveyType)"
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
  </ContentWrap>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import type { HrmTeamHomeOverviewVO } from '@/api/hrm/home'
import { checkPermi } from '@/utils/permission'
import {
  HrmEmployeeStatusTab,
  HrmEmployeeSurveyType,
  type HrmEmployeeSurveyTypeValue
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmTeamHomeOverview' })

const props = defineProps<{
  leaderEmployeeId?: number
  overview?: HrmTeamHomeOverviewVO
}>()

const router = useRouter() // 路由
const canOpenEmployeeList = computed(
  () => !!props.leaderEmployeeId && checkPermi(['hrm:employee:query'])
)
const currentMonthRange = `${dayjs().startOf('month').format('YYYY.MM.DD')}-${dayjs()
  .endOf('month')
  .format('YYYY.MM.DD')}`

const overviewItems = computed(() => [
  {
    label: '团队人数',
    value: props.overview?.employeeCount || 0,
    surveyType: undefined
  },
  {
    label: '本月入职',
    value: props.overview?.entryThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.ENTRY
  },
  {
    label: '本月离职',
    value: props.overview?.leaveThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.LEAVE
  },
  {
    label: '本月转正',
    value: props.overview?.regularThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.REGULAR
  }
])

/** 打开当前直属团队对应的员工列表 */
function openEmployeeList(surveyType?: HrmEmployeeSurveyTypeValue) {
  if (!canOpenEmployeeList.value) {
    return
  }
  router.push({
    name: 'HrmEmployee',
    query: {
      leaderEmployeeId: props.leaderEmployeeId,
      statusCategory: surveyType === undefined ? HrmEmployeeStatusTab.ACTIVE : undefined,
      surveyType
    }
  })
}
</script>
