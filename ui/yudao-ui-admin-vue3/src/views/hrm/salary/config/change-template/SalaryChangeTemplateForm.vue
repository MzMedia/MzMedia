<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <!-- 基本信息 -->
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" maxlength="64" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="默认模板" prop="defaultStatus">
        <el-switch v-model="formData.defaultStatus" />
      </el-form-item>
      <el-form-item label="调薪项" prop="options">
        <SalaryChangeOptionSelect ref="optionSelectRef" v-model="formData.options" />
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as SalaryChangeTemplateApi from '@/api/hrm/salary/config/change-template'
import SalaryChangeOptionSelect from '../option/components/SalaryChangeOptionSelect.vue'

defineOptions({ name: 'HrmSalaryChangeTemplateForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<SalaryChangeTemplateApi.HrmSalaryChangeTemplateVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const optionSelectRef = ref<InstanceType<typeof SalaryChangeOptionSelect>>() // 调薪项选择器 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await SalaryChangeTemplateApi.getSalaryChangeTemplate(id)
    } finally {
      formLoading.value = false
    }
  }
  // 初始化调薪项选择器
  await nextTick()
  await optionSelectRef.value?.init(type === 'create')
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await SalaryChangeTemplateApi.createSalaryChangeTemplate(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await SalaryChangeTemplateApi.updateSalaryChangeTemplate(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 创建表单默认值 */
function createDefaultFormData(): SalaryChangeTemplateApi.HrmSalaryChangeTemplateVO {
  return {
    name: '',
    defaultStatus: false,
    options: []
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}
</script>
