<template>
  <el-table v-loading="loading" :data="list" border class="w-full">
    <el-table-column type="index" label="序号" width="70" align="center" />

    <template
      v-if="
        activeTab === HrmPerformanceStageType.FILL_QUOTA ||
        activeTab === HrmPerformanceStageType.RESULT_CONFIRM
      "
    >
      <el-table-column label="考核名称" prop="name" min-width="220" show-overflow-tooltip />
      <el-table-column label="考核周期" min-width="210">
        <template #default="scope">
          {{ formatHrmDate(scope.row.startTime) }} 至 {{ formatHrmDate(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column label="当前阶段" width="130" align="center">
        <template #default="scope">
          <span
            v-if="
              activeTab === HrmPerformanceStageType.FILL_QUOTA ||
              activeTab === HrmPerformanceStageType.RESULT_CONFIRM
            "
          >
            {{ scope.row.currentStage?.name || '-' }}
          </span>
          <dict-tag
            v-else
            :type="DICT_TYPE.HRM_PERFORMANCE_STAGE_STATUS"
            :value="scope.row.stageType ?? 0"
          />
        </template>
      </el-table-column>
      <el-table-column label="绩效得分" width="110" align="center">
        <template #default="scope">{{ formatHrmScore(scope.row.score) }}</template>
      </el-table-column>
      <el-table-column label="绩效等级" width="110" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.resultLevel" type="success" effect="plain">
            {{ scope.row.resultLevel }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="绩效系数" width="100" align="center">
        <template #default="scope">{{ scope.row.coefficient ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="260" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="emit('detail', scope.row)">详情</el-button>
          <el-button
            v-if="
              activeTab === HrmPerformanceStageType.FILL_QUOTA &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING &&
              scope.row.stageType === HrmPerformanceStageType.FILL_QUOTA
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="primary"
            @click="emit('quota', scope.row.id)"
          >
            制定指标
          </el-button>
          <el-button
            v-if="
              activeTab === HrmPerformanceStageType.RESULT_CONFIRM &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="success"
            @click="emit('result-confirm', scope.row.id)"
          >
            确认结果
          </el-button>
          <el-button
            v-if="
              activeTab === HrmPerformanceStageType.RESULT_CONFIRM &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING &&
              scope.row.appealStatus !== HrmPerformanceAppealStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="warning"
            @click="emit('appeal', scope.row.id)"
          >
            提交申诉
          </el-button>
        </template>
      </el-table-column>
    </template>

    <template v-else>
      <el-table-column label="考核名称" prop="name" min-width="220" show-overflow-tooltip />
      <el-table-column label="被考核人" min-width="160">
        <template #default="scope">
          {{ scope.row.employeeName || '-' }}
          <span class="ml-6px text-12px text-[var(--el-text-color-secondary)]">
            {{ scope.row.jobNumber || '' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="当前阶段" width="140" align="center">
        <template #default="scope">
          <span v-if="activeTab === HrmPerformanceStageType.OTHER_SCORE">
            {{ scope.row.currentReviewStage?.name || '待评分' }}
          </span>
          <span
            v-else-if="
              activeTab === HrmPerformanceStageType.RESULT_AUDIT ||
              activeTab === HrmPerformanceStageType.APPEAL_CONFIRM
            "
          >
            {{ scope.row.currentStage?.name || '待处理' }}
          </span>
          <dict-tag
            v-else
            :type="DICT_TYPE.HRM_PERFORMANCE_STAGE_STATUS"
            :value="scope.row.stageType ?? 0"
          />
        </template>
      </el-table-column>
      <el-table-column
        v-if="activeTab === HrmPerformanceStageType.TARGET_CONFIRM"
        label="指标数"
        width="100"
        align="center"
      >
        <template #default="scope">{{ scope.row.quotas?.length || 0 }}</template>
      </el-table-column>
      <el-table-column
        v-else-if="activeTab === HrmPerformanceStageType.OTHER_SCORE"
        label="评分权重"
        width="100"
        align="center"
      >
        <template #default="scope">{{ scope.row.currentReviewStage?.weight || 0 }}%</template>
      </el-table-column>
      <el-table-column v-else label="绩效得分" width="100" align="center">
        <template #default="scope">{{ formatHrmScore(scope.row.score) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="110" align="center">
        <template #default="scope">
          <el-button
            v-if="
              activeTab === HrmPerformanceStageType.TARGET_CONFIRM &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="primary"
            @click="emit('target-confirm', scope.row.id, scope.row.currentStage?.id)"
          >
            去确认
          </el-button>
          <el-button
            v-else-if="
              activeTab === HrmPerformanceStageType.OTHER_SCORE &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="primary"
            @click="emit('review', scope.row.id, scope.row.currentReviewStage?.id)"
          >
            去评分
          </el-button>
          <el-button
            v-else-if="
              activeTab === HrmPerformanceStageType.RESULT_AUDIT &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="primary"
            @click="emit('result-audit', scope.row.id, scope.row.currentStage?.id)"
          >
            去审核
          </el-button>
          <el-button
            v-else-if="
              activeTab === HrmPerformanceStageType.APPEAL_CONFIRM &&
              activeStatus === HrmPerformanceAssessmentStageStatus.PENDING
            "
            v-hasPermi="['hrm:portal:performance:action']"
            link
            type="primary"
            @click="emit('appeal-handle', scope.row.id, scope.row.currentStage?.id)"
          >
            去确认
          </el-button>
          <el-button v-else link type="primary" @click="emit('detail', scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </template>
  </el-table>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import type { PortalPerformanceAssessmentVO } from '@/api/hrm/portal/performance/assessment'
import {
  HrmPerformanceAppealStatus,
  HrmPerformanceAssessmentStageStatus,
  HrmPerformanceStageType
} from '@/views/hrm/utils/constants'
import { formatHrmDate, formatHrmScore } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalPerformanceTaskTable' })

defineProps<{
  activeTab: number
  activeStatus: number
  loading: boolean
  list: PortalPerformanceAssessmentVO[]
}>()

const emit = defineEmits<{
  detail: [row: PortalPerformanceAssessmentVO]
  quota: [id?: number]
  'result-confirm': [id?: number]
  appeal: [id?: number]
  'target-confirm': [assessmentId?: number, stageId?: number]
  review: [assessmentId?: number, stageId?: number]
  'result-audit': [assessmentId?: number, stageId?: number]
  'appeal-handle': [assessmentId?: number, stageId?: number]
}>() // 定义详情与处理入口事件
</script>
