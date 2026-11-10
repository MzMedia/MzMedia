import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { createLayoutTitleRule, getLayoutTitle, parseRuleChildren } from './utils'

const COLLAPSE_TYPES = new Set(['collapse', 'Collapse', 'elCollapse', 'ElCollapse', 'el-collapse'])
const COLLAPSE_ITEM_TYPES = new Set(['collapseItem', 'CollapseItem', 'elCollapseItem', 'ElCollapseItem', 'el-collapse-item'])

export default {
  name: 'collapse',
  match(rule: FormCreateRule) {
    return COLLAPSE_TYPES.has(rule.type) || COLLAPSE_ITEM_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    const children = parseRuleChildren(rule, indexPath, parseRule)
    if (COLLAPSE_TYPES.has(rule.type)) {
      return children
    }
    const title = getLayoutTitle(rule)
    return title ? [createLayoutTitleRule(rule, indexPath, title), ...children] : children
  },
}
