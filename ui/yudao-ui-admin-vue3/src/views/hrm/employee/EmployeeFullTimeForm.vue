<template>
  <Dialog v-model="dialogVisible" title="转为全职" width="760px">
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
          <el-form-item label="异动原因" prop="reason">
            <el-select v-model="formData.reason" class="!w-1/1">
              <el-option
                v-for="item in changeReasonOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生效日期" prop="effectTime">
            <el-date-picker
              v-model="formData.effectTime"
              class="!w-1/1"
              type="date"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="试用期（月）" prop="probation">
        <el-input-number v-model="formData.probation" :min="0" :max="6" class="!w-240px" />
      </el-form-item>
      <el-row :gutter="18">
        <el-col :span="12">
          <el-form-item label="转全职后部门" prop="newDeptId">
            <DeptSelect v-model="formData.newDeptId" class="!w-1/1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="转全职后岗位" prop="newPostName">
            <el-input
              v-model="formData.newPostName"
              maxlength="255"
              placeholder="未调整则保持当前岗位"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :span="12">
          <el-form-item label="转全职后职级" prop="newPostLevel">
            <el-input
              v-model="formData.newPostLevel"
              maxlength="255"
              placeholder="未调整则保持当前职级"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="转全职后上级" prop="newLeaderEmployeeId">
            <HrmEmployeeSelect
              v-model="formData.newLeaderEmployeeId"
              :disabled-ids="formData.employeeId ? [formData.employeeId] : []"
              placeholder="未调整则保持当前直属上级"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="转全职后工作地点" prop="newWorkAddress">
        <el-input
          v-model="formData.newWorkAddress"
          maxlength="255"
          placeholder="未调整则保持当前工作地点"
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
      <el-button type="primary" :loading="formLoading" @click="submitForm">确认转为全职</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'
import type { HrmEmployeeConvertToFullTimeReqVO, HrmEmployeeVO } from '@/api/hrm/employee'
import * as EmployeeApi from '@/api/hrm/employee'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import {
  HrmEmployeeChangeReason,
  HrmEmployeeChangeReasonOptions,
  HRM_EMPLOYEE_NO_PROBATION_MONTHS
} from '@/views/hrm/utils/constants'
import HrmEmployeeSelect from './components/HrmEmployeeSelect.vue'

defineOptions({ name: 'HrmEmployeeFullTimeForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的提交中
const formData = ref<HrmEmployeeConvertToFullTimeReqVO>({}) // 表单数据
const formRules: FormRules = {
  reason: [{ required: true, message: '请选择异动原因', trigger: 'change' }],
  probation: [{ required: true, message: '请设置试用期', trigger: 'change' }],
  effectTime: [{ required: true, message: '请选择生效日期', trigger: 'change' }]
} // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const employee = ref<HrmEmployeeVO>() // 员工信息
const changeReasonOptions = HrmEmployeeChangeReasonOptions // 异动原因选项

/** 打开弹窗 */
function open(row: HrmEmployeeVO) {
  dialogVisible.value = true
  employee.value = row
  resetForm(row)
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value || !(await formRef.value.validate())) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await EmployeeApi.convertEmployeeToFullTime(formData.value)
    message.success('转为全职办理成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(row: HrmEmployeeVO) {
  formData.value = {
    employeeId: row.id,
    reason: HrmEmployeeChangeReason.ORGANIZATION_ADJUSTMENT,
    probation: HRM_EMPLOYEE_NO_PROBATION_MONTHS,
    newDeptId: row.deptId,
    newPostName: row.postName,
    newPostLevel: row.postLevel,
    newWorkAddress: row.workAddress,
    newLeaderEmployeeId: row.leaderEmployeeId,
    effectTime: dayjs().startOf('day').valueOf()
  }
  formRef.value?.resetFields()
}
</script>
