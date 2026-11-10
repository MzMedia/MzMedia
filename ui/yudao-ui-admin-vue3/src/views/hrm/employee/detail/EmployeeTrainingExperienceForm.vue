<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="培训课程" prop="course">
        <el-input v-model="formData.course" :maxlength="128" placeholder="请输入培训课程" />
      </el-form-item>
      <el-form-item label="培训机构">
        <el-input
          v-model="formData.organizationName"
          :maxlength="128"
          placeholder="请输入培训机构"
        />
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
      <el-form-item label="培训时长">
        <el-input v-model="formData.duration" :maxlength="64" placeholder="请输入培训时长" />
      </el-form-item>
      <el-form-item label="培训成绩">
        <el-input v-model="formData.result" :maxlength="64" placeholder="请输入培训成绩" />
      </el-form-item>
      <el-form-item label="证书名称">
        <el-input
          v-model="formData.certificateName"
          :maxlength="128"
          placeholder="请输入证书名称"
        />
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
import * as TrainingExperienceApi from '@/api/hrm/employee/training-experience'

defineOptions({ name: 'HrmEmployeeTrainingExperienceForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<TrainingExperienceApi.HrmEmployeeTrainingExperienceVO>({}) // 表单数据
const formRules = reactive<FormRules>({
  course: [{ required: true, message: '培训课程不能为空', trigger: 'blur' }],
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
  ]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改培训经历' : '新增培训经历')) // 弹窗的标题

/** 打开弹窗 */
function open(employeeId: number, row?: TrainingExperienceApi.HrmEmployeeTrainingExperienceVO) {
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
      await TrainingExperienceApi.updateEmployeeTrainingExperience(formData.value)
    } else {
      await TrainingExperienceApi.createEmployeeTrainingExperience(formData.value)
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
