<template>
  <Dialog v-model="dialogVisible" title="编辑社保资料" width="720">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="126px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="社保方案" prop="schemeId">
            <InsuranceSchemeSelect
              v-model="formData.schemeId"
              :clearable="false"
              placeholder="请选择方案"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="起缴月份" prop="socialSecurityStartMonth">
            <el-date-picker
              v-model="formData.socialSecurityStartMonth"
              class="!w-1/1"
              disabled
              type="month"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="本地首次缴纳社保" prop="firstSocialSecurity">
            <el-select v-model="formData.firstSocialSecurity" class="!w-1/1">
              <el-option
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="String(dict.value)"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="本地首次缴纳公积金" prop="firstAccumulationFund">
            <el-select v-model="formData.firstAccumulationFund" class="!w-1/1">
              <el-option
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="String(dict.value)"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="个人社保号" prop="socialSecurityNumber">
            <el-input v-model="formData.socialSecurityNumber" placeholder="请输入个人社保号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="个人公积金号" prop="accumulationFundNumber">
            <el-input v-model="formData.accumulationFundNumber" placeholder="请输入个人公积金号" />
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
import { DICT_TYPE, getBoolDictOptions } from '@/utils/dict'
import * as InsuranceEmployeeInfoApi from '@/api/hrm/insurance/employee-info'
import InsuranceSchemeSelect from '@/views/hrm/insurance/scheme/components/InsuranceSchemeSelect.vue'

defineOptions({ name: 'HrmEmployeeInsuranceInfoForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<InsuranceEmployeeInfoApi.HrmInsuranceEmployeeInfoVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  firstSocialSecurity: [
    { required: true, message: '请选择是否本地首次缴纳社保', trigger: 'change' }
  ],
  firstAccumulationFund: [
    { required: true, message: '请选择是否本地首次缴纳公积金', trigger: 'change' }
  ]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 初始化员工参保信息 */
function createDefaultFormData(): InsuranceEmployeeInfoApi.HrmInsuranceEmployeeInfoVO {
  return {
    id: undefined,
    employeeId: undefined,
    firstSocialSecurity: false,
    firstAccumulationFund: false,
    socialSecurityNumber: '',
    accumulationFundNumber: '',
    socialSecurityStartMonth: undefined,
    schemeId: undefined
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
    const data = await InsuranceEmployeeInfoApi.getInsuranceEmployeeInfo(employeeId)
    formData.value = {
      ...createDefaultFormData(),
      ...(data || {}),
      employeeId
    }
  } finally {
    formLoading.value = false
  }
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
    await InsuranceEmployeeInfoApi.saveInsuranceEmployeeInfo(formData.value)
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
