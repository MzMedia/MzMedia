import type { NormalizedFormCreateRule } from '../../../../../types/typing'
import type { CustomSelectOption, CustomSelectValue, FlattenedDept } from './types'

export function isMultipleSelect(rule: NormalizedFormCreateRule) {
  return !!rule.props?.multiple || rule.props?.mode === 'multiple'
}

export function normalizeSelectValue(value: any, multiple: boolean): CustomSelectValue | CustomSelectValue[] | '' {
  if (multiple) {
    return Array.isArray(value) ? value.filter(isSelectableValue) : []
  }
  return isSelectableValue(value) ? value : ''
}

export function formatSelectedSummary(
  value: any,
  options: CustomSelectOption[],
  multiple: boolean,
  unit: string,
  maxVisible = 2,
) {
  if (multiple) {
    const values = normalizeSelectValue(value, true) as CustomSelectValue[]
    if (values.length === 0) {
      return ''
    }
    const labels = values.map(item => getOptionLabel(item, options)).filter(Boolean)
    const visibleLabels = labels.slice(0, maxVisible).join('、')
    if (values.length > maxVisible) {
      return `${visibleLabels} 等 ${values.length}${unit}`
    }
    return visibleLabels
  }

  const nextValue = normalizeSelectValue(value, false)
  if (nextValue === '') {
    return ''
  }
  return getOptionLabel(nextValue as CustomSelectValue, options)
}

export function getOptionLabel(value: CustomSelectValue, options: CustomSelectOption[]) {
  const option = options.find(item => isSameValue(item.value, value))
  return option?.displayLabel || option?.label || String(value)
}

export function isSameValue(left: CustomSelectValue, right: CustomSelectValue) {
  return left === right || String(left) === String(right)
}

export function flattenDeptList(list: Record<string, any>[] = []) {
  if (hasTreeChildren(list)) {
    return flattenTreeDepts(list)
  }
  return flattenFlatDepts(list)
}

function isSelectableValue(value: any): value is CustomSelectValue {
  return ['string', 'number', 'boolean'].includes(typeof value) && value !== ''
}

function hasTreeChildren(list: Record<string, any>[]) {
  return list.some(item => Array.isArray(item.children) && item.children.length > 0)
}

function flattenTreeDepts(list: Record<string, any>[], parentPath = '', level = 0): FlattenedDept[] {
  return list.flatMap((dept) => {
    const name = dept.name || String(dept.id || '')
    const path = parentPath ? `${parentPath} / ${name}` : name
    return [
      {
        id: dept.id,
        name,
        parentId: dept.parentId,
        path,
        level,
        raw: dept,
      },
      ...flattenTreeDepts(dept.children || [], path, level + 1),
    ]
  })
}

function flattenFlatDepts(list: Record<string, any>[]) {
  const nodeMap = new Map<any, Record<string, any> & { children: Record<string, any>[] }>()
  const roots: Array<Record<string, any> & { children: Record<string, any>[] }> = []

  list.forEach((dept) => {
    nodeMap.set(dept.id, { ...dept, children: [] })
  })

  nodeMap.forEach((dept) => {
    const parent = nodeMap.get(dept.parentId)
    if (parent && parent.id !== dept.id) {
      parent.children.push(dept)
    } else {
      roots.push(dept)
    }
  })

  return flattenTreeDepts(roots.length ? roots : Array.from(nodeMap.values()))
}
