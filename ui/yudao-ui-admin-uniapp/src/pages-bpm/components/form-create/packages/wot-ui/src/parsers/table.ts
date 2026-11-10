import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { parseRuleChildren } from './utils'

const TABLE_TYPES = new Set([
  'table',
  'tbody',
  'thead',
  'tr',
  'td',
  'th',
  'elTable',
  'elTableColumn',
  'ElTable',
  'ElTableColumn',
  'fcTable',
])

export default {
  name: 'table',
  match(rule: FormCreateRule) {
    return TABLE_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    return parseRuleChildren(rule, indexPath, parseRule)
  },
}
