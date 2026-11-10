<template>
  <Dialog v-model="dialogVisible" title="批量调整参保方案" width="960px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="86px"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="已选员工">
            <el-input :model-value="`${formData.ids.length} 人`" disabled />
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
      </el-row>
      <el-table :data="projectList" border>
        <el-table-column label="类型" width="130">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.HRM_INSURANCE_PROJECT_TYPE" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="项目名称" min-width="150" prop="name" />
        <el-table-column
          v-if="schemeType === HrmInsuranceSchemeType.PROPORTION"
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
          v-if="schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="公司比例"
          width="120"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.corporateRate) }}</template>
        </el-table-column>
        <el-table-column
          v-if="schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="个人比例"
          width="120"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.personalRate) }}</template>
        </el-table-column>
        <el-table-column
          v-if="schemeType === HrmInsuranceSchemeType.AMOUNT"
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
          v-if="schemeType === HrmInsuranceSchemeType.AMOUNT"
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
import { useBatchOperation } from '@/views/hrm/utils/batch'
import InsuranceSchemeSelect from '../../scheme/components/InsuranceSchemeSelect.vue'

defineOptions({ name: 'HrmInsuranceBatchEmployeeRecordForm' })

const { executeBatch } = useBatchOperation() // 批量操作

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({ ids: [] as number[], schemeId: undefined as number | undefined }) // 表单数据
const schemeType = ref<number>() // 参保方案类型
const projectList = ref<InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeProjectVO[]>([]) // 参保项目列表
const formRules = reactive<FormRules>({
  schemeId: [{ required: true, message: '请选择社保方案', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(ids: number[]) {
  if (!ids.length) {
    return
  }
  dialogVisible.value = true
  formData.value = { ids: [...ids], schemeId: undefined }
  schemeType.value = undefined
  projectList.value = []
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
  if (!formData.value.schemeId) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    const schemeId = formData.value.schemeId
    const projects = buildProjectUpdateList()
    const hasSuccess = await executeBatch(
      formData.value.ids.map((id) =>
        InsuranceMonthEmployeeRecordApi.updateInsuranceMonthEmployeeRecord({
          id,
          schemeId,
          projects
        })
      )
    )
    if (hasSuccess) {
      dialogVisible.value = false
      // 发送操作成功的事件
      emit('success')
    }
  } finally {
    formLoading.value = false
  }
}

/** 切换参保方案 */
async function handleSchemeChange(scheme?: InsuranceSchemeApi.InsuranceSchemeVO) {
  if (!scheme?.id) {
    projectList.value = []
    schemeType.value = undefined
    return
  }
  const detail = await InsuranceSchemeApi.getInsuranceScheme(scheme.id)
  schemeType.value = detail.type
  projectList.value = (detail.projectList || []).map((project) => ({
    ...project,
    schemeProjectId: project.id
  }))
}

/** 构建参保项目修改参数 */
function buildProjectUpdateList(): InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeProjectUpdateReqVO[] {
  return projectList.value.map((project) => ({
    schemeProjectId: project.schemeProjectId!,
    ...(schemeType.value === HrmInsuranceSchemeType.PROPORTION
      ? { baseAmount: project.baseAmount }
      : {
          corporateAmount: project.corporateAmount,
          personalAmount: project.personalAmount
        })
  }))
}
</script>
