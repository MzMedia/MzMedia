<template>
  <div class="mx-auto max-w-1200px">
    <div v-loading="templateLoading">
      <PerformanceAssessmentConfigEditor
        ref="configEditorRef"
        v-model="assessmentConfig"
        :disabled="disabled"
        :show-dimensions="Boolean(model.assessmentTemplateId)"
        prop-prefix="assessmentConfig."
      >
        <template #after-score-config>
          <el-form-item label="考核指标模板" prop="assessmentTemplateId">
            <PerformanceAssessmentTemplateSelect
              v-model="model.assessmentTemplateId"
              @change="handleAssessmentTemplateChange"
            />
          </el-form-item>
        </template>
      </PerformanceAssessmentConfigEditor>
      <el-empty
        v-if="!model.assessmentTemplateId"
        :image-size="96"
        description="请选择考核指标模板"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import * as PerformanceAssessmentTemplateApi from '@/api/hrm/performance/config/assessment-template'
import type { PerformancePlanVO } from '@/api/hrm/performance/plan'
import PerformanceAssessmentConfigEditor from '@/views/hrm/performance/config/assessment-template/components/PerformanceAssessmentConfigEditor.vue'
import PerformanceAssessmentTemplateSelect from '@/views/hrm/performance/config/assessment-template/components/PerformanceAssessmentTemplateSelect.vue'
import { cloneAssessmentConfig, createDefaultAssessmentConfig } from '@/views/hrm/utils/performance'

defineOptions({ name: 'HrmPerformancePlanIndicatorForm' })

defineProps<{
  disabled: boolean
}>()

const model = defineModel<PerformancePlanVO>({ required: true }) // 绩效计划表单数据
const templateLoading = ref(false) // 考核模板的加载中
const configEditorRef = ref<InstanceType<typeof PerformanceAssessmentConfigEditor>>() // 考核配置编辑器 Ref
const assessmentConfig = computed({
  get: () => model.value.assessmentConfig || createDefaultAssessmentConfig(),
  set: (value) => {
    model.value.assessmentConfig = value
  }
})

/** 切换考核模板，并复制为当前计划的指标配置快照 */
async function handleAssessmentTemplateChange(templateId?: number) {
  model.value.assessmentConfig = createDefaultAssessmentConfig()
  if (!templateId) {
    return
  }
  templateLoading.value = true
  try {
    const template =
      await PerformanceAssessmentTemplateApi.getPerformanceAssessmentTemplate(templateId)
    if (model.value.assessmentTemplateId !== templateId) {
      return
    }
    model.value.assessmentConfig = cloneAssessmentConfig(template)
  } finally {
    templateLoading.value = false
  }
}

/** 校验指标配置 */
function validate() {
  return Boolean(model.value.assessmentTemplateId) && Boolean(configEditorRef.value?.validate())
}

defineExpose({ validate }) // 提供 validate 方法，用于校验表单
</script>
