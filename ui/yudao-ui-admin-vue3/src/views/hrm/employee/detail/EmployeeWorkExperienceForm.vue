<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="工作单位" prop="workUnit">
        <el-input v-model="formData.workUnit" :maxlength="255" placeholder="请输入工作单位" />
      </el-form-item>
      <el-form-item label="职务" prop="postName">
        <el-input v-model="formData.postName" :maxlength="255" placeholder="请输入职务" />
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
      <el-form-item label="离职原因">
        <el-input v-model="formData.reason" :maxlength="1024" placeholder="请输入离职原因" />
      </el-form-item>
      <el-form-item label="证明人">
        <el-input v-model="formData.witnessName" :maxlength="255" placeholder="请输入证明人" />
      </el-form-item>
      <el-form-item label="证明人电话" prop="witnessPhone">
        <el-input v-model="formData.witnessPhone" :maxlength="32" placeholder="请输入证明人电话" />
      </el-form-item>
      <el-form-item label="工作备注">
        <el-input
          v-model="formData.remark"
          :maxlength="500"
          :rows="3"
          placeholder="请输入工作备注"
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
import * as WorkExperienceApi from '@/api/hrm/employee/work-experience'

defineOptions({ name: 'HrmEmployeeWorkExperienceForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<WorkExperienceApi.HrmEmployeeWorkExperienceVO>({}) // 表单数据
const formRules = reactive<FormRules>({
  workUnit: [{ required: true, message: '工作单位不能为空', trigger: 'blur' }],
  postName: [{ required: true, message: '职务不能为空', trigger: 'blur' }],
  startTime: [
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.startTime != null &&
          formData.value.endTime != null &&
          Number(formData.value.endTime) < Number(formData.value.startTime)
        ) {
          callback(new Error('结束日期不能早于开始日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  endTime: [
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.startTime != null &&
          formData.value.endTime != null &&
          Number(formData.value.endTime) < Number(formData.value.startTime)
        ) {
          callback(new Error('结束日期不能早于开始日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  witnessPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改工作经历' : '新增工作经历')) // 弹窗的标题

/** 打开弹窗 */
function open(employeeId: number, row?: WorkExperienceApi.HrmEmployeeWorkExperienceVO) {
  dialogVisible.value = true
  resetForm()
  // 设置表单数据
  formData.value = {
    ...formData.value,
    employeeId,
    ...row
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

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
      await WorkExperienceApi.updateEmployeeWorkExperience(formData.value)
    } else {
      await WorkExperienceApi.createEmployeeWorkExperience(formData.value)
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
    sort: 1
  }
}
</script>
