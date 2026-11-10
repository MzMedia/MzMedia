<template>
  <ContentWrap title="面试记录">
    <!-- 列表 -->
    <el-table :data="interviewList" :show-overflow-tooltip="true" stripe>
      <el-table-column align="center" label="面试轮次" prop="stageNumber" width="100" />
      <el-table-column align="center" label="面试方式" prop="type" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_INTERVIEW_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="主面试官"
        min-width="120"
        prop="interviewEmployeeName"
      />
      <el-table-column align="center" label="其他面试官" min-width="160">
        <template #default="scope">
          {{ scope.row.otherInterviewEmployeeNames?.join('、') || '-' }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="面试时间"
        prop="interviewTime"
        width="180"
      />
      <el-table-column align="center" label="面试地址" min-width="160" prop="address" />
      <el-table-column align="center" label="面试结果" prop="result" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_RECRUIT_INTERVIEW_RESULT" :value="scope.row.result" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="面试评价" min-width="180" prop="evaluate" />
      <el-table-column align="center" label="取消原因" min-width="160" prop="cancelReason" />
      <el-table-column align="center" label="备注" min-width="160" prop="remark" />
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as RecruitInterviewApi from '@/api/hrm/recruit/interview'

defineOptions({ name: 'HrmRecruitCandidateInterviewList' })

defineProps<{
  interviewList: RecruitInterviewApi.HrmRecruitInterviewVO[]
}>()
</script>
