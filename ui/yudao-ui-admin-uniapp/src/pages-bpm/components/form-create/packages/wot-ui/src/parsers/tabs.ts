import type { FormCreateRule } from '../../../../types/typing'
import type { ParseRule } from './utils'
import { createLayoutTitleRule, getLayoutTitle, parseRuleChildren } from './utils'

const TABS_TYPES = new Set(['tabs', 'Tabs', 'elTabs', 'ElTabs', 'el-tabs'])
const TAB_PANE_TYPES = new Set(['tabPane', 'TabPane', 'elTabPane', 'ElTabPane', 'el-tab-pane'])

export default {
  name: 'tabs',
  match(rule: FormCreateRule) {
    return TABS_TYPES.has(rule.type) || TAB_PANE_TYPES.has(rule.type)
  },
  render(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
    const children = parseRuleChildren(rule, indexPath, parseRule)
    if (TABS_TYPES.has(rule.type)) {
      return children
    }
    const title = getLayoutTitle(rule)
    return title ? [createLayoutTitleRule(rule, indexPath, title), ...children] : children
  },
}
