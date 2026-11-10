<template>
  <template v-if="salaryEmployee.id">
    <ContentWrap>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="正式工资">
          {{ formatHrmMoney(salaryEmployee.regularSalary) }}
        </el-descriptions-item>
        <el-descriptions-item label="试用期工资">
          {{ formatHrmMoney(salaryEmployee.probationSalary) }}
        </el-descriptions-item>
        <el-descriptions-item label="生效日期">
          {{ formatHrmDate(salaryEmployee.effectTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="调整原因">
          <dict-tag
            v-if="salaryEmployee.changeReason != null"
            :type="DICT_TYPE.HRM_SALARY_CHANGE_REASON"
            :value="salaryEmployee.changeReason"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="档案状态">
          <dict-tag
            v-if="salaryEmployee.changeType != null"
            :type="DICT_TYPE.HRM_SALARY_CHANGE_TYPE"
            :value="salaryEmployee.changeType"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ salaryEmployee.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <el-row :gutter="16">
      <el-col :span="12">
        <ContentWrap title="正式工资明细">
          <el-table :data="salaryEmployee.salaryOptions || []" :stripe="true">
            <el-table-column label="薪资项" min-width="160" prop="name" />
            <el-table-column align="center" label="编码" prop="code" width="110" />
            <el-table-column align="right" label="金额" width="130">
              <template #default="scope">{{ formatHrmMoney(scope.row.value) }}</template>
            </el-table-column>
          </el-table>
        </ContentWrap>
      </el-col>
      <el-col :span="12">
        <ContentWrap title="试用期工资明细">
          <el-table :data="salaryEmployee.probationSalaryOptions || []" :stripe="true">
            <el-table-column label="薪资项" min-width="160" prop="name" />
            <el-table-column align="center" label="编码" prop="code" width="110" />
            <el-table-column align="right" label="金额" width="130">
              <template #default="scope">{{ formatHrmMoney(scope.row.value) }}</template>
            </el-table-column>
          </el-table>
        </ContentWrap>
      </el-col>
    </el-row>
  </template>

  <ContentWrap v-else>
    <el-empty description="该员工尚未定薪" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import type { SalaryEmployeeInfoVO } from '@/api/hrm/salary/employee-info'
import { formatHrmDate, formatHrmMoney } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmSalaryEmployeeInfoDetails' })

defineProps<{
  salaryEmployee: SalaryEmployeeInfoVO
}>()
</script>
