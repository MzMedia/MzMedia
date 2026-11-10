<template>
  <ContentWrap v-loading="loading" title="社保资料">
    <template #header>
      <el-button
        v-hasPermi="['hrm:insurance:employee-info:update']"
        class="ml-auto"
        type="primary"
        plain
        @click="openForm"
      >
        <Icon icon="ep:edit" class="mr-5px" />编辑
      </el-button>
    </template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="社保编号">
        {{ insuranceInfo?.socialSecurityNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="公积金编号">
        {{ insuranceInfo?.accumulationFundNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="社保起始月">
        {{ formatHrmMonth(insuranceInfo?.socialSecurityStartMonth) }}
      </el-descriptions-item>
      <el-descriptions-item label="参保方案">
        {{ insuranceInfo?.schemeName || insuranceInfo?.schemeId || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="本地首次缴纳社保">
        {{ formatHrmYesNo(insuranceInfo?.firstSocialSecurity) }}
      </el-descriptions-item>
      <el-descriptions-item label="本地首次缴纳公积金">
        {{ formatHrmYesNo(insuranceInfo?.firstAccumulationFund) }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>

  <!-- 表单弹窗：编辑社保资料 -->
  <EmployeeInsuranceInfoForm ref="formRef" @success="getInsuranceInfo" />
</template>

<script lang="ts" setup>
import * as InsuranceEmployeeInfoApi from '@/api/hrm/insurance/employee-info'
import { formatHrmMonth, formatHrmYesNo } from '@/views/hrm/utils/format'
import EmployeeInsuranceInfoForm from './EmployeeInsuranceInfoForm.vue'

defineOptions({ name: 'HrmEmployeeInsuranceInfo' })

const props = defineProps<{
  employeeId: number
}>()

const loading = ref(true) // 社保资料的加载中
const insuranceInfo = ref<InsuranceEmployeeInfoApi.HrmInsuranceEmployeeInfoVO>() // 社保资料

/** 获取社保资料 */
async function getInsuranceInfo() {
  loading.value = true
  try {
    insuranceInfo.value = await InsuranceEmployeeInfoApi.getInsuranceEmployeeInfo(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeInsuranceInfoForm>>() // 社保资料表单 Ref

/** 编辑社保资料 */
function openForm() {
  formRef.value?.open(props.employeeId)
}

/** 初始化 */
onMounted(() => {
  getInsuranceInfo()
})
</script>
