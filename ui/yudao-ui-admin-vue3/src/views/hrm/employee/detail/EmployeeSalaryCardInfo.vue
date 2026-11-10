<template>
  <ContentWrap v-loading="loading" title="工资卡">
    <template #header>
      <el-button
        v-hasPermi="['hrm:employee:update']"
        class="ml-auto"
        type="primary"
        plain
        @click="openForm"
      >
        <Icon icon="ep:edit" class="mr-5px" />编辑
      </el-button>
      <el-button
        v-if="salaryCard?.id"
        v-hasPermi="['hrm:employee:update']"
        type="danger"
        plain
        @click="handleDelete"
      >
        <Icon icon="ep:delete" class="mr-5px" />删除
      </el-button>
    </template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="银行卡号">
        {{ salaryCard?.bankCardNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="开户地区">
        {{ salaryCard?.bankAreaName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="银行名称">
        {{ salaryCard?.bankName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="开户支行" :span="3">
        {{ salaryCard?.bankBranchName || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>

  <!-- 表单弹窗：编辑工资卡 -->
  <EmployeeSalaryCardForm ref="formRef" @success="getSalaryCard" />
</template>

<script lang="ts" setup>
import * as SalaryCardApi from '@/api/hrm/employee/salary-card'
import EmployeeSalaryCardForm from './EmployeeSalaryCardForm.vue'

defineOptions({ name: 'HrmEmployeeSalaryCardInfo' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗

const loading = ref(true) // 工资卡的加载中
const salaryCard = ref<SalaryCardApi.HrmEmployeeSalaryCardVO>() // 工资卡

/** 获取工资卡 */
async function getSalaryCard() {
  loading.value = true
  try {
    salaryCard.value = await SalaryCardApi.getEmployeeSalaryCard(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeSalaryCardForm>>() // 工资卡表单 Ref

/** 编辑工资卡 */
function openForm() {
  formRef.value?.open(props.employeeId)
}

/** 删除工资卡 */
async function handleDelete() {
  try {
    // 删除的二次确认
    await message.delConfirm('确定删除当前员工的工资卡信息吗？')
    // 发起删除
    await SalaryCardApi.deleteEmployeeSalaryCard(props.employeeId)
    message.success('工资卡删除成功')
    await getSalaryCard()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getSalaryCard()
})
</script>
