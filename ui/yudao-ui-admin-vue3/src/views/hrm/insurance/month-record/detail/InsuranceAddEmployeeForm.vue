<template>
  <Dialog v-model="dialogVisible" title="添加参保人员" width="560">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="76px"
    >
      <el-form-item label="员工" prop="employeeIds">
        <HrmEmployeeSelect
          v-model="formData.employeeIds"
          multiple
          placeholder="请选择员工"
          :selectable="isEmployeeSelectable"
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
import * as InsuranceMonthEmployeeRecordApi from '@/api/hrm/insurance/month-record/employee'
import * as EmployeeApi from '@/api/hrm/employee'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'

defineOptions({ name: 'HrmInsuranceAddEmployeeForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const monthRecordId = ref<number>() // 月度社保表编号
const formData = ref({ employeeIds: [] as number[] }) // 表单数据
const selectableEmployeeIds = ref<Set<number>>(new Set()) // 本月可添加的员工编号
const formRules = reactive<FormRules>({
  employeeIds: [{ required: true, message: '请选择员工', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(recordId: number) {
  dialogVisible.value = true
  monthRecordId.value = recordId
  formData.value.employeeIds = []
  formLoading.value = true
  try {
    // 获取可添加员工
    const employeeList = await InsuranceMonthEmployeeRecordApi.getUninsuredEmployeeList(recordId)
    selectableEmployeeIds.value = new Set(
      employeeList.map((employee) => employee.id).filter((id): id is number => id !== undefined)
    )
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) {
    return
  }
  // 校验表单
  await formRef.value.validate()
  if (!monthRecordId.value) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await InsuranceMonthEmployeeRecordApi.createInsuranceMonthEmployeeRecordList({
      monthRecordId: monthRecordId.value,
      employeeIds: formData.value.employeeIds
    })
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 员工是否可添加到本月社保表 */
function isEmployeeSelectable(employee: EmployeeApi.HrmEmployeeVO) {
  return employee.id !== undefined && selectableEmployeeIds.value.has(employee.id)
}
</script>
