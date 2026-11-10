<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="860">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="104px"
    >
      <!-- 基本信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="薪资组" prop="name">
            <el-input v-model="formData.name" maxlength="64" placeholder="请输入薪资组名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计税规则" prop="taxRuleId">
            <SalaryTaxRuleSelect v-model="formData.taxRuleId" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 适用范围 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计薪标准">
            <span>21.75 天 / 月</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="调薪规则">
            <span>按转正、调薪生效日前后的工资混合计算</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="部门范围" prop="deptIds">
            <DeptSelect
              v-model="formData.deptIds"
              multiple
              placeholder="请选择部门"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="员工范围" prop="employeeIds">
            <HrmEmployeeSelect
              v-model="formData.employeeIds"
              class="!w-1/1"
              multiple
              placeholder="请选择员工"
              title="选择薪资组员工"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as SalaryGroupApi from '@/api/hrm/salary/config/group'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import SalaryTaxRuleSelect from '../tax-rule/components/SalaryTaxRuleSelect.vue'

defineOptions({ name: 'HrmSalaryGroupForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗是否展示
const dialogTitle = ref('') // 弹窗标题
const formLoading = ref(false) // 表单加载中
const formType = ref('') // 表单类型：create - 新增；update - 修改
const formData = ref<SalaryGroupApi.SalaryGroupVO>({
  id: undefined,
  name: '',
  taxRuleId: undefined,
  deptIds: [],
  employeeIds: []
})
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '薪资组名称不能为空', trigger: 'blur' }],
  taxRuleId: [{ required: true, message: '计税规则不能为空', trigger: 'change' }],
  employeeIds: [{ validator: validateSalaryGroupScope, trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 校验薪资组适用范围 */
function validateSalaryGroupScope(
  _rule: unknown,
  _value: number[],
  callback: (error?: Error) => void
) {
  if (formData.value.deptIds?.length || formData.value.employeeIds?.length) {
    callback()
    return
  }
  callback(new Error('适用部门和适用员工不能同时为空'))
}

/** 打开表单 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      // 获取表单数据
      formData.value = await SalaryGroupApi.getSalaryGroup(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await SalaryGroupApi.createSalaryGroup(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await SalaryGroupApi.updateSalaryGroup(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    name: '',
    taxRuleId: undefined,
    deptIds: [],
    employeeIds: []
  }
  formRef.value?.resetFields()
}
</script>
