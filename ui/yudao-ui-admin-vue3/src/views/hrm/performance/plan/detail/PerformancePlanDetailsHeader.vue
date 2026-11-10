<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between gap-16px">
      <div class="flex min-w-0 items-start gap-10px">
        <el-button link title="返回" @click="emit('back')">
          <Icon icon="ep:arrow-left" />
        </el-button>
        <div class="min-w-0">
          <div class="flex flex-wrap items-center gap-10px">
            <span class="break-all text-xl font-bold">{{ plan.name || '-' }}</span>
            <dict-tag
              v-if="plan.status != null"
              :type="DICT_TYPE.HRM_PERFORMANCE_PLAN_STATUS"
              :value="plan.status"
            />
            <dict-tag
              v-if="plan.stageType != null"
              :type="DICT_TYPE.HRM_PERFORMANCE_STAGE_STATUS"
              :value="plan.stageType"
            />
          </div>
          <div class="mt-6px text-sm text-[var(--el-text-color-secondary)]">
            计划编号：{{ plan.id || '-' }}
          </div>
        </div>
      </div>
      <div>
        <slot></slot>
      </div>
    </div>
    <ContentWrap class="mt-10px">
      <el-descriptions :column="5" direction="vertical">
        <el-descriptions-item label="考核周期">
          {{ formatHrmPerformancePlanCycle(plan) }}
        </el-descriptions-item>
        <el-descriptions-item label="起止日期">
          {{ formatHrmDateRange(plan.startTime, plan.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="考核模板">
          {{ plan.assessmentTemplateName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="参评人数">{{ plan.employeeCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="完成人数">{{ plan.finishedCount || 0 }}</el-descriptions-item>
      </el-descriptions>
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import type { PerformancePlanVO } from '@/api/hrm/performance/plan'
import { formatHrmDateRange, formatHrmPerformancePlanCycle } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPerformancePlanDetailsHeader' })

defineProps<{
  plan: PerformancePlanVO
  loading: boolean
}>()

const emit = defineEmits<{
  back: []
}>() // 定义 back 事件
</script>
