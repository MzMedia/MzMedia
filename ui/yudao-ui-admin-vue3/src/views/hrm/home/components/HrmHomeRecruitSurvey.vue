<template>
  <ContentWrap :title="`招聘动态（${recruitRange}）`">
    <div class="grid grid-cols-4">
      <button
        v-for="(item, index) in surveyItems"
        :key="item.label"
        :disabled="item.disabled"
        class="min-h-88px flex flex-col items-center justify-center border-0 bg-transparent"
        :class="[
          item.disabled ? 'cursor-default' : 'group cursor-pointer',
          index < surveyItems.length - 1
            ? 'border-r border-r-solid border-r-[var(--el-border-color-lighter)]'
            : ''
        ]"
        type="button"
        @click="goRecruitSurvey(item.action)"
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
import type { HrmHrHomeRecruitSurveyVO } from '@/api/hrm/home'
import { checkPermi } from '@/utils/permission'
import { HrmRecruitCandidateStatus } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmHomeRecruitSurvey' })

type RecruitAction = 'post' | 'pending-entry' | 'joined'

const props = defineProps<{
  survey?: HrmHrHomeRecruitSurveyVO
}>()

const router = useRouter() // 路由
const canQueryRecruitPost = checkPermi(['hrm:recruit:post:query']) // 是否拥有招聘职位查询权限
const canQueryRecruitCandidate = checkPermi(['hrm:recruit:candidate:query']) // 是否拥有候选人查询权限
const recruitRange = `${dayjs().subtract(6, 'month').format('YYYY.MM.DD')}-${dayjs().format(
  'YYYY.MM.DD'
)}`

const surveyItems = computed(() => [
  {
    label: '正在招聘职位',
    value: props.survey?.recruitingPostCount || 0,
    action: 'post' as RecruitAction,
    disabled: !canQueryRecruitPost
  },
  {
    label: '评选中',
    value: props.survey?.candidateInProcessCount || 0,
    action: undefined,
    disabled: true
  },
  {
    label: '待入职',
    value: props.survey?.pendingEntryCount || 0,
    action: 'pending-entry' as RecruitAction,
    disabled: !canQueryRecruitCandidate
  },
  {
    label: '已入职',
    value: props.survey?.joinedCount || 0,
    action: 'joined' as RecruitAction,
    disabled: !canQueryRecruitCandidate
  }
])

/** 打开招聘动态对应的列表 */
function goRecruitSurvey(action?: RecruitAction) {
  if (action === 'post' && canQueryRecruitPost) {
    router.push({ name: 'HrmRecruitPost' })
  } else if (action === 'pending-entry' && canQueryRecruitCandidate) {
    router.push({
      name: 'HrmRecruitCandidate',
      query: { status: HrmRecruitCandidateStatus.PENDING_ENTRY }
    })
  } else if (action === 'joined' && canQueryRecruitCandidate) {
    router.push({
      name: 'HrmRecruitCandidate',
      query: { status: HrmRecruitCandidateStatus.JOINED }
    })
  }
}
</script>
