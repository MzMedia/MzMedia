<template>
  <ContentWrap :title="`人事概况（${currentMonthRange}）`">
    <div class="grid grid-cols-7 lt-xl:grid-cols-4">
      <button
        v-for="(item, index) in surveyItems"
        :key="item.label"
        :disabled="!canQueryEmployee"
        class="min-h-88px flex flex-col items-center justify-center border-0 bg-transparent"
        :class="[
          canQueryEmployee ? 'group cursor-pointer' : 'cursor-default',
          index < surveyItems.length - 1
            ? 'border-r border-r-solid border-r-[var(--el-border-color-lighter)]'
            : '',
          index === 0 ? 'mr-24px border-r-[var(--el-border-color)] lt-xl:mr-0' : ''
        ]"
        type="button"
        @click="goEmployeeSurvey(item.surveyType)"
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
import type { HrmHrHomeEmployeeSurveyVO } from '@/api/hrm/home'
import { checkPermi } from '@/utils/permission'
import {
  HrmEmployeeStatusTab,
  HrmEmployeeSurveyType,
  type HrmEmployeeSurveyTypeValue
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmHomeEmployeeSurvey' })

const props = defineProps<{
  survey?: HrmHrHomeEmployeeSurveyVO
}>()

const router = useRouter() // 路由
const canQueryEmployee = checkPermi(['hrm:employee:query']) // 是否拥有员工查询权限
const currentMonthRange = `${dayjs().startOf('month').format('YYYY.MM.DD')}-${dayjs()
  .endOf('month')
  .format('YYYY.MM.DD')}`

const surveyItems = computed(() => [
  {
    label: '在职',
    value: props.survey?.activeCount || 0,
    surveyType: undefined
  },
  {
    label: '入职',
    value: props.survey?.entryThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.ENTRY
  },
  {
    label: '待入职',
    value: props.survey?.pendingEntryThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.PENDING_ENTRY
  },
  {
    label: '离职',
    value: props.survey?.leaveThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.LEAVE
  },
  {
    label: '待离职',
    value: props.survey?.pendingLeaveThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.PENDING_LEAVE
  },
  {
    label: '转正',
    value: props.survey?.regularThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.REGULAR
  },
  {
    label: '调岗',
    value: props.survey?.transferThisMonthCount || 0,
    surveyType: HrmEmployeeSurveyType.TRANSFER
  }
])

/** 打开人事概况对应的员工列表 */
function goEmployeeSurvey(surveyType?: HrmEmployeeSurveyTypeValue) {
  if (!canQueryEmployee) {
    return
  }
  if (surveyType === undefined) {
    router.push({
      name: 'HrmEmployee',
      query: { statusCategory: HrmEmployeeStatusTab.ACTIVE }
    })
    return
  }
  router.push({ name: 'HrmEmployee', query: { surveyType } })
}
</script>
