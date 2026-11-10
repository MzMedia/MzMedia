<template>
  <ContentWrap title="待办提醒">
    <div class="grid grid-cols-3 gap-y-20px">
      <button
        v-for="todo in todoItems"
        :key="todo.label"
        :disabled="todo.disabled"
        class="relative min-h-78px flex flex-col items-center justify-center border-0 bg-transparent"
        :class="todo.disabled ? 'cursor-default' : 'group cursor-pointer'"
        type="button"
        @click="goTodo(todo.action)"
      >
        <strong
          class="text-22px text-[var(--el-text-color-primary)] leading-28px group-hover:text-[var(--el-color-primary)]"
        >
          {{ todo.value }}
        </strong>
        <span
          class="mt-6px text-13px text-[var(--el-text-color-secondary)] group-hover:text-[var(--el-color-primary)]"
        >
          {{ todo.label }}
        </span>
        <small
          class="absolute left-[calc(50%+18px)] top-18px text-[var(--el-text-color-placeholder)]"
        >
          {{ todo.unit }}
        </small>
      </button>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { HrmHrHomeTodoSurveyVO } from '@/api/hrm/home'
import { checkPermi } from '@/utils/permission'
import { HrmEmployeeStatusTab, HrmEmployeeTodoType } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmHomeTodoSurvey' })

type TodoAction = 'salary' | 'leave' | 'contract' | 'regular' | 'entry' | 'birthday'

const props = defineProps<{
  survey?: HrmHrHomeTodoSurveyVO
}>()

const router = useRouter() // 路由
const canQueryEmployee = checkPermi(['hrm:employee:query']) // 是否拥有员工查询权限
const canQuerySalary = checkPermi(['hrm:salary:month-record:query']) // 是否拥有薪资查询权限

const todoItems = computed(() => [
  {
    label: '待核算薪资',
    value: props.survey?.toSalaryComputeCount || 0,
    unit: '条',
    action: 'salary' as TodoAction,
    disabled: !canQuerySalary || !props.survey?.toSalaryComputeCount
  },
  {
    label: '待离职',
    value: props.survey?.toLeaveCount || 0,
    unit: '人',
    action: 'leave' as TodoAction,
    disabled: !canQueryEmployee
  },
  {
    label: '合同到期',
    value: props.survey?.toExpireContractCount || 0,
    unit: '人',
    action: 'contract' as TodoAction,
    disabled: !canQueryEmployee
  },
  {
    label: '待转正',
    value: props.survey?.toRegularCount || 0,
    unit: '人',
    action: 'regular' as TodoAction,
    disabled: !canQueryEmployee
  },
  {
    label: '待入职',
    value: props.survey?.toEntryCount || 0,
    unit: '人',
    action: 'entry' as TodoAction,
    disabled: !canQueryEmployee
  },
  {
    label: '生日',
    value: props.survey?.toBirthdayCount || 0,
    unit: '人',
    action: 'birthday' as TodoAction,
    disabled: !canQueryEmployee
  }
])

/** 打开待办对应的业务列表 */
function goTodo(action: TodoAction) {
  if (action === 'salary') {
    if (canQuerySalary) {
      router.push({ name: 'HrmSalaryMonthRecord' })
    }
    return
  }
  if (!canQueryEmployee) {
    return
  }
  const employeeFilters = {
    leave: {
      todoType: HrmEmployeeTodoType.PENDING_LEAVE,
      statusCategory: HrmEmployeeStatusTab.PENDING_LEAVE
    },
    contract: {
      todoType: HrmEmployeeTodoType.CONTRACT_EXPIRE,
      statusCategory: HrmEmployeeStatusTab.ACTIVE
    },
    regular: {
      todoType: HrmEmployeeTodoType.REGULAR,
      statusCategory: HrmEmployeeStatusTab.ACTIVE
    },
    entry: {
      todoType: HrmEmployeeTodoType.PENDING_ENTRY,
      statusCategory: HrmEmployeeStatusTab.PENDING_ENTRY
    },
    birthday: {
      todoType: HrmEmployeeTodoType.BIRTHDAY,
      statusCategory: HrmEmployeeStatusTab.ACTIVE
    }
  }
  router.push({ name: 'HrmEmployee', query: employeeFilters[action] })
}
</script>
