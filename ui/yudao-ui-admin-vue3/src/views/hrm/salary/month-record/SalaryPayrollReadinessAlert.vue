<template>
  <el-alert v-if="readiness?.noSalaryGroupEmployeeCount" class="!mt-8px" show-icon type="warning">
    <template #title>
      有 {{ readiness.noSalaryGroupEmployeeCount }} 名员工未加入任何薪资组，无法参与工资核算。
      <el-button link type="primary" @click="noSalaryGroupDialogVisible = true">查看员工</el-button>
    </template>
  </el-alert>
  <el-alert v-if="readiness?.noSalaryEmployeeCount" class="!mt-8px" show-icon type="warning">
    <template #title>
      有
      {{ readiness.noSalaryEmployeeCount }}
      名员工没有生效薪资档案，将优先继承上月工资；无上月工资时按 0 核算。
      <el-button link type="primary" @click="noSalaryDialogVisible = true">查看员工</el-button>
    </template>
  </el-alert>

  <!-- 未加入薪资组员工 -->
  <Dialog v-model="noSalaryGroupDialogVisible" title="未加入薪资组的员工" width="860">
    <SalaryPayrollReadinessEmployeeList :list="readiness?.noSalaryGroupEmployees" />
  </Dialog>

  <!-- 未设置薪资档案员工 -->
  <Dialog v-model="noSalaryDialogVisible" title="未设置薪资档案的员工" width="860">
    <SalaryPayrollReadinessEmployeeList :list="readiness?.noSalaryEmployees" />
  </Dialog>
</template>

<script lang="ts" setup>
import * as SalaryMonthRecordApi from '@/api/hrm/salary/month-record'
import SalaryPayrollReadinessEmployeeList from './SalaryPayrollReadinessEmployeeList.vue'

defineOptions({ name: 'HrmSalaryPayrollReadinessAlert' })

const props = defineProps<{
  monthRecordId?: number
}>()

const readiness = ref<SalaryMonthRecordApi.SalaryPayrollReadinessVO>() // 核算准备
const noSalaryGroupDialogVisible = ref(false) // 未加入薪资组员工弹窗
const noSalaryDialogVisible = ref(false) // 未设置薪资档案员工弹窗

/** 刷新薪资核算准备状态 */
async function refresh() {
  if (!props.monthRecordId) {
    readiness.value = undefined
    return
  }
  readiness.value = await SalaryMonthRecordApi.getSalaryPayrollReadiness(props.monthRecordId)
}
defineExpose({ refresh }) // 提供 refresh 方法，用于刷新核算准备状态
</script>
