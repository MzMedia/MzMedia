<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="920">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="结果设置名称" prop="name">
            <el-input
              v-model.trim="formData.name"
              maxlength="255"
              placeholder="请输入结果设置名称"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="结果等级" prop="levels">
        <PerformanceResultLevelForm ref="resultLevelFormRef" v-model="formData.levels" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as PerformanceResultTemplateApi from '@/api/hrm/performance/config/result-template'
import PerformanceResultLevelForm from './components/PerformanceResultLevelForm.vue'

/** 绩效结果模板表单 */
defineOptions({ name: 'HrmPerformanceResultTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<PerformanceResultTemplateApi.ResultTemplateVO>({
  id: undefined,
  name: '',
  levels: []
}) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '结果设置名称不能为空', trigger: 'blur' }]
})
const formRef = ref<FormInstance>() // 表单 Ref
const resultLevelFormRef = ref<InstanceType<typeof PerformanceResultLevelForm>>() // 结果等级表单 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      // 获取表单数据
      formData.value = await PerformanceResultTemplateApi.getPerformanceResultTemplate(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  if (!resultLevelFormRef.value?.validate()) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await PerformanceResultTemplateApi.createPerformanceResultTemplate(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await PerformanceResultTemplateApi.updatePerformanceResultTemplate(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    name: '',
    levels: [
      { name: 'S', minScore: 85, maxScore: 100, coefficient: 1.2 },
      { name: 'A', minScore: 75, maxScore: 84.99, coefficient: 1 },
      { name: 'B', minScore: 60, maxScore: 74.99, coefficient: 0.8 },
      { name: 'C', minScore: 0, maxScore: 59.99, coefficient: 0.6 }
    ]
  }
  formRef.value?.resetFields()
}
</script>
