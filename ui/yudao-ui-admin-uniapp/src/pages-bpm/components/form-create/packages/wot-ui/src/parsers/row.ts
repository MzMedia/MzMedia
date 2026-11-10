import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { parseRuleChildren } from './utils'

const ROW_TYPES = new Set([
  'row',
  'col',
  'grid',
  'elRow',
  'elCol',
  'ElRow',
  'ElCol',
  'fcRow',
  'fcCol',
  'fcGrid',
])

export default {
  name: 'row',
  match(rule: FormCreateRule) {
    return ROW_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    return parseRuleChildren(rule, indexPath, parseRule)
  },
}
