<template>
  <el-select
    v-model="selectValue"
    :clearable="clearable"
    :disabled="disabled"
    :filterable="filterable"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
  >
    <el-option
      v-for="template in templateOptions"
      :key="template.id"
      :label="template.name"
      :value="template.id"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as PerformanceAssessmentTemplateApi from '@/api/hrm/performance/config/assessment-template'

defineOptions({ name: 'HrmPerformanceAssessmentTemplateSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number
    disabled?: boolean
    clearable?: boolean
    filterable?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    filterable: true,
    placeholder: '请选择考核指标模板'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [value: number | undefined]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 选项的加载中
const templateList = ref<PerformanceAssessmentTemplateApi.AssessmentTemplateVO[]>([]) // 模板列表
const selectedTemplate = ref<PerformanceAssessmentTemplateApi.AssessmentTemplateVO>() // 当前回显的停用模板
const templateOptions = computed(() => {
  const options = templateList.value.filter(
    (
      template
    ): template is PerformanceAssessmentTemplateApi.AssessmentTemplateVO & { id: number } =>
      template.id !== undefined
  )
  const currentTemplate = selectedTemplate.value
  if (
    currentTemplate?.id === undefined ||
    options.some((template) => template.id === currentTemplate.id)
  ) {
    return options
  }
  return [
    currentTemplate as PerformanceAssessmentTemplateApi.AssessmentTemplateVO & { id: number },
    ...options
  ]
})
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value)
    emit('change', value)
  }
})

/** 补充当前选中的考核模板，支持已停用模板回显 */
async function ensureSelectedTemplate() {
  const templateId = props.modelValue
  selectedTemplate.value = undefined
  if (templateId == null || templateList.value.some((template) => template.id === templateId)) {
    return
  }
  const template =
    await PerformanceAssessmentTemplateApi.getPerformanceAssessmentTemplate(templateId)
  if (props.modelValue === templateId && template?.id === templateId) {
    selectedTemplate.value = template
  }
}

/** 获得考核模板选项 */
async function getTemplateList() {
  loading.value = true
  try {
    templateList.value =
      await PerformanceAssessmentTemplateApi.getPerformanceAssessmentTemplateSimpleList()
    await ensureSelectedTemplate()
  } finally {
    loading.value = false
  }
}

/** 监听选中考核模板变化 */
watch(
  () => props.modelValue,
  () => {
    ensureSelectedTemplate()
  }
)

/** 初始化 */
onMounted(() => {
  getTemplateList()
})
</script>
