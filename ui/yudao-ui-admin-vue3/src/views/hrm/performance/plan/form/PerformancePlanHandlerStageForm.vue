<template>
  <div class="w-full">
    <el-table :data="modelValue" border>
      <el-table-column label="处理人" min-width="150">
        <template #default="scope">
          <el-select
            v-model="scope.row.type"
            class="!w-1/1"
            placeholder="请选择处理人"
            :disabled="disabled"
            @change="handleHandlerTypeChange(scope.row)"
          >
            <el-option
              v-for="item in HrmPerformanceHandlerTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="处理人范围" min-width="220">
        <template #default="scope">
          <HrmPerformanceRaterLevelSelect
            v-if="
              scope.row.type === HrmPerformanceRaterType.SUPERIOR ||
              scope.row.type === HrmPerformanceRaterType.DEPT_LEADER
            "
            v-model="scope.row.level"
            class="!w-1/1"
            :rater-type="scope.row.type"
            :disabled="disabled"
          />
          <HrmEmployeeSelect
            v-else
            v-model="scope.row.employeeId"
            placeholder="请选择处理员工"
            :disabled="disabled"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="72">
        <template #default="scope">
          <el-button
            link
            type="danger"
            title="删除处理节点"
            :disabled="disabled || modelValue.length <= 1"
            @click="removeStage(scope.$index)"
          >
            <Icon icon="ep:delete" />
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button
      class="mt-12px"
      plain
      :disabled="disabled || modelValue.length >= 3"
      @click="addStage"
    >
      <Icon icon="ep:plus" class="mr-5px" />新增处理节点
    </el-button>
  </div>
</template>

<script lang="ts" setup>
import type { PerformanceHandlerStageVO } from '@/api/hrm/performance/plan'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import HrmPerformanceRaterLevelSelect from '@/views/hrm/performance/components/HrmPerformanceRaterLevelSelect.vue'
import {
  HrmPerformanceHandlerTypeOptions,
  HrmPerformanceRaterType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmPerformancePlanHandlerStageForm' })

const props = withDefaults(
  defineProps<{
    modelValue: PerformanceHandlerStageVO[]
    disabled?: boolean
  }>(),
  {
    disabled: false
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: PerformanceHandlerStageVO[]]
}>() // 定义 modelValue 更新事件

/** 新增处理阶段 */
function addStage() {
  emit('update:modelValue', [
    ...props.modelValue,
    { type: HrmPerformanceRaterType.SUPERIOR, level: 1 }
  ])
}

/** 删除处理阶段 */
function removeStage(index: number) {
  emit(
    'update:modelValue',
    props.modelValue.filter((_, stageIndex) => stageIndex !== index)
  )
}

/** 处理人类型变化操作 */
function handleHandlerTypeChange(stage: PerformanceHandlerStageVO) {
  stage.level =
    stage.type === HrmPerformanceRaterType.SUPERIOR ||
    stage.type === HrmPerformanceRaterType.DEPT_LEADER
      ? 1
      : undefined
  stage.employeeId = undefined
}
</script>
