<template>
  <Dialog v-model="dialogVisible" title="设置参保方案" width="520">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="92px"
    >
      <el-form-item label="员工数量"> {{ employeeIds.length }} 人 </el-form-item>
      <el-form-item label="社保方案" prop="schemeId">
        <InsuranceSchemeSelect v-model="formData.schemeId" placeholder="请选择社保方案" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as InsuranceEmployeeInfoApi from '@/api/hrm/insurance/employee-info'
import InsuranceSchemeSelect from '@/views/hrm/insurance/scheme/components/InsuranceSchemeSelect.vue'
import { useBatchOperation } from '@/views/hrm/utils/batch'

defineOptions({ name: 'HrmEmployeeInsuranceSchemeForm' })

const { executeBatch } = useBatchOperation() // 批量操作

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const employeeIds = ref<number[]>([]) // 员工编号
const formData = ref({
  schemeId: undefined as number | undefined
}) // 表单数据
const formRules = reactive<FormRules>({
  schemeId: [{ required: true, message: '社保方案不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(ids: number[]) {
  dialogVisible.value = true
  employeeIds.value = [...ids]
  formData.value.schemeId = undefined
  formRef.value?.resetFields()
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits<{ success: [] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) {
    return
  }
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const schemeId = formData.value.schemeId as number
    const success = await executeBatch(
      employeeIds.value.map((employeeId) =>
        InsuranceEmployeeInfoApi.updateEmployeeScheme(employeeId, schemeId)
      )
    )
    if (success) {
      dialogVisible.value = false
      // 发送操作成功的事件
      emit('success')
    }
  } finally {
    formLoading.value = false
  }
}
</script>
