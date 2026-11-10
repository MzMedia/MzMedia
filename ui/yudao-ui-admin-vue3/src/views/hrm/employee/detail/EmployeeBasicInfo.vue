<template>
  <EmployeeDetailsInfo :employee="employee" />
  <ContentWrap title="教育经历">
    <EmployeeEducationExperienceList :employee-id="employeeId" />
  </ContentWrap>
  <ContentWrap title="工作经历">
    <EmployeeWorkExperienceList :employee-id="employeeId" />
  </ContentWrap>
  <ContentWrap title="证书/证件">
    <EmployeeCertificateList :employee-id="employeeId" />
  </ContentWrap>
  <ContentWrap title="培训经历">
    <EmployeeTrainingExperienceList :employee-id="employeeId" />
  </ContentWrap>
  <ContentWrap title="联系人">
    <EmployeeContactList :employee-id="employeeId" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { HrmEmployeeVO } from '@/api/hrm/employee'
import EmployeeCertificateList from './EmployeeCertificateList.vue'
import EmployeeContactList from './EmployeeContactList.vue'
import EmployeeDetailsInfo from './EmployeeDetailsInfo.vue'
import EmployeeEducationExperienceList from './EmployeeEducationExperienceList.vue'
import EmployeeTrainingExperienceList from './EmployeeTrainingExperienceList.vue'
import EmployeeWorkExperienceList from './EmployeeWorkExperienceList.vue'

defineOptions({ name: 'HrmEmployeeBasicInfo' })

defineProps<{
  employee: HrmEmployeeVO
  employeeId: number
}>()
</script>
