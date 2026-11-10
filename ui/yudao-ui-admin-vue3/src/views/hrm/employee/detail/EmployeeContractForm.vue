<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="合同编号">
        <el-input v-model="formData.no" :maxlength="128" placeholder="请输入合同编号" />
      </el-form-item>
      <el-form-item label="合同类型" prop="type">
        <el-select
          v-model="formData.type"
          class="!w-1/1"
          clearable
          placeholder="请选择合同类型"
          @change="handleContractTypeChange"
        >
          <el-option
            v-for="item in HrmEmployeeContractTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          class="!w-1/1"
          placeholder="请选择开始日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="结束日期" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          class="!w-1/1"
          placeholder="请选择结束日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.type !== HrmEmployeeContractType.NON_FIXED_TERM_LABOR_CONTRACT"
        label="期限（年）"
        prop="term"
      >
        <el-select v-model="formData.term" class="!w-1/1" placeholder="请选择合同期限">
          <el-option
            v-for="item in HrmEmployeeContractTermOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" class="!w-1/1" clearable placeholder="请选择状态">
          <el-option
            v-for="item in HrmEmployeeContractStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="签约公司">
        <el-input v-model="formData.signCompany" :maxlength="255" placeholder="请输入签约公司" />
      </el-form-item>
      <el-form-item label="签订日期" prop="signTime">
        <el-date-picker
          v-model="formData.signTime"
          class="!w-1/1"
          placeholder="请选择签订日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="到期提醒">
        <el-switch v-model="formData.expireRemind" />
      </el-form-item>
      <el-form-item label="附件">
        <UploadFile v-model="formData.fileUrls" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          :maxlength="500"
          :rows="3"
          placeholder="请输入备注"
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="formData.sort" :min="0" class="!w-1/1" placeholder="请输入排序" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :loading="formLoading" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as ContractApi from '@/api/hrm/employee/contract'
import {
  HrmEmployeeContractStatus,
  HrmEmployeeContractStatusOptions,
  HrmEmployeeContractTermOptions,
  HrmEmployeeContractType,
  HrmEmployeeContractTypeOptions
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmEmployeeContractForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<ContractApi.HrmEmployeeContractVO & { fileUrls: string[] }>({ fileUrls: [] }) // 表单数据
const formRules = reactive<FormRules>({
  type: [{ required: true, message: '合同类型不能为空', trigger: 'change' }],
  startTime: [{ required: true, message: '开始日期不能为空', trigger: 'change' }],
  endTime: [
    { required: true, message: '结束日期不能为空', trigger: 'change' },
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.startTime != null &&
          formData.value.endTime != null &&
          Number(formData.value.endTime) < Number(formData.value.startTime)
        ) {
          callback(new Error('合同结束日期不能早于开始日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  term: [{ required: true, message: '合同期限不能为空', trigger: 'change' }],
  status: [{ required: true, message: '合同状态不能为空', trigger: 'change' }],
  signTime: [{ required: true, message: '签订日期不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改合同' : '新增合同')) // 弹窗的标题

/** 打开弹窗 */
function open(employeeId: number, row?: ContractApi.HrmEmployeeContractVO) {
  dialogVisible.value = true
  resetForm()
  // 设置表单数据
  formData.value = {
    ...formData.value,
    employeeId,
    ...row,
    fileUrls: row?.fileUrls ?? []
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

/** 处理合同类型变更 */
function handleContractTypeChange(type?: number) {
  if (type === HrmEmployeeContractType.NON_FIXED_TERM_LABOR_CONTRACT) {
    formData.value.term = undefined
    formRef.value?.clearValidate('term')
  }
}

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value || !(await formRef.value.validate())) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    if (formData.value.id) {
      await ContractApi.updateEmployeeContract(formData.value)
    } else {
      await ContractApi.createEmployeeContract(formData.value)
    }
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
  formRef.value?.resetFields()
  formData.value = {
    sort: 1,
    type: HrmEmployeeContractType.FIXED_TERM_LABOR_CONTRACT,
    term: 1,
    status: HrmEmployeeContractStatus.NOT_PERFORMED,
    expireRemind: false,
    fileUrls: []
  }
}
</script>
