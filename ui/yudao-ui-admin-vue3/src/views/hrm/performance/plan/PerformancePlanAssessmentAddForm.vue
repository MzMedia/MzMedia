<template>
  <Dialog v-model="dialogVisible" title="添加参评员工" width="620px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="参评员工" prop="employeeIds">
        <HrmEmployeeSelect
          v-model="formData.employeeIds"
          multiple
          :selectable="isEmployeeSelectable"
          placeholder="请选择未加入当前计划的员工"
          title="选择参评员工"
        />
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
import type { HrmEmployeeVO } from '@/api/hrm/employee'
import * as PerformanceAssessmentApi from '@/api/hrm/performance/assessment'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'

defineOptions({ name: 'HrmPerformancePlanAssessmentAddForm' })

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否展示
const formLoading = ref(false) // 表单加载中
const formRef = ref<FormInstance>() // 表单 Ref
const planId = ref<number>() // 绩效计划编号
const selectableEmployeeIds = ref(new Set<number>()) // 可选员工编号
const formData = ref({
  employeeIds: [] as number[]
})
const formRules: FormRules = {
  employeeIds: [{ required: true, message: '请选择参评员工', trigger: 'change' }]
}

/** 打开弹窗 */
async function open(id: number) {
  dialogVisible.value = true
  formLoading.value = true
  planId.value = id
  formData.value.employeeIds = []
  formRef.value?.resetFields()
  try {
    // 获取可添加员工编号
    selectableEmployeeIds.value = new Set(
      await PerformanceAssessmentApi.getPerformancePlanUnassignedEmployeeIdList(id)
    )
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

/** 判断员工是否允许加入当前计划 */
function isEmployeeSelectable(employee: HrmEmployeeVO) {
  return !!employee.id && selectableEmployeeIds.value.has(employee.id)
}

const emit = defineEmits(['success']) // 定义组件事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  if (!planId.value) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await PerformanceAssessmentApi.addPerformancePlanEmployees({
      planId: planId.value,
      employeeIds: formData.value.employeeIds
    })
    message.success('参评员工添加成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
