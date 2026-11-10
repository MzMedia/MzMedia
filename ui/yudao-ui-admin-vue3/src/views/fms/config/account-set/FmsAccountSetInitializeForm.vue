<template>
  <Dialog v-model="dialogVisible" title="开始记账" width="640">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="104px"
    >
      <el-form-item label="公司名称">
        <el-input :model-value="accountSet?.companyName" disabled />
      </el-form-item>
      <el-form-item label="本位币" prop="currencyCode">
        <el-select v-model="formData.currencyCode" disabled class="!w-1/1">
          <el-option
            v-for="item in FMS_CURRENCY_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="启用期间" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          class="!w-1/1"
          placeholder="请选择启用期间"
          type="month"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="会计制度" prop="standard">
        <el-select v-model="formData.standard" class="!w-1/1" placeholder="请选择会计制度">
          <el-option
            v-for="item in FMS_ACCOUNTING_STANDARD_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="科目级次" prop="level">
        <el-select v-model="formData.level" class="!w-1/1" placeholder="请选择科目级次">
          <el-option v-for="level in 8" :key="level" :label="`${level} 级`" :value="level" />
        </el-select>
      </el-form-item>
      <el-form-item label="科目编码规则" prop="subjectCodeRule">
        <el-input v-model="formData.subjectCodeRule" placeholder="例如：4-2-2-2" />
      </el-form-item>
      <el-form-item label="余额方向" prop="ledgerBalanceMode">
        <el-select
          v-model="formData.ledgerBalanceMode"
          class="!w-1/1"
          placeholder="请选择账簿余额方向"
        >
          <el-option
            v-for="item in FMS_LEDGER_BALANCE_MODE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-alert
        :closable="false"
        title="初始化后将建立本位币、财务参数和默认凭证字，启用期间不可随意变更"
        type="info"
        show-icon
      />
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">开始记账</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { FmsAccountSetApi } from '@/api/fms/config/account-set'
import type { FmsAccountSetInitializeReqVO, FmsAccountSetVO } from '@/api/fms/config/account-set'
import type { FormInstance, FormRules } from 'element-plus'
import {
  FMS_ACCOUNTING_STANDARD_OPTIONS,
  FMS_CURRENCY_CODE,
  FMS_CURRENCY_OPTIONS,
  FMS_DEFAULT_SUBJECT_CODE_RULE,
  FMS_DEFAULT_SUBJECT_LEVEL,
  FMS_LEDGER_BALANCE_MODE,
  FMS_LEDGER_BALANCE_MODE_OPTIONS
} from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsAccountSetInitializeForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单提交的加载中
const accountSet = ref<FmsAccountSetVO>() // 当前账套
const formData = ref<FmsAccountSetInitializeReqVO>(createEmptyFormData()) // 表单数据
const formRules = reactive<FormRules>({
  currencyCode: [{ required: true, message: '本位币不能为空', trigger: 'change' }],
  startTime: [{ required: true, message: '启用期间不能为空', trigger: 'change' }],
  standard: [{ required: true, message: '会计制度不能为空', trigger: 'change' }],
  level: [{ required: true, message: '科目级次不能为空', trigger: 'change' }],
  subjectCodeRule: [
    { required: true, message: '科目编码规则不能为空', trigger: 'blur' },
    { pattern: /^([2-5]-)*[2-5]$/, message: '各级编码长度必须为 2 至 5 位', trigger: 'blur' }
  ],
  ledgerBalanceMode: [{ required: true, message: '余额方向不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(row: FmsAccountSetVO) {
  accountSet.value = row
  formData.value = createEmptyFormData(row.id)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调
async function submitForm() {
  // 校验表单
  if (!formRef.value || !accountSet.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    await FmsAccountSetApi.initializeAccountSet(formData.value)
    message.success('账套初始化成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 创建空表单数据 */
function createEmptyFormData(accountSetId = 0): FmsAccountSetInitializeReqVO {
  return {
    accountSetId,
    currencyCode: FMS_CURRENCY_CODE.RMB,
    startTime: dayjs().startOf('month').valueOf(),
    standard: FMS_ACCOUNTING_STANDARD_OPTIONS[0].value,
    level: FMS_DEFAULT_SUBJECT_LEVEL,
    subjectCodeRule: FMS_DEFAULT_SUBJECT_CODE_RULE,
    ledgerBalanceMode: FMS_LEDGER_BALANCE_MODE.SAME_AS_SUBJECT
  }
}
</script>
