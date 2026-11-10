<template>
  <div v-loading="loading">
    <ContentWrap title="岗位信息">
      <el-descriptions :column="4" border>
        <el-descriptions-item label="工号">{{ employee.jobNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ employee.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位">{{ employee.postName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位职级">{{
          employee.postLevel || '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="直属上级">
          {{ employee.leaderEmployeeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="入职状态">
          <dict-tag
            v-if="employee.entryStatus != null"
            :type="DICT_TYPE.HRM_EMPLOYEE_ENTRY_STATUS"
            :value="employee.entryStatus"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="员工状态">
          <dict-tag
            v-if="employee.status != null"
            :type="DICT_TYPE.HRM_EMPLOYEE_STATUS"
            :value="employee.status"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="聘用形式">
          <dict-tag
            v-if="employee.type != null"
            :type="DICT_TYPE.HRM_EMPLOYEE_TYPE"
            :value="employee.type"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="入职时间">
          {{ formatHrmDateTime(employee.entryTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="转正时间">
          {{ formatHrmDateTime(employee.regularTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="试用期">
          {{ employee.probation != null ? `${employee.probation} 个月` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="司龄">
          {{ employee.companyAge != null ? `${employee.companyAge} 年` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="工作城市">{{ employee.workCity || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作地点">
          {{ employee.workAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">
          {{ employee.workDetailAddress || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <ContentWrap v-if="quitInfo" title="离职信息">
      <el-descriptions :column="4" border>
        <el-descriptions-item label="计划离职时间">
          {{ formatHrmDateTime(quitInfo.planQuitTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="申请离职日期">
          {{ formatHrmDate(quitInfo.applyQuitTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="薪资结算日期">
          {{ formatHrmDate(quitInfo.salarySettlementTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="离职类型">
          {{ formatEmployeeQuitType(quitInfo.type) }}
        </el-descriptions-item>
        <el-descriptions-item label="离职原因">
          {{ formatEmployeeQuitReason(quitInfo.reason) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">
          {{ quitInfo.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import type { HrmEmployeeQuitInfoVO } from '@/api/hrm/employee/quit-info'
import type { HrmPortalEmployeeVO } from '@/api/hrm/portal/employee'
import * as QuitInfoApi from '@/api/hrm/portal/employee/quit-info'
import { DICT_TYPE } from '@/utils/dict'
import {
  formatEmployeeQuitReason,
  formatEmployeeQuitType,
  formatHrmDate,
  formatHrmDateTime
} from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalEmployeePostInfo' })

defineProps<{
  employee: HrmPortalEmployeeVO
}>()

const loading = ref(false) // 离职信息加载中
const quitInfo = ref<HrmEmployeeQuitInfoVO>() // 离职信息

/** 获得当前员工离职信息 */
async function getQuitInfo() {
  loading.value = true
  try {
    quitInfo.value = await QuitInfoApi.getEmployeeQuitInfo()
  } finally {
    loading.value = false
  }
}

defineExpose({ getQuitInfo }) // 提供 getQuitInfo 方法，用于刷新离职信息

/** 初始化 */
onMounted(() => {
  getQuitInfo()
})
</script>
