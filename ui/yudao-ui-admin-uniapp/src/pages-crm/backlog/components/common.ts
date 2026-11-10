import { CRM_SCENE_TYPES } from '@/api/crm/permission'

/** 待办筛选选项（与 vue3 backlog/components/common.ts 对齐） */
export interface BacklogFilterOption {
  label: string
  value: any
}

export interface BacklogFilter {
  prop: string
  label: string
  options: BacklogFilterOption[]
}

/** 归属范围（复用全局 CRM 归属场景选项） */
export const SCENE_TYPES: BacklogFilterOption[] = CRM_SCENE_TYPES

/** 联系状态 */
export const CONTACT_STATUS: BacklogFilterOption[] = [
  { label: '今日需联系', value: 1 },
  { label: '已逾期', value: 2 },
  { label: '已联系', value: 3 },
]

/** 跟进状态 */
export const FOLLOWUP_STATUS: BacklogFilterOption[] = [
  { label: '待跟进', value: false },
  { label: '已跟进', value: true },
]

/** 审批状态 */
export const AUDIT_STATUS: BacklogFilterOption[] = [
  { label: '待审批', value: 10 },
  { label: '审核通过', value: 20 },
  { label: '审核不通过', value: 30 },
]

/** 合同到期状态 */
export const CONTRACT_EXPIRY_TYPE: BacklogFilterOption[] = [
  { label: '即将过期', value: 1 },
  { label: '已过期', value: 2 },
]

/** 回款提醒类型 */
export const RECEIVABLE_REMIND_TYPE: BacklogFilterOption[] = [
  { label: '待回款', value: 1 },
  { label: '已逾期', value: 2 },
  { label: '已回款', value: 3 },
]

/** 构建筛选默认值（取每项第一个选项） */
export function buildDefaultFilters(filters: BacklogFilter[]) {
  const values: Record<string, any> = {}
  filters.forEach((filter) => {
    values[filter.prop] = filter.options[0]?.value
  })
  return values
}
