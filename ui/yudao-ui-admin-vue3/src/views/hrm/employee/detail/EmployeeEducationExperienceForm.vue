<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="学历" prop="education">
        <el-select v-model="formData.education" class="!w-1/1" clearable placeholder="请选择学历">
          <el-option
            v-for="item in getIntDictOptions(DICT_TYPE.HRM_EMPLOYEE_EDUCATION)"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="毕业院校" prop="graduateSchool">
        <el-input v-model="formData.graduateSchool" :maxlength="255" placeholder="请输入毕业院校" />
      </el-form-item>
      <el-form-item label="专业" prop="major">
        <el-input v-model="formData.major" :maxlength="255" placeholder="请输入专业" />
      </el-form-item>
      <el-form-item label="入学日期" prop="admissionTime">
        <el-date-picker
          v-model="formData.admissionTime"
          class="!w-1/1"
          placeholder="请选择入学日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="毕业日期" prop="graduationTime">
        <el-date-picker
          v-model="formData.graduationTime"
          class="!w-1/1"
          placeholder="请选择毕业日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="教学方式">
        <el-select
          v-model="formData.teachingMethods"
          class="!w-1/1"
          clearable
          placeholder="请选择教学方式"
        >
          <el-option
            v-for="item in HrmEmployeeTeachingMethodOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="第一学历" prop="firstDegree">
        <el-switch v-model="formData.firstDegree" />
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
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as EducationExperienceApi from '@/api/hrm/employee/education-experience'
import { HrmEmployeeTeachingMethodOptions } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmEmployeeEducationExperienceForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<EducationExperienceApi.HrmEmployeeEducationExperienceVO>({}) // 表单数据
const formRules = reactive<FormRules>({
  education: [{ required: true, message: '学历不能为空', trigger: 'change' }],
  graduateSchool: [{ required: true, message: '毕业院校不能为空', trigger: 'blur' }],
  major: [{ required: true, message: '专业不能为空', trigger: 'blur' }],
  admissionTime: [
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.admissionTime != null &&
          formData.value.graduationTime != null &&
          Number(formData.value.graduationTime) < Number(formData.value.admissionTime)
        ) {
          callback(new Error('毕业日期不能早于入学日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  graduationTime: [
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.admissionTime != null &&
          formData.value.graduationTime != null &&
          Number(formData.value.graduationTime) < Number(formData.value.admissionTime)
        ) {
          callback(new Error('毕业日期不能早于入学日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  firstDegree: [{ required: true, message: '是否第一学历不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改教育经历' : '新增教育经历')) // 弹窗的标题

/** 打开弹窗 */
function open(employeeId: number, row?: EducationExperienceApi.HrmEmployeeEducationExperienceVO) {
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
      await EducationExperienceApi.updateEmployeeEducationExperience(formData.value)
    } else {
      await EducationExperienceApi.createEmployeeEducationExperience(formData.value)
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
    firstDegree: false
  }
}
</script>
