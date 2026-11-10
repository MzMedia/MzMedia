<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between gap-16px">
      <div class="min-w-0">
        <div class="flex flex-wrap items-center gap-10px">
          <span class="break-all text-xl font-bold">{{ candidate.name || '-' }}</span>
          <dict-tag
            v-if="candidate.status != null"
            :type="DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS"
            :value="candidate.status"
          />
        </div>
        <div class="mt-6px text-sm text-[var(--el-text-color-secondary)]">
          候选人编号：{{ candidate.id || '-' }}
        </div>
      </div>
      <div><slot></slot></div>
    </div>
    <ContentWrap class="mt-10px">
      <el-descriptions :column="5" direction="vertical">
        <el-descriptions-item label="应聘职位">
          <span>{{ candidate.postName || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="用人部门">
          <span>{{ candidate.deptName || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="招聘负责人">
          <span>{{ candidate.ownerEmployeeName || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="招聘渠道">
          <span>{{ candidate.channelName || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="当前面试">
          {{ candidate.interviewTime ? formatDate(candidate.interviewTime) : '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'

defineOptions({ name: 'HrmRecruitCandidateDetailsHeader' })

defineProps<{
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
  loading: boolean
}>()
</script>
