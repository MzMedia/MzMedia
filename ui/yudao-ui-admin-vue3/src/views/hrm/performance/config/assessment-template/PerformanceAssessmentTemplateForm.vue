<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1120">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="考核模板名称" prop="name">
            <el-input
              v-model="formData.name"
              maxlength="50"
              placeholder="请输入考核模板名称"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="考核指标说明" prop="illustrate">
        <el-input
          v-model="formData.illustrate"
          :rows="3"
          maxlength="200"
          placeholder="请输入考核指标说明"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <PerformanceAssessmentConfigEditor ref="configEditorRef" v-model="formData" />
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as PerformanceAssessmentTemplateApi from '@/api/hrm/performance/config/assessment-template'
import {
  HrmPerformanceScoreCalculation,
  HrmPerformanceUpperLimitType
} from '@/views/hrm/utils/constants'
import PerformanceAssessmentConfigEditor from './components/PerformanceAssessmentConfigEditor.vue'

/** 绩效考核模板表单 */
defineOptions({ name: 'HrmPerformanceAssessmentTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<PerformanceAssessmentTemplateApi.AssessmentTemplateVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  name: [
    { required: true, message: '考核模板名称不能为空', trigger: 'blur' },
    { max: 50, message: '考核模板名称不能超过 50 个字符', trigger: 'blur' }
  ],
  illustrate: [{ max: 200, message: '考核指标说明不能超过 200 个字符', trigger: 'blur' }],
  scoreCalculation: [{ required: true, message: '总分计算不能为空', trigger: 'change' }],
  upperLimitType: [{ required: true, message: '评分上限类型不能为空', trigger: 'change' }],
  upperLimitScore: [{ required: true, message: '评分上限不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref
const configEditorRef = ref<InstanceType<typeof PerformanceAssessmentConfigEditor>>() // 考核配置编辑器 Ref

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
      formData.value = await PerformanceAssessmentTemplateApi.getPerformanceAssessmentTemplate(id)
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
  if (!configEditorRef.value?.validate()) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await PerformanceAssessmentTemplateApi.createPerformanceAssessmentTemplate(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await PerformanceAssessmentTemplateApi.updatePerformanceAssessmentTemplate(formData.value)
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
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}

/** 创建默认表单数据 */
function createDefaultFormData(): PerformanceAssessmentTemplateApi.AssessmentTemplateVO {
  return {
    id: undefined,
    name: '',
    illustrate: '',
    scoreCalculation: HrmPerformanceScoreCalculation.WEIGHTED,
    upperLimitType: HrmPerformanceUpperLimitType.UNIFIED,
    upperLimitScore: 100,
    dimensions: []
  }
}
</script>
