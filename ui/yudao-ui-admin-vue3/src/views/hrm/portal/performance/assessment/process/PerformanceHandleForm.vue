<template>
  <Dialog v-model="dialogVisible" :title="title" width="900px">
    <div v-loading="loading">
      <el-descriptions :column="3" border class="mb-16px">
        <el-descriptions-item label="考核名称">{{ detail.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="被考核人">
          {{ detail.employeeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="工号">{{ detail.jobNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">
          {{ detail.currentStage?.name || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="绩效得分">
          {{ formatHrmScore(detail.score) }}
        </el-descriptions-item>
        <el-descriptions-item label="绩效等级">
          {{ detail.resultLevel || '-' }}
        </el-descriptions-item>
        <template v-if="mode === 'appeal'">
          <el-descriptions-item label="申诉原因" :span="3">
            {{ detail.appealReason || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="申诉时间">
            {{ formatHrmDateTime(detail.appealSubmitTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="申诉附件" :span="2">
            <div v-if="detail.appealFileUrls?.length" class="flex flex-wrap gap-8px">
              <el-link
                v-for="url in detail.appealFileUrls"
                :key="url"
                type="primary"
                underline="never"
                @click="openSafeUrl(url)"
              >
                {{ getFileNameFromUrl(url) }}
              </el-link>
            </div>
            <span v-else>-</span>
          </el-descriptions-item>
        </template>
      </el-descriptions>

      <el-table :data="detail.quotas || []" border class="mb-16px" max-height="300">
        <el-table-column label="维度" prop="dimensionName" min-width="120" />
        <el-table-column label="指标" prop="name" min-width="150" show-overflow-tooltip />
        <el-table-column label="目标值" prop="targetValue" min-width="120" show-overflow-tooltip />
        <el-table-column label="实际值" prop="actualValue" min-width="120" show-overflow-tooltip />
        <el-table-column label="最终分" width="90" align="center">
          <template #default="scope">{{ formatHrmScore(scope.row.finalScore) }}</template>
        </el-table-column>
      </el-table>

      <el-form label-width="110px">
        <el-form-item v-if="mode === 'result-audit'" label="退回评分节点">
          <el-checkbox-group v-model="reviewStageIds">
            <el-checkbox v-for="stage in completedReviewStages" :key="stage.id" :value="stage.id">
              {{ stage.name || '评分阶段' }}
              <span v-if="stage.handlerName">（{{ stage.handlerName }}）</span>
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-else label="申诉评分节点">
          <span>{{ appealReviewStageNames || '-' }}</span>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input
            v-model="comment"
            :rows="3"
            maxlength="500"
            placeholder="请输入处理意见"
            show-word-limit
            type="textarea"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button :disabled="loading" @click="dialogVisible = false">取 消</el-button>
      <el-button :loading="submitting" type="danger" @click="submitForm(false)">驳 回</el-button>
      <el-button :loading="submitting" type="primary" @click="submitForm(true)">通 过</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { getFileNameFromUrl } from '@/utils/file'
import { openSafeUrl } from '@/utils/url'
import * as PerformanceAssessmentApi from '@/api/hrm/portal/performance/assessment'
import {
  HrmPerformanceAssessmentStageStatus,
  HrmPerformanceConfirmationResult
} from '@/views/hrm/utils/constants'
import { formatHrmDateTime, formatHrmScore } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalPerformanceHandleForm' })

const props = defineProps<{
  mode: 'result-audit' | 'appeal'
}>()

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否展示
const loading = ref(false) // 加载中
const submitting = ref(false) // 提交中
const detail = ref<PerformanceAssessmentApi.PortalPerformanceAssessmentVO>({}) // 详情数据
const reviewStageIds = ref<number[]>([]) // 评分阶段编号
const comment = ref('') // 处理意见
const title = computed(() => (props.mode === 'appeal' ? '绩效申诉确认' : '绩效结果审核')) // 弹窗标题
const completedReviewStages = computed(() =>
  (detail.value.reviewStages || []).filter(
    (stage) => stage.id != null && stage.status === HrmPerformanceAssessmentStageStatus.PROCESSED
  )
)
const appealReviewStageNames = computed(() => {
  const selectedIds = new Set(detail.value.appealReviewStageIds || [])
  return completedReviewStages.value
    .filter((stage) => stage.id != null && selectedIds.has(stage.id))
    .map((stage) => stage.name || '评分阶段')
    .join('、')
})

/** 打开弹窗 */
async function open(assessmentId?: number, stageId?: number) {
  if (!assessmentId || !stageId) {
    return
  }
  dialogVisible.value = true
  loading.value = true
  detail.value = {}
  reviewStageIds.value = []
  comment.value = ''
  try {
    // 获取表单数据
    detail.value = await PerformanceAssessmentApi.getPerformanceAssessment(assessmentId, stageId)
    const latestStage = completedReviewStages.value[completedReviewStages.value.length - 1]
    reviewStageIds.value = latestStage?.id ? [latestStage.id] : []
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 处理当前绩效阶段 */
async function submitForm(pass: boolean) {
  if (!detail.value.id || !detail.value.currentStage?.id) {
    return
  }
  if (!pass && props.mode === 'result-audit' && !reviewStageIds.value.length) {
    message.warning('请选择需要退回的评分节点')
    return
  }
  await message.confirm(`确认${pass ? '通过' : '驳回'}当前${title.value}？`)
  // 提交请求
  submitting.value = true
  try {
    const data: PerformanceAssessmentApi.PerformanceHandleStageReqVO = {
      assessmentId: detail.value.id,
      stageId: detail.value.currentStage.id,
      pass: pass ? HrmPerformanceConfirmationResult.PASS : HrmPerformanceConfirmationResult.REJECT,
      comment: comment.value.trim() || undefined,
      reviewStageIds: !pass && props.mode === 'result-audit' ? reviewStageIds.value : undefined
    }
    if (props.mode === 'appeal') {
      await PerformanceAssessmentApi.handlePerformanceAssessmentAppeal(data)
    } else {
      await PerformanceAssessmentApi.handlePerformanceAssessmentResultAudit(data)
    }
    message.success(`${title.value}处理成功`)
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
