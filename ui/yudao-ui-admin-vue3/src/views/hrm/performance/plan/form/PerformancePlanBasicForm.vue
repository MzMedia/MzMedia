<template>
  <div class="mx-auto max-w-1100px">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="考核计划名称" prop="name">
          <el-input v-model="model.name" maxlength="50" placeholder="请输入考核计划名称" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="周期类型" prop="cycleType">
          <el-select
            v-model="model.cycleType"
            class="!w-1/1"
            placeholder="请选择周期类型"
            @change="handleCycleTypeChange"
          >
            <el-option
              v-for="item in HrmPerformanceCycleTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="考核周期" prop="cycle">
      <el-date-picker
        v-if="model.cycleType === HrmPerformanceCycleType.MONTH"
        v-model="model.cycle"
        class="!w-1/1"
        placeholder="请选择月份"
        type="month"
        value-format="YYYY-MM"
      />
      <div
        v-else-if="model.cycleType === HrmPerformanceCycleType.QUARTER"
        class="grid w-1/1 grid-cols-2 gap-12px"
      >
        <el-date-picker
          v-model="model.cycle"
          class="!w-1/1"
          placeholder="请选择年份"
          type="year"
          value-format="YYYY"
        />
        <el-select v-model="model.quarter" class="!w-1/1" placeholder="请选择季度">
          <el-option label="第一季度" :value="1" />
          <el-option label="第二季度" :value="2" />
          <el-option label="第三季度" :value="3" />
          <el-option label="第四季度" :value="4" />
        </el-select>
      </div>
      <el-date-picker
        v-else-if="model.cycleType !== HrmPerformanceCycleType.OTHER"
        v-model="model.cycle"
        class="!w-1/1"
        placeholder="请选择年份"
        type="year"
        value-format="YYYY"
      />
      <el-date-picker
        v-else
        v-model="customDateRange"
        class="!w-1/1"
        end-placeholder="结束日期"
        start-placeholder="开始日期"
        type="daterange"
        value-format="YYYY-MM-DD"
      />
    </el-form-item>
    <el-form-item label="考核范围" prop="scopes">
      <PerformancePlanScopeForm v-model="scopes" />
    </el-form-item>
    <el-form-item label="考核说明" prop="description">
      <el-input
        v-model="model.description"
        :rows="4"
        maxlength="200"
        placeholder="请输入考核说明"
        show-word-limit
        type="textarea"
      />
    </el-form-item>
  </div>
</template>

<script lang="ts" setup>
import type { PerformancePlanVO, PerformanceScopeVO } from '@/api/hrm/performance/plan'
import {
  HrmPerformanceCycleType,
  HrmPerformanceCycleTypeOptions
} from '@/views/hrm/utils/constants'
import PerformancePlanScopeForm from './PerformancePlanScopeForm.vue'

defineOptions({ name: 'HrmPerformancePlanBasicForm' })

const model = defineModel<PerformancePlanVO>({
  required: true
})
const customDateRange = defineModel<string[]>('customDateRange', {
  required: true
})
const scopes = computed({
  get: () => model.value.scopes || [],
  set: (value: PerformanceScopeVO[]) => (model.value.scopes = value)
})

/** 切换考核周期类型 */
function handleCycleTypeChange() {
  model.value.cycle = ''
  model.value.quarter = model.value.cycleType === HrmPerformanceCycleType.QUARTER ? 1 : undefined
  customDateRange.value = []
}
</script>
