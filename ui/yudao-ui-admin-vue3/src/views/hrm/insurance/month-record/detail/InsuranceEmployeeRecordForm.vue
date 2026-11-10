<template>
  <Dialog v-model="dialogVisible" title="调整参保方案" width="960px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="86px"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="员工">
            <el-input
              :model-value="`${formData.employeeName || ''}${formData.jobNumber ? ' / ' + formData.jobNumber : ''}`"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="社保方案" prop="schemeId">
            <InsuranceSchemeSelect
              v-model="formData.schemeId"
              placeholder="请选择方案"
              @change="handleSchemeChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="状态">
            <dict-tag :type="DICT_TYPE.HRM_INSURANCE_EMP_STATUS" :value="formData.status ?? ''" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-table :data="projectList" border>
        <el-table-column label="类型" width="130">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.HRM_INSURANCE_PROJECT_TYPE" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="项目名称" min-width="150" prop="name" />
        <el-table-column
          v-if="formData.schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="缴纳基数"
          width="150"
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row.baseAmount"
              :min="0"
              :precision="2"
              :controls="false"
              class="!w-1/1"
            />
          </template>
        </el-table-column>
        <el-table-column
          v-if="formData.schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="公司比例"
          width="120"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.corporateRate) }}</template>
        </el-table-column>
        <el-table-column
          v-if="formData.schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="个人比例"
          width="120"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.personalRate) }}</template>
        </el-table-column>
        <el-table-column
          v-if="formData.schemeType === HrmInsuranceSchemeType.AMOUNT"
          label="公司金额"
          width="150"
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row.corporateAmount"
              :min="0"
              :precision="2"
              :controls="false"
              class="!w-1/1"
            />
          </template>
        </el-table-column>
        <el-table-column
          v-if="formData.schemeType === HrmInsuranceSchemeType.AMOUNT"
          label="个人金额"
          width="150"
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row.personalAmount"
              :min="0"
              :precision="2"
              :controls="false"
              class="!w-1/1"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE } from '@/utils/dict'
import * as InsuranceMonthEmployeeRecordApi from '@/api/hrm/insurance/month-record/employee'
import * as InsuranceSchemeApi from '@/api/hrm/insurance/scheme'
import { HrmInsuranceSchemeType } from '@/views/hrm/utils/constants'
import { formatHrmRate } from '@/views/hrm/utils/format'
import InsuranceSchemeSelect from '../../scheme/components/InsuranceSchemeSelect.vue'

defineOptions({ name: 'HrmInsuranceEmployeeRecordForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeRecordVO>({
  socialSecurityProjectList: [],
  providentFundProjectList: []
}) // 表单数据
const projectList = ref<InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeProjectVO[]>([]) // 参保项目列表
const formRules = reactive<FormRules>({
  schemeId: [{ required: true, message: '请选择社保方案', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(row: InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeRecordVO) {
  if (!row.id) {
    return
  }
  dialogVisible.value = true
  formLoading.value = true
  try {
    // 获取表单数据
    const detail = await InsuranceMonthEmployeeRecordApi.getInsuranceMonthEmployeeRecord(row.id)
    formData.value = { ...detail }
    projectList.value = [
      ...(detail.socialSecurityProjectList || []),
      ...(detail.providentFundProjectList || [])
    ].map((project) => ({ ...project }))
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
  if (!formData.value.id || !formData.value.schemeId) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await InsuranceMonthEmployeeRecordApi.updateInsuranceMonthEmployeeRecord({
      id: formData.value.id,
      schemeId: formData.value.schemeId,
      projects: buildProjectUpdateList()
    })
    message.success(t('common.updateSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 切换参保方案 */
async function handleSchemeChange(scheme?: InsuranceSchemeApi.InsuranceSchemeVO) {
  if (!scheme?.id) {
    projectList.value = []
    return
  }
  const detail = await InsuranceSchemeApi.getInsuranceScheme(scheme.id)
  formData.value.schemeType = detail.type
  projectList.value = (detail.projectList || []).map((project) => ({
    ...project,
    schemeProjectId: project.id
  }))
}

/** 构建参保项目修改参数 */
function buildProjectUpdateList(): InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeProjectUpdateReqVO[] {
  return projectList.value.map((project) => ({
    schemeProjectId: project.schemeProjectId!,
    ...(formData.value.schemeType === HrmInsuranceSchemeType.PROPORTION
      ? { baseAmount: project.baseAmount }
      : {
          corporateAmount: project.corporateAmount,
          personalAmount: project.personalAmount
        })
  }))
}
</script>
