<template>
  <div class="w-full">
    <div
      v-for="(scope, index) in modelValue"
      :key="index"
      class="mb-12px flex items-center gap-12px"
    >
      <el-select
        v-model="scope.type"
        class="!w-150px flex-none"
        placeholder="请选择范围类型"
        :disabled="disabled"
        @change="handleScopeTypeChange(scope)"
      >
        <el-option
          label="员工部门"
          :value="HrmPerformancePlanScopeType.EMPLOYEE_DEPT"
          :disabled="
            hasEmployeeDeptScope && scope.type !== HrmPerformancePlanScopeType.EMPLOYEE_DEPT
          "
        />
        <el-option label="聘用形式" :value="HrmPerformancePlanScopeType.EMPLOYMENT" />
      </el-select>
      <template v-if="scope.type === HrmPerformancePlanScopeType.EMPLOYEE_DEPT">
        <HrmEmployeeSelect
          v-model="scope.employeeIds"
          multiple
          class="!w-280px"
          placeholder="请选择员工"
          :disabled="disabled"
        />
        <DeptSelect
          v-model="scope.deptIds"
          multiple
          class="!w-280px"
          placeholder="请选择部门"
          :disabled="disabled"
        />
      </template>
      <template v-else>
        <el-select
          v-model="scope.employeeType"
          class="!w-280px"
          placeholder="请选择聘用形式"
          :disabled="disabled"
          @change="handleEmployTypeChange(scope)"
        >
          <el-option label="正式" :value="HrmEmployeeType.FORMAL" />
          <el-option label="非正式" :value="HrmEmployeeType.INFORMAL" />
        </el-select>
        <el-select
          v-model="scope.employeeStatuses"
          multiple
          class="!w-280px"
          placeholder="请选择员工状态"
          :disabled="disabled"
        >
          <el-option
            v-for="option in getEmployeeStatusOptions(scope.employeeType)"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </template>
      <el-button
        link
        type="danger"
        title="删除考核范围"
        :disabled="disabled || modelValue.length <= 1"
        @click="removeScope(index)"
      >
        <Icon icon="ep:delete" />
      </el-button>
    </div>
    <el-button plain :disabled="disabled || modelValue.length >= 3" @click="addScope">
      <Icon icon="ep:plus" class="mr-5px" />新增考核范围
    </el-button>
  </div>
</template>

<script lang="ts" setup>
import type { PerformanceScopeVO } from '@/api/hrm/performance/plan'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import {
  HRM_EMPLOYEE_NON_FORMAL_STATUSES,
  HrmEmployeeStatus,
  HrmEmployeeType,
  HrmPerformancePlanScopeType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmPerformancePlanScopeForm' })

const props = withDefaults(
  defineProps<{
    modelValue: PerformanceScopeVO[]
    disabled?: boolean
  }>(),
  {
    disabled: false
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: PerformanceScopeVO[]]
}>() // 定义 modelValue 更新事件

const hasEmployeeDeptScope = computed(() =>
  props.modelValue.some((scope) => scope.type === HrmPerformancePlanScopeType.EMPLOYEE_DEPT)
)

/** 新增考评范围 */
function addScope() {
  const type = hasEmployeeDeptScope.value
    ? HrmPerformancePlanScopeType.EMPLOYMENT
    : HrmPerformancePlanScopeType.EMPLOYEE_DEPT
  emit('update:modelValue', [...props.modelValue, createScope(type)])
}

/** 删除考评范围 */
function removeScope(index: number) {
  emit(
    'update:modelValue',
    props.modelValue.filter((_, scopeIndex) => scopeIndex !== index)
  )
}

/** 切换考评范围类型 */
function handleScopeTypeChange(scope: PerformanceScopeVO) {
  const replacement = createScope(scope.type)
  Object.keys(scope).forEach((key) => delete scope[key as keyof PerformanceScopeVO])
  Object.assign(scope, replacement)
}

/** 切换聘用形式 */
function handleEmployTypeChange(scope: PerformanceScopeVO) {
  scope.employeeStatuses = []
}

/** 创建考评范围 */
function createScope(type: number = HrmPerformancePlanScopeType.EMPLOYEE_DEPT): PerformanceScopeVO {
  return type === HrmPerformancePlanScopeType.EMPLOYEE_DEPT
    ? { type, employeeIds: [], deptIds: [] }
    : { type, employeeType: HrmEmployeeType.FORMAL, employeeStatuses: [] }
}

/** 获得员工状态选项 */
function getEmployeeStatusOptions(employType?: number) {
  if (employType === HrmEmployeeType.INFORMAL) {
    const labelMap: Record<number, string> = {
      [HrmEmployeeStatus.INTERN]: '实习',
      [HrmEmployeeStatus.PART_TIME]: '兼职',
      [HrmEmployeeStatus.LABOR]: '劳务',
      [HrmEmployeeStatus.CONSULTANT]: '顾问',
      [HrmEmployeeStatus.REHIRE]: '返聘',
      [HrmEmployeeStatus.OUTSOURCE]: '外包'
    }
    return HRM_EMPLOYEE_NON_FORMAL_STATUSES.map((value) => ({ value, label: labelMap[value] }))
  }
  return [
    { label: '正式', value: HrmEmployeeStatus.REGULAR },
    { label: '试用', value: HrmEmployeeStatus.PROBATION }
  ]
}
</script>
