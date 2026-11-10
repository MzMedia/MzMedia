import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { isLayoutGroupTypeName } from '../../../core/src'
import { createLayoutTitleRule, getLayoutTitle, parseRuleChildren } from './utils'

export default {
  name: 'group',
  match(rule: FormCreateRule) {
    return isLayoutGroupTypeName(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    const children = getGroupChildren(rule, indexPath, parseRule)
    const title = getLayoutTitle(rule)
    return title ? [createLayoutTitleRule(rule, indexPath, title), ...children] : children
  },
}

function getGroupChildren(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
  const children = parseRuleChildren(rule, indexPath, parseRule)
  if (children.length > 0) {
    return children
  }
  return extractRules(rule.props?.rule).flatMap((child, index) => parseRule(child, `${indexPath}_${index}`))
}

function extractRules(rules: unknown): FormCreateRule[] {
  if (!rules) {
    return []
  }
  if (Array.isArray(rules)) {
    return rules.flatMap(item => extractRules(item))
  }
  if (typeof rules !== 'object') {
    return []
  }
  const rule = rules as FormCreateRule
  if (rule.type || rule.field) {
    return [rule]
  }
  return extractRules(rule.children || rule.props?.children || rule.props?.rule)
}
