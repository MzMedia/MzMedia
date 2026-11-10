<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">基本信息</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item label="职位名称">{{ post.postName || '-' }}</el-descriptions-item>
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
          <el-descriptions-item label="招聘人数">{{ post.recruitNum ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="已入职人数">
            {{ post.hasEntryNum ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘原因" :span="2">
            {{ post.reason || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="工作经验">
            <dict-tag
              v-if="post.workTime != null"
              :type="DICT_TYPE.HRM_RECRUIT_WORK_TIME"
              :value="post.workTime"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="学历要求">
            <dict-tag
              v-if="post.educationRequire != null"
              :type="DICT_TYPE.HRM_RECRUIT_POST_EDUCATION"
              :value="post.educationRequire"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="薪资范围">
            {{ formatRecruitPostSalary(post) }}
          </el-descriptions-item>
          <el-descriptions-item label="年龄要求">
            {{ formatRecruitPostAge(post) }}
          </el-descriptions-item>
          <el-descriptions-item label="最迟到岗时间">
            {{ post.latestEntryTime ? formatDate(post.latestEntryTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <dict-tag
              v-if="post.emergencyLevel != null"
              :type="DICT_TYPE.HRM_RECRUIT_EMERGENCY_LEVEL"
              :value="post.emergencyLevel"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="招聘负责人">
            {{ post.ownerEmployeeName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="职位类型">
            {{ post.postTypeName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="面试官" :span="2">
            {{ post.interviewEmployeeNames?.join('、') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag
              v-if="post.status != null"
              :type="DICT_TYPE.HRM_RECRUIT_POST_STATUS"
              :value="post.status"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item
            v-if="post.status === HrmRecruitPostStatus.STOPPED"
            label="停止原因"
            :span="2"
          >
            {{ post.stopReason || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ post.createTime ? formatDate(post.createTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="description">
        <template #title>
          <span class="text-base font-bold">职位描述</span>
        </template>
        <div class="min-h-32px whitespace-pre-wrap break-words">
          {{ post.description || '-' }}
        </div>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as RecruitPostApi from '@/api/hrm/recruit/post'
import { HrmRecruitPostStatus } from '@/views/hrm/utils/constants'
import { formatRecruitPostAge, formatRecruitPostSalary } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmRecruitPostDetailsInfo' })

defineProps<{
  post: RecruitPostApi.HrmRecruitPostVO
}>()

const activeNames = ref(['basicInfo', 'description']) // 默认展开的折叠面板
</script>
