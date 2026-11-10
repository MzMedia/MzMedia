import type { FormCreateRule } from '../../../types/typing'

export function getRuleChildren(rule: FormCreateRule): FormCreateRule[] {
  return normalizeChildRules(rule.children || rule.props?.children || rule.props?.rows || rule.props?.columns)
}

function normalizeChildRules(children: unknown): FormCreateRule[] {
  if (!children) {
    return []
  }
  if (Array.isArray(children)) {
    return children.flatMap(item => normalizeChildRules(item))
  }
  if (typeof children !== 'object') {
    return []
  }
  const child = children as FormCreateRule
  if (child.type || child.field) {
    return [child]
  }
  return normalizeChildRules(child.children || child.props?.children || child.props?.rows || child.props?.columns)
}
