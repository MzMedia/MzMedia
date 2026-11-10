<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between gap-16px">
      <div class="min-w-0">
        <div class="flex flex-wrap items-center gap-10px">
          <span class="break-all text-xl font-bold">{{ post.postName || '-' }}</span>
          <dict-tag
            v-if="post.status != null"
            :type="DICT_TYPE.HRM_RECRUIT_POST_STATUS"
            :value="post.status"
          />
        </div>
        <div class="mt-6px text-sm text-[var(--el-text-color-secondary)]">
          职位编号：{{ post.id || '-' }}
        </div>
      </div>
      <div>
        <slot></slot>
      </div>
    </div>
    <ContentWrap class="mt-10px">
      <el-descriptions :column="5" direction="vertical">
        <el-descriptions-item label="用人部门">{{ post.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作性质">
          <dict-tag
            v-if="post.jobNature != null"
            :type="DICT_TYPE.HRM_RECRUIT_JOB_NATURE"
            :value="post.jobNature"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="工作城市">{{ post.areaName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="招聘负责人">
          {{ post.ownerEmployeeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="招聘进度">
          {{ formatRecruitPostProgress(post) }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as RecruitPostApi from '@/api/hrm/recruit/post'
import { formatRecruitPostProgress } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmRecruitPostDetailsHeader' })

defineProps<{
  post: RecruitPostApi.HrmRecruitPostVO
  loading: boolean
}>()
</script>
