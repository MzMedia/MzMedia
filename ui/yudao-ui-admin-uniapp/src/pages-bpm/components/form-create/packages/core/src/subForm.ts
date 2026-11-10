import type { FormCreateRule, NormalizedFormCreateRule } from '../../../types/typing'
import { isSubFormTypeName } from './registry'
import { normalizeRules } from './utils'

export type ParseSubFormRules = (rules: FormCreateRule[]) => FormCreateRule[]

interface GetSubFormRulesOptions {
  createColumnTitleRule?: (column: Record<string, any>, index: number) => FormCreateRule | undefined
}

const MAX_EXTRACT_DEPTH = 30

export function isSubFormRule(rule: FormCreateRule) {
  return isSubFormTypeName(rule.type)
}

export function getSubFormRules(rule: FormCreateRule, options: GetSubFormRulesOptions = {}): FormCreateRule[] {
  const children = pickFirstRuleSource(rule.props?.rule, rule.children)
  if (children) {
    return extractSubFormRules(children)
  }
  if (Array.isArray(rule.props?.columns)) {
    return rule.props.columns.flatMap((column: Record<string, any>, index: number) => {
      const rules = getSubFormColumnRules(column)
      const titleRule = options.createColumnTitleRule?.(column, index)
      return titleRule ? [titleRule, ...rules] : rules
    })
  }
  return []
}

export function normalizeSubFormRules(
  rule: FormCreateRule,
  parseRules: ParseSubFormRules = rules => rules,
  options?: GetSubFormRulesOptions,
): NormalizedFormCreateRule[] {
  return normalizeRules(parseRules(getSubFormRules(rule, options)))
}

export function getSubFormColumnRules(column: Record<string, any>): FormCreateRule[] {
  const rules = pickFirstRuleSource(column.rule, column.rules, column.children, column.props?.rule, column.props?.children)
  return extractSubFormRules(rules)
}

function pickFirstRuleSource(...sources: unknown[]) {
  for (const source of sources) {
    if (Array.isArray(source)) {
      if (source.length > 0) {
        return source
      }
      continue
    }
    if (source && typeof source === 'object') {
      return source
    }
  }
  return undefined
}

function extractSubFormRules(rules: unknown, depth = 0, visited = new WeakSet<object>()): FormCreateRule[] {
  if (!rules) {
    return []
  }
  if (depth > MAX_EXTRACT_DEPTH) {
    return []
  }
  let target: object | undefined
  if (typeof rules === 'object') {
    target = rules as object
    if (visited.has(target)) {
      return []
    }
    visited.add(target)
  }
  const result = extractSubFormRulesSource(rules, depth, visited)
  if (target) {
    visited.delete(target)
  }
  return result
}

function extractSubFormRulesSource(rules: unknown, depth: number, visited: WeakSet<object>): FormCreateRule[] {
  if (Array.isArray(rules)) {
    return rules.flatMap(item => extractSubFormRules(item, depth + 1, visited))
  }
  if (typeof rules !== 'object') {
    return []
  }
  const rule = rules as FormCreateRule
  if (rule.type || rule.field) {
    return [rule]
  }
  return extractSubFormRules(
    pickFirstRuleSource(rule.children, rule.props?.children, rule.props?.rule),
    depth + 1,
    visited,
  )
}
