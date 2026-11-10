<template>
  <EmployeeSalaryInfo
    v-if="checkPermi(['hrm:salary:employee-info:query'])"
    :employee-id="employeeId"
  />
  <EmployeeSalaryCardInfo :employee-id="employeeId" />
  <EmployeeInsuranceInfo
    v-if="checkPermi(['hrm:insurance:employee-info:query'])"
    :employee-id="employeeId"
  />
  <EmployeeSalaryHistoryList
    v-if="checkPermi(['hrm:salary:month-record:query'])"
    :employee-id="employeeId"
  />
  <EmployeeSalaryChangeRecordList :employee-id="employeeId" />
</template>

<script lang="ts" setup>
import { checkPermi } from '@/utils/permission'
import EmployeeSalaryInfo from './EmployeeSalaryInfo.vue'
import EmployeeSalaryCardInfo from './EmployeeSalaryCardInfo.vue'
import EmployeeSalaryChangeRecordList from './EmployeeSalaryChangeRecordList.vue'
import EmployeeSalaryHistoryList from './EmployeeSalaryHistoryList.vue'
import EmployeeInsuranceInfo from './EmployeeInsuranceInfo.vue'

defineOptions({ name: 'HrmEmployeeSalarySocialSecurity' })

defineProps<{
  employeeId: number
}>()
</script>
