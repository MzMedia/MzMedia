<template>
  <ContentWrap v-loading="loading" title="当前薪资档案">
    <el-descriptions v-if="salaryInfo" :column="3" border>
      <el-descriptions-item label="转正工资">
        {{ formatHrmMoney(salaryInfo.regularSalary) }}
      </el-descriptions-item>
      <el-descriptions-item label="试用工资">
        {{ formatHrmMoney(salaryInfo.probationSalary) }}
      </el-descriptions-item>
      <el-descriptions-item label="调整日期">
        {{ formatHrmDate(salaryInfo.effectTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="调整类型">
        <dict-tag
          v-if="salaryInfo.changeType != null"
          :type="DICT_TYPE.HRM_SALARY_CHANGE_TYPE"
          :value="salaryInfo.changeType"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="调整原因">
        <dict-tag
          v-if="salaryInfo.changeReason != null"
          :type="DICT_TYPE.HRM_SALARY_CHANGE_REASON"
          :value="salaryInfo.changeReason"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="备注">{{ salaryInfo.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-empty v-else description="暂无薪资档案" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as SalaryEmployeeInfoApi from '@/api/hrm/salary/employee-info'
import { formatHrmDate, formatHrmMoney } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmEmployeeSalaryInfo' })

const props = defineProps<{
  employeeId: number
}>()

const loading = ref(true) // 薪资档案的加载中
const salaryInfo = ref<SalaryEmployeeInfoApi.SalaryEmployeeInfoVO>() // 薪资档案

/** 获取薪资档案 */
async function getSalaryInfo() {
  loading.value = true
  try {
    salaryInfo.value = await SalaryEmployeeInfoApi.getSalaryEmployeeInfo(props.employeeId)
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getSalaryInfo()
})
</script>
