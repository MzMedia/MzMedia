import type { FormCreateRule } from '../../../../types/typing'
import { getRuleChildren } from '../../../utils/src'
import { INTERNAL_LAYOUT_GAP_TYPE, INTERNAL_LAYOUT_TITLE_TYPE } from '../core/utils'

export type ParseRule = (rule: FormCreateRule, indexPath: string) => FormCreateRule[]

export function parseRuleChildren(rule: FormCreateRule, indexPath: string, parseRule: ParseRule) {
  return getRuleChildren(rule).flatMap((child, index) => parseRule(child, `${indexPath}_${index}`))
}

export function getLayoutTitle(rule: FormCreateRule) {
  const title = rule.title ?? rule.props?.title ?? rule.props?.header ?? rule.props?.label
  if (title === false || title === undefined || title === null) {
    return ''
  }
  const text = String(title)
  return text === rule.type ? '' : text
}

export function createLayoutTitleRule(rule: FormCreateRule, indexPath: string, title: string): FormCreateRule {
  return {
    type: INTERNAL_LAYOUT_TITLE_TYPE,
    title,
    props: {
      sourceType: rule.type,
    },
    __fcId: `${INTERNAL_LAYOUT_TITLE_TYPE}_${indexPath}`,
    __originType: rule.type,
  }
}

export function createLayoutGapRule(rule: FormCreateRule, indexPath: string): FormCreateRule {
  return {
    type: INTERNAL_LAYOUT_GAP_TYPE,
    props: {
      height: rule.props?.height ?? rule.props?.size ?? getStyleValue(rule.props?.style, 'height'),
      sourceType: rule.type,
    },
    __fcId: `${INTERNAL_LAYOUT_GAP_TYPE}_${indexPath}`,
    __originType: rule.type,
  }
}

function getStyleValue(style: unknown, key: string) {
  if (!style || typeof style !== 'object') {
    return undefined
  }
  return (style as Record<string, any>)[key]
}
