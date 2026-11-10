import type { FormCreateOptionItem, NormalizedFormCreateRule } from '../../../../../types/typing'
import type { CustomSelectOption } from './types'
import type { Dept } from '@/api/system/dept'
import type { User } from '@/api/system/user'
import { getSimpleDeptList } from '@/api/system/dept'
import { getSimpleUserList } from '@/api/system/user'
import { getDictOptions } from '@/hooks/useDict'
import { http } from '@/http/http'
import { flattenDeptList } from './utils'

export async function loadUserOptions(): Promise<CustomSelectOption[]> {
  const users = await getSimpleUserList()
  return users.map(toUserOption).filter(Boolean) as CustomSelectOption[]
}

export async function loadDeptOptions(returnType: 'id' | 'name' = 'id'): Promise<CustomSelectOption[]> {
  const depts = await getSimpleDeptList()
  return flattenDeptList(depts as Dept[]).map(dept => toDeptOption(dept, returnType)).filter(Boolean) as CustomSelectOption[]
}

export function loadDictOptions(dictType: string, valueType: 'string' | 'number' | 'boolean' = 'string') {
  return getDictOptions(dictType, valueType).map(toCustomOption).filter(Boolean) as CustomSelectOption[]
}

export async function loadApiSelectOptions(rule: NormalizedFormCreateRule): Promise<CustomSelectOption[]> {
  const url = rule.props?.url
  if (!url) {
    return []
  }
  const params = parseJsonObject(rule.props?.data)
  const method = String(rule.props?.method || 'GET').toUpperCase()
  const data = method === 'POST'
    ? await http.post<any>(url, params)
    : await http.get<any>(url, params)
  const list = Array.isArray(data)
    ? data
    : Array.isArray(data?.list)
      ? data.list
      : Array.isArray(data?.records)
        ? data.records
        : []

  return list.map((item: Record<string, any>) => ({
    displayLabel: resolveField(item, rule.props?.labelField || 'label') || item.nickname || item.name || item.label || String(item.id ?? ''),
    label: resolveField(item, rule.props?.labelField || 'label') || item.nickname || item.name || item.label || String(item.id ?? ''),
    raw: item,
    value: resolveField(item, rule.props?.valueField || 'value') ?? item.id ?? item.value,
  })).filter(item => item.value !== undefined && item.value !== null && item.value !== '') as CustomSelectOption[]
}

function toUserOption(user: User): CustomSelectOption | undefined {
  if (user.id === undefined || user.id === null) {
    return undefined
  }
  const displayLabel = user.nickname || user.username || String(user.id)
  const deptLabel = user.deptName || '未分配部门'
  const description = [user.username, user.deptName, user.mobile].filter(Boolean).join(' / ')
  return {
    avatar: user.avatar,
    description,
    displayLabel,
    label: `${displayLabel}  [${deptLabel}]`,
    raw: user as unknown as Record<string, any>,
    value: user.id,
  }
}

function toCustomOption(option: FormCreateOptionItem): CustomSelectOption | undefined {
  if (option.value === undefined || option.value === null) {
    return undefined
  }
  const label = option.label || option.text || String(option.value)
  return {
    disabled: option.disabled,
    displayLabel: label,
    label,
    raw: option,
    value: option.value,
  }
}

function toDeptOption(dept: { id?: number, name: string, path: string, level: number, raw: Record<string, any> }, returnType: 'id' | 'name') {
  const value = returnType === 'name' ? dept.name : dept.id
  if (value === undefined || value === null) {
    return undefined
  }
  return {
    description: dept.path,
    displayLabel: dept.name,
    label: dept.path && dept.path !== dept.name ? `${dept.name} ${dept.path}` : dept.name,
    level: dept.level,
    path: dept.path,
    raw: dept.raw,
    value,
  }
}

function parseJsonObject(value: any) {
  if (!value) {
    return undefined
  }
  if (typeof value !== 'string') {
    return value
  }
  try {
    return JSON.parse(value)
  } catch {
    return undefined
  }
}

function resolveField(item: Record<string, any>, field: string) {
  if (field.includes('${')) {
    return field.replace(/\$\{([^}]+)\}/g, (_, key) => item[key.trim()] ?? '')
  }
  return item[field]
}
