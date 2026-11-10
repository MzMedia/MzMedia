<template>
  <Dialog
    v-model="dialogVisible"
    :title="employee?.entryStatus === HrmEmployeeEntryStatus.LEFT ? '修改离职信息' : '办理离职'"
    width="680px"
  >
    <el-descriptions :column="2" border class="mb-18px">
      <el-descriptions-item label="员工姓名">{{ employee?.name || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前岗位">{{ employee?.postName || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-row :gutter="18">
        <el-col :span="12">
          <el-form-item label="计划离职时间" prop="planQuitTime">
            <el-date-picker
              v-model="formData.planQuitTime"
              class="!w-1/1"
              type="datetime"
              value-format="x"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="申请离职日期" prop="applyQuitTime">
            <el-date-picker
              v-model="formData.applyQuitTime"
              class="!w-1/1"
              type="date"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :span="12">
          <el-form-item label="离职类型" prop="type">
            <el-select v-model="formData.type" class="!w-1/1" @change="handleQuitTypeChange">
              <el-option
                v-for="item in quitTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="formData.type !== HrmEmployeeQuitType.RETIREMENT" :span="12">
          <el-form-item label="离职原因" prop="reason">
            <el-select v-model="formData.reason" class="!w-1/1" clearable>
              <el-option
                v-for="item in filteredQuitReasonOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="薪资结算日期" prop="salarySettlementTime">
        <el-date-picker
          v-model="formData.salarySettlementTime"
          class="!w-240px"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          :rows="3"
          maxlength="500"
          show-word-limit
          type="textarea"
        />
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
import dayjs from 'dayjs'
import * as EmployeeApi from '@/api/hrm/employee'
import type { HrmEmployeeVO } from '@/api/hrm/employee'
import * as QuitInfoApi from '@/api/hrm/employee/quit-info'
import {
  HrmEmployeeQuitReason,
  HrmEmployeeQuitReasonOptions,
  HrmEmployeeQuitType,
  HrmEmployeeQuitTypeOptions,
  HrmEmployeeEntryStatus
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmEmployeeQuitForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref<EmployeeApi.HrmEmployeeQuitReqVO>({}) // 表单数据
const formRules: FormRules = {
  planQuitTime: [
    { required: true, message: '请选择计划离职时间', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (
          value &&
          formData.value.applyQuitTime &&
          dayjs(value).isBefore(dayjs(formData.value.applyQuitTime), 'day')
        ) {
          callback(new Error('计划离职日期不能早于申请离职日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  applyQuitTime: [{ required: true, message: '请选择申请离职日期', trigger: 'change' }],
  salarySettlementTime: [
    { required: true, message: '请选择薪资结算日期', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (
          value &&
          formData.value.planQuitTime &&
          dayjs(value).isBefore(dayjs(formData.value.planQuitTime), 'day')
        ) {
          callback(new Error('薪资结算日期不能早于计划离职日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  type: [{ required: true, message: '请选择离职类型', trigger: 'change' }],
  reason: [
    {
      validator: (_rule, value, callback) => {
        if (formData.value.type !== HrmEmployeeQuitType.RETIREMENT && !value) {
          callback(new Error('请选择离职原因'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ]
} // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const employee = ref<HrmEmployeeVO>() // 员工信息

const quitTypeOptions = HrmEmployeeQuitTypeOptions // 离职类型选项
const quitReasonOptions = HrmEmployeeQuitReasonOptions // 离职原因选项
const filteredQuitReasonOptions = computed(() =>
  quitReasonOptions.filter((item) => item.quitType === formData.value.type)
)

/** 打开弹窗 */
async function open(row: HrmEmployeeVO) {
  dialogVisible.value = true
  employee.value = row
  resetForm(row.id)
  if (row.id) {
    formLoading.value = true
    try {
      // 获取表单数据
      const quitInfo = await QuitInfoApi.getEmployeeQuitInfo(row.id)
      if (quitInfo) {
        formData.value = {
          ...formData.value,
          ...quitInfo,
          employeeId: row.id
        }
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 切换离职类型 */
function handleQuitTypeChange(quitType?: number) {
  formData.value.reason = quitReasonOptions.find((item) => item.quitType === quitType)?.value
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
    await EmployeeApi.quitEmployee(formData.value)
    message.success('离职信息保存成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(employeeId?: number) {
  formData.value = {
    employeeId,
    applyQuitTime: dayjs().startOf('day').valueOf(),
    type: HrmEmployeeQuitType.VOLUNTARY,
    reason: HrmEmployeeQuitReason.FAMILY
  }
  formRef.value?.resetFields()
}
</script>
