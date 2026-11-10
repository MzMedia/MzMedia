<template>
  <div class="mx-auto max-w-1100px">
    <el-form-item label="考核结果模板" prop="resultTemplateId">
      <el-select
        v-model="model.resultTemplateId"
        class="!w-1/1"
        filterable
        placeholder="请选择考核结果模板"
        @change="handleResultTemplateChange"
      >
        <el-option
          v-for="template in resultTemplateOptions"
          :key="template.id"
          :label="template.name"
          :value="template.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="同步到薪资" prop="syncToSalary">
      <el-switch v-model="syncToSalary" />
    </el-form-item>
    <el-form-item v-if="model.syncToSalary" label="参与计薪月份" prop="paidForMonth" required>
      <el-date-picker
        v-model="model.paidForMonth"
        class="!w-1/1"
        placeholder="请选择参与计薪月份"
        type="month"
        value-format="YYYY-MM"
      />
    </el-form-item>
    <el-form-item label="结果等级" prop="resultConfig">
      <PerformanceResultLevelForm
        ref="resultLevelFormRef"
        v-model="resultLevels"
        :disabled="props.disabled"
      />
    </el-form-item>
  </div>
</template>

<script lang="ts" setup>
import type { ResultTemplateVO } from '@/api/hrm/performance/config/result-template'
import type { PerformancePlanVO } from '@/api/hrm/performance/plan'
import PerformanceResultLevelForm from '../../config/result-template/components/PerformanceResultLevelForm.vue'

defineOptions({ name: 'HrmPerformancePlanResultForm' })

const props = defineProps<{
  disabled: boolean
  resultTemplateList: ResultTemplateVO[]
}>()

const model = defineModel<PerformancePlanVO>({ required: true }) // 绩效计划表单数据
const resultLevelFormRef = ref<InstanceType<typeof PerformanceResultLevelForm>>() // 结果等级表单 Ref
const resultTemplateOptions = computed(() =>
  props.resultTemplateList.filter(
    (template): template is ResultTemplateVO & { id: number } => template.id !== undefined
  )
)
const resultLevels = computed({
  get: () => model.value.resultConfig?.levels || [],
  set: (value) => {
    model.value.resultConfig = {
      name: model.value.resultConfig?.name || '',
      levels: value
    }
  }
})
const syncToSalary = computed({
  get: () => Boolean(model.value.syncToSalary),
  set: (value) => {
    model.value.syncToSalary = value
    if (!value) {
      model.value.paidForMonth = ''
    }
  }
})

/** 切换结果模板 */
function handleResultTemplateChange(resultTemplateId?: number) {
  const resultTemplate = props.resultTemplateList.find(
    (template) => template.id === resultTemplateId
  )
  model.value.resultConfig = resultTemplate
    ? {
        name: resultTemplate.name,
        levels: resultTemplate.levels.map((level) => ({ ...level }))
      }
    : { name: '', levels: [] }
}

/** 校验结果等级 */
function validate() {
  return resultLevelFormRef.value?.validate()
}
defineExpose({ validate }) // 提供 validate 方法，用于校验表单
</script>
