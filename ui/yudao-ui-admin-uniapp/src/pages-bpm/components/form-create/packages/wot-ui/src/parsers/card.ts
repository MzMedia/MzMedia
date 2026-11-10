import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { createLayoutTitleRule, getLayoutTitle, parseRuleChildren } from './utils'

const CARD_TYPES = new Set(['card', 'elCard', 'ElCard'])

export default {
  name: 'card',
  match(rule: FormCreateRule) {
    return CARD_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    const children = parseRuleChildren(rule, indexPath, parseRule)
    const title = getLayoutTitle(rule)
    return title ? [createLayoutTitleRule(rule, indexPath, title), ...children] : children
  },
}
