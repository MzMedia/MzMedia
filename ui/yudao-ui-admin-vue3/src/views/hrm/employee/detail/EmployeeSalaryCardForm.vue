<template>
  <Dialog v-model="dialogVisible" title="编辑工资卡" width="640">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="92px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="银行卡号" prop="bankCardNumber">
            <el-input v-model="formData.bankCardNumber" placeholder="请输入银行卡号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开户地区" prop="bankAreaId">
            <AreaSelect v-model="formData.bankAreaId" class="!w-1/1" placeholder="请选择开户地区" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="银行名称" prop="bankName">
            <el-input v-model="formData.bankName" placeholder="请输入银行名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开户支行" prop="bankBranchName">
            <el-input v-model="formData.bankBranchName" placeholder="请输入开户支行" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as SalaryCardApi from '@/api/hrm/employee/salary-card'
import AreaSelect from '@/views/system/area/components/AreaSelect.vue'

defineOptions({ name: 'HrmEmployeeSalaryCardForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<SalaryCardApi.HrmEmployeeSalaryCardVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  bankCardNumber: [{ required: true, message: '银行卡号不能为空', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 创建默认表单数据 */
function createDefaultFormData(): SalaryCardApi.HrmEmployeeSalaryCardVO {
  return {
    id: undefined,
    employeeId: undefined,
    bankCardNumber: '',
    bankAreaId: undefined,
    bankName: '',
    bankBranchName: ''
  }
}

/** 打开弹窗 */
async function open(employeeId: number) {
  dialogVisible.value = true
  resetForm()
  formData.value.employeeId = employeeId
  formLoading.value = true
  try {
    // 获取表单数据
    const data = await SalaryCardApi.getEmployeeSalaryCard(employeeId)
    formData.value = { ...createDefaultFormData(), ...(data || {}), employeeId }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    await SalaryCardApi.saveEmployeeSalaryCard(formData.value)
    message.success('保存成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}
</script>
