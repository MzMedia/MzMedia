<template>
  <el-drawer v-model="drawerVisible" title="绩效详情" size="760px">
    <div v-loading="loading">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="绩效详情" name="detail">
          <el-descriptions v-if="assessment" :column="2" border>
            <el-descriptions-item label="考核名称" :span="2">
              {{ assessment.name || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="开始日期">
              {{ formatHrmDate(assessment.startTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="结束日期">
              {{ formatHrmDate(assessment.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="当前阶段">
              <dict-tag
                :type="DICT_TYPE.HRM_PERFORMANCE_STAGE_STATUS"
                :value="assessment.stageType ?? 0"
              />
            </el-descriptions-item>
            <el-descriptions-item label="绩效得分">
              {{ formatHrmScore(assessment.score) }}
            </el-descriptions-item>
            <el-descriptions-item label="绩效等级">
              {{ assessment.resultLevel || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="绩效系数">
              {{ assessment.coefficient ?? '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="归档时间" :span="2">
              {{ formatHrmDateTime(assessment.archiveTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="指标确认人">
              {{ assessment.targetConfirmationEmployeeName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="指标确认结果">
              <el-tag
                v-if="assessment.targetConfirmationResult === 1"
                type="success"
                effect="plain"
              >
                已通过
              </el-tag>
              <el-tag
                v-else-if="assessment.targetConfirmationResult === 0"
                type="danger"
                effect="plain"
              >
                已退回
              </el-tag>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="自评说明" :span="2">
              {{ assessment.selfComment || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="评分说明" :span="2">
              {{ assessment.reviewerComment || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="结果说明" :span="2">
              {{ assessment.resultComment || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="结果确认时间" :span="2">
              {{ formatHrmDateTime(assessment.resultConfirmationTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="指标确认意见" :span="2">
              {{ assessment.targetConfirmationComment || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="申诉状态">
              <dict-tag
                :type="DICT_TYPE.HRM_PERFORMANCE_APPEAL_STATUS"
                :value="assessment.appealStatus ?? 0"
              />
            </el-descriptions-item>
            <el-descriptions-item label="申诉提交时间">
              {{ formatHrmDateTime(assessment.appealSubmitTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="申诉完成时间">
              {{ formatHrmDateTime(assessment.appealTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="申诉原因" :span="2">
              {{ assessment.appealReason || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="申诉附件" :span="2">
              <div v-if="assessment.appealFileUrls?.length" class="flex flex-col items-start">
                <el-link
                  v-for="url in assessment.appealFileUrls"
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
            <el-descriptions-item label="申诉审批意见" :span="2">
              {{ assessment.appealComment || '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <div
            v-if="assessment?.quotas?.length"
            class="mb-12px mt-20px text-16px text-[var(--el-text-color-primary)] font-600"
          >
            绩效指标
          </div>
          <el-table v-if="assessment?.quotas?.length" :data="assessment.quotas" border>
            <el-table-column label="维度" prop="dimensionName" min-width="120" />
            <el-table-column label="指标" prop="name" min-width="160" />
            <el-table-column label="考核标准" prop="standard" min-width="200" />
            <el-table-column label="权重" width="80" align="center">
              <template #default="scope">{{ scope.row.weight || 0 }}%</template>
            </el-table-column>
            <el-table-column label="最终得分" width="90" align="center">
              <template #default="scope">{{ formatHrmScore(scope.row.finalScore) }}</template>
            </el-table-column>
          </el-table>

          <div
            v-if="assessment?.reviewStages?.length"
            class="mb-12px mt-20px text-16px text-[var(--el-text-color-primary)] font-600"
          >
            评分流程
          </div>
          <el-table v-if="assessment?.reviewStages?.length" :data="assessment.reviewStages" border>
            <el-table-column label="评分阶段" prop="name" min-width="130" />
            <el-table-column label="评分人" prop="handlerName" min-width="120" />
            <el-table-column label="权重" width="80" align="center">
              <template #default="scope">{{ scope.row.weight || 0 }}%</template>
            </el-table-column>
            <el-table-column label="阶段得分" width="90" align="center">
              <template #default="scope">{{ formatHrmScore(scope.row.score) }}</template>
            </el-table-column>
            <el-table-column label="评语" prop="comment" min-width="160" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="流程记录" name="record">
          <PerformanceProcessRecordTimeline :records="recordList" :loading="loading" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { getFileNameFromUrl } from '@/utils/file'
import { openSafeUrl } from '@/utils/url'
import * as PerformanceAssessmentApi from '@/api/hrm/portal/performance/assessment'
import PerformanceProcessRecordTimeline from '@/views/hrm/performance/assessment/components/PerformanceProcessRecordTimeline.vue'
import { HrmPerformanceStageType } from '@/views/hrm/utils/constants'
import { formatHrmDate, formatHrmDateTime, formatHrmScore } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalPerformanceAssessmentDetail' })

const message = useMessage() // 消息弹窗
const drawerVisible = ref(false) // 抽屉是否展示
const loading = ref(false) // 加载中
const activeTab = ref('detail') // 当前页签
const assessment = ref<PerformanceAssessmentApi.PortalPerformanceAssessmentVO>() // 考核详情
const recordList = ref<PerformanceAssessmentApi.PerformanceRecordVO[]>([]) // 流程记录列表

/** 打开绩效考核详情 */
async function open(
  row:
    | PerformanceAssessmentApi.PortalPerformanceAssessmentSummaryVO
    | PerformanceAssessmentApi.PortalPerformanceAssessmentVO,
  taskType?: number
) {
  if (!row.id) {
    return
  }
  let stageId: number | undefined
  if (taskType !== undefined) {
    const task = row as PerformanceAssessmentApi.PortalPerformanceAssessmentVO
    stageId =
      taskType === HrmPerformanceStageType.OTHER_SCORE
        ? task.currentReviewStage?.id
        : task.currentStage?.id
    if (!stageId) {
      message.error('绩效任务阶段不存在')
      return
    }
  }
  drawerVisible.value = true
  activeTab.value = 'detail'
  loading.value = true
  try {
    const [assessmentData, records] = await Promise.all([
      PerformanceAssessmentApi.getPerformanceAssessment(row.id, stageId),
      PerformanceAssessmentApi.getPerformanceAssessmentProcessRecordList(row.id, stageId)
    ])
    assessment.value = assessmentData
    recordList.value = records
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件
</script>
