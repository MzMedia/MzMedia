import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { getRuleChildren } from '../../../utils/src'
import { createLayoutGapRule, parseRuleChildren } from './utils'

const SPACE_TYPES = new Set(['space', 'Space', 'elSpace', 'ElSpace', 'gap', 'Gap', 'div'])

export default {
  name: 'space',
  match(rule: FormCreateRule) {
    return SPACE_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    if (getRuleChildren(rule).length > 0) {
      return parseRuleChildren(rule, indexPath, parseRule)
    }
    return [createLayoutGapRule(rule, indexPath)]
  },
}
