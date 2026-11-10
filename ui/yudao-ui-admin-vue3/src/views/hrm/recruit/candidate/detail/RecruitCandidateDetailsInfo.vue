<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="candidateInfo">
        <template #title><span class="text-base font-bold">候选人信息</span></template>
        <el-descriptions :column="4">
          <el-descriptions-item label="候选人姓名">
            {{ candidate.name || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号码">
            {{ candidate.mobile || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag
              v-if="candidate.sex != null"
              :type="DICT_TYPE.SYSTEM_USER_SEX"
              :value="candidate.sex"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="年龄">{{ candidate.age ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ candidate.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工作年限">
            {{ candidate.workTime != null ? `${candidate.workTime} 年` : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="学历">
            <dict-tag
              v-if="candidate.education != null"
              :type="DICT_TYPE.HRM_RECRUIT_CANDIDATE_EDUCATION"
              :value="candidate.education"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="毕业院校">
            {{ candidate.graduateSchool || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="最近工作单位">
            {{ candidate.latestWorkPlace || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="4">
            {{ candidate.remark || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="recruitInfo">
        <template #title><span class="text-base font-bold">招聘信息</span></template>
        <el-descriptions :column="4">
          <el-descriptions-item label="应聘职位">
            {{ candidate.postName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="用人部门">
            {{ candidate.deptName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘负责人">
            {{ candidate.ownerEmployeeName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘渠道">
            {{ candidate.channelName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="候选人状态">
            <dict-tag
              v-if="candidate.status != null"
              :type="DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS"
              :value="candidate.status"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="面试轮次">
            {{ candidate.stageNumber ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="状态更新时间">
            {{ candidate.statusUpdateTime ? formatDate(candidate.statusUpdateTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="入职时间">
            {{ candidate.entryTime ? formatDate(candidate.entryTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="淘汰原因" :span="2">
            {{ candidate.eliminate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建人">
            {{ candidate.creatorName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ candidate.createTime ? formatDate(candidate.createTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'

defineOptions({ name: 'HrmRecruitCandidateDetailsInfo' })

defineProps<{
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
}>()

const activeNames = ref(['candidateInfo', 'recruitInfo']) // 默认展开的折叠面板
</script>
