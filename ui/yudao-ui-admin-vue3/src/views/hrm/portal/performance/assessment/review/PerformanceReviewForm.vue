<template>
  <el-drawer v-model="drawerVisible" title="绩效评分" size="880px" destroy-on-close>
    <div v-loading="loading" class="min-w-0">
      <div class="mb-16px flex items-start justify-between gap-16px">
        <div>
          <div class="text-20px text-[var(--el-text-color-primary)] font-600">
            {{ detail.employeeName || '-' }}
          </div>
          <div class="mt-4px text-13px text-[var(--el-text-color-secondary)]">
            {{ detail.name || '-' }}
          </div>
        </div>
        <div class="flex items-center gap-8px whitespace-nowrap">
          <el-tag type="warning" effect="plain">{{ currentStage?.name || '待评分' }}</el-tag>
          <span>权重 {{ currentStage?.weight || 0 }}%</span>
        </div>
      </div>

      <div
        v-if="detail.reviewStages?.length"
        class="mb-16px border-t border-t-solid border-t-[var(--el-border-color-lighter)]"
      >
        <div
          v-for="stage in detail.reviewStages"
          :key="stage.id"
          class="min-h-52px grid grid-cols-[minmax(180px,1fr)_70px_80px_70px] items-center border-b border-b-solid border-b-[var(--el-border-color-lighter)]"
        >
          <div class="flex flex-col">
            <span>{{ stage.name }}</span>
            <span class="mt-4px text-13px text-[var(--el-text-color-secondary)]">
              {{ stage.handlerName || '-' }}
            </span>
          </div>
          <span>{{ stage.weight || 0 }}%</span>
          <el-tag
            v-if="stage.status === HrmPerformanceAssessmentStageStatus.PROCESSED"
            type="success"
            effect="plain"
          >
            已完成
          </el-tag>
          <el-tag
            v-else-if="stage.status === HrmPerformanceAssessmentStageStatus.PENDING"
            type="warning"
            effect="plain"
          >
            待评分
          </el-tag>
          <el-tag v-else type="info" effect="plain">未开始</el-tag>
          <span class="text-right">{{ stage.score ?? '-' }}</span>
        </div>
      </div>

      <el-alert
        v-if="currentStage?.rejectReason"
        class="mb-16px"
        :closable="false"
        type="warning"
        show-icon
        :title="`评分被驳回：${currentStage.rejectReason}`"
      />

      <div
        v-if="scorePreview"
        class="mb-16px min-h-48px flex items-center justify-between gap-20px border-y border-y-solid border-y-[var(--el-border-color-lighter)] py-8px"
        aria-live="polite"
      >
        <div class="flex items-center gap-8px">
          <span class="text-13px text-[var(--el-text-color-secondary)]">本阶段试算</span>
          <strong>{{ scorePreview.stageScore ?? '-' }} 分</strong>
          <el-tag v-if="scorePreview.stageResultLevel" size="small" effect="plain">
            {{ scorePreview.stageResultLevel }}
          </el-tag>
        </div>
        <div class="flex items-center gap-8px">
          <span class="text-13px text-[var(--el-text-color-secondary)]">当前累计分</span>
          <strong>{{ scorePreview.cumulativeScore ?? '-' }} 分</strong>
          <el-tag
            v-if="scorePreview.cumulativeResultLevel"
            size="small"
            type="success"
            effect="plain"
          >
            {{ scorePreview.cumulativeResultLevel }}
          </el-tag>
        </div>
      </div>

      <el-alert
        class="mb-12px"
        :closable="false"
        type="info"
        show-icon
        :title="`单项评分范围为 0～${detail.upperLimitScore ?? '-'} 分，最多保留两位小数；总分按评分、维度权重和指标权重计算。`"
      />

      <el-table :data="detail.quotas || []" border>
        <el-table-column label="维度" prop="dimensionName" width="110" show-overflow-tooltip />
        <el-table-column label="指标" prop="name" min-width="145" show-overflow-tooltip />
        <el-table-column label="目标值" prop="targetValue" min-width="125" show-overflow-tooltip />
        <el-table-column label="实际值" min-width="125">
          <template #default="scope">
            <el-input v-model="scope.row.actualValue" maxlength="1000" placeholder="实际完成情况" />
          </template>
        </el-table-column>
        <el-table-column label="评分" width="110">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.finalScore"
              :min="0"
              :max="detail.upperLimitScore"
              :precision="2"
              :controls="false"
              aria-label="指标评分"
              class="!w-1/1"
              @change="schedulePreview"
            />
          </template>
        </el-table-column>
        <el-table-column label="评语" min-width="160">
          <template #default="scope">
            <el-input v-model="scope.row.comment" maxlength="1000" placeholder="指标评语" />
          </template>
        </el-table-column>
      </el-table>

      <el-input
        v-model="stageComment"
        class="mt-16px"
        type="textarea"
        :rows="3"
        maxlength="2000"
        :placeholder="currentStage?.raterType === 4 ? '自评说明' : '评分说明'"
        show-word-limit
      />
    </div>

    <template #footer>
      <el-button
        v-if="canReject"
        :loading="submitting"
        plain
        type="danger"
        @click="rejectPreviousStage"
      >
        驳回上一阶段
      </el-button>
      <el-button @click="drawerVisible = false">取 消</el-button>
      <el-button :loading="submitting" type="primary" @click="submitReview">提交评分</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { useDebounceFn } from '@vueuse/core'
import type { PerformanceScorePreviewVO } from '@/api/hrm/performance/assessment'
import * as PerformanceAssessmentApi from '@/api/hrm/portal/performance/assessment'
import {
  HrmPerformanceAssessmentStageStatus,
  HrmPerformanceRaterType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmPortalPerformanceReviewForm' })

const message = useMessage() // 消息弹窗
const drawerVisible = ref(false) // 抽屉是否展示
const loading = ref(false) // 加载中
const submitting = ref(false) // 提交中
const detail = ref<PerformanceAssessmentApi.PortalPerformanceAssessmentVO>({}) // 详情数据
const stageComment = ref('') // 阶段评语
const scorePreview = ref<PerformanceScorePreviewVO>() // 分数预览
const currentStage = computed(() => detail.value.currentReviewStage) // 当前评分阶段
const canReject = computed(
  () =>
    currentStage.value?.rejectAuthority === true &&
    !!detail.value.reviewStages?.some(
      (stage) =>
        stage.status === HrmPerformanceAssessmentStageStatus.PROCESSED &&
        (stage.sort || 0) < (currentStage.value?.sort || 0)
    )
)

/** 监听抽屉展示状态 */
watch(drawerVisible, (visible) => {
  document.body.classList.toggle('hrm-performance-review-open', visible)
})
/** 清理页面样式 */
onBeforeUnmount(() => document.body.classList.remove('hrm-performance-review-open'))

/** 打开弹窗 */
async function open(assessmentId?: number, stageId?: number) {
  if (!assessmentId || !stageId) {
    return
  }
  drawerVisible.value = true
  loading.value = true
  stageComment.value = ''
  scorePreview.value = undefined
  try {
    // 获取表单数据
    detail.value = await PerformanceAssessmentApi.getPerformanceAssessment(assessmentId, stageId)
    stageComment.value = detail.value.currentReviewStage?.comment || ''
    const scoreMap = new Map(
      (detail.value.currentReviewStage?.quotaScoreList || []).map((score) => [
        score.assessmentQuotaId,
        score.score
      ])
    )
    detail.value.quotas?.forEach((quota) => {
      quota.finalScore = scoreMap.get(quota.id)
    })
    schedulePreview()
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 预览绩效分数 */
async function previewScore() {
  const stage = currentStage.value
  const quotaList = detail.value.quotas || []
  if (
    !detail.value.id ||
    !stage?.id ||
    !quotaList.length ||
    quotaList.some((quota) => quota.finalScore === undefined || quota.finalScore === null)
  ) {
    scorePreview.value = undefined
    return
  }
  try {
    scorePreview.value = await PerformanceAssessmentApi.previewPerformanceAssessmentScore({
      assessmentId: detail.value.id,
      reviewStageId: stage.id,
      quotas: quotaList
    })
  } catch {
    scorePreview.value = undefined
  }
}
const schedulePreview = useDebounceFn(previewScore, 250) // 评分预览调度器

/** 驳回至上一评分阶段 */
async function rejectPreviousStage() {
  const stage = currentStage.value
  if (!detail.value.id || !stage?.id) {
    return
  }
  try {
    const { value } = await message.prompt('请输入驳回原因', '驳回上一评分阶段')
    const reason = value?.trim()
    if (!reason) {
      message.warning('驳回原因不能为空')
      return
    }
    submitting.value = true
    await PerformanceAssessmentApi.rejectPerformanceAssessmentReviewStage({
      assessmentId: detail.value.id,
      reviewStageId: stage.id,
      reason
    })
    message.success('上一评分阶段已驳回')
    drawerVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } catch {
  } finally {
    submitting.value = false
  }
}

/** 提交绩效评分 */
async function submitReview() {
  const stage = currentStage.value
  if (!detail.value.id || !stage?.id) {
    return
  }
  const quotaList = detail.value.quotas || []
  if (
    !quotaList.length ||
    quotaList.some((quota) => quota.finalScore === undefined || quota.finalScore === null)
  ) {
    message.error('请完成全部指标评分')
    return
  }
  if (stage.requiredSetting && !stageComment.value.trim()) {
    message.error('请填写本阶段评语')
    return
  }
  // 提交请求
  submitting.value = true
  try {
    await PerformanceAssessmentApi.scorePerformanceAssessment({
      assessmentId: detail.value.id,
      reviewStageId: stage.id,
      comment: stageComment.value.trim(),
      selfComment:
        stage.raterType === HrmPerformanceRaterType.SELF ? stageComment.value.trim() : undefined,
      reviewerComment:
        stage.raterType === HrmPerformanceRaterType.SELF ? undefined : stageComment.value.trim(),
      quotas: quotaList
    })
    message.success('当前阶段评分已提交')
    drawerVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
:global(body.hrm-performance-review-open .el-backtop) {
  display: none;
}
</style>
