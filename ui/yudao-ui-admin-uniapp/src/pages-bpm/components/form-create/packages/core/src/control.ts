import type {
  FormCreateControl,
  FormCreateFieldState,
  FormCreateRule,
  NormalizedFormCreateRule,
} from '../../../types/typing'
import { isEmptyValue, normalizeRules } from './utils'

type ControlFieldState = Pick<FormCreateFieldState, 'controlDisabled' | 'controlHidden' | 'controlRequired'>

interface ControlRuleResult {
  fieldStates: Record<string, ControlFieldState>
  rules: NormalizedFormCreateRule[]
}

interface DynamicInsertion {
  afterField?: string
  afterId?: string
  beforeField?: string
  rules: NormalizedFormCreateRule[]
}

const MAX_CONTROL_DEPTH = 5

export function applyControlRules(
  rules: NormalizedFormCreateRule[],
  formData: Record<string, any>,
): ControlRuleResult {
  const controlledRules = createControlledRules(rules, formData)
  return {
    fieldStates: createControlFieldStates(controlledRules, formData),
    rules: controlledRules,
  }
}

function createControlledRules(
  rules: NormalizedFormCreateRule[],
  formData: Record<string, any>,
  depth = 0,
): NormalizedFormCreateRule[] {
  if (depth >= MAX_CONTROL_DEPTH) {
    return rules
  }

  const insertions = collectDynamicInsertions(rules, formData, depth)
  if (insertions.length === 0) {
    return rules
  }

  const output: NormalizedFormCreateRule[] = []
  const usedInsertions = new Set<DynamicInsertion>()

  rules.forEach((rule) => {
    appendFieldInsertions(output, insertions, usedInsertions, 'beforeField', rule.field)
    output.push(rule)
    appendFieldInsertions(output, insertions, usedInsertions, 'afterField', rule.field)
    appendFieldInsertions(output, insertions, usedInsertions, 'afterId', rule.__fcId)
  })

  insertions.forEach((insertion) => {
    if (!usedInsertions.has(insertion)) {
      output.push(...insertion.rules)
    }
  })

  return output
}

function collectDynamicInsertions(
  rules: NormalizedFormCreateRule[],
  formData: Record<string, any>,
  depth: number,
) {
  const insertions: DynamicInsertion[] = []
  rules.forEach((rule) => {
    getControlList(rule).forEach((control, controlIndex) => {
      if (!isControlMatched(control, getRuleValue(rule, formData))) {
        return
      }
      const controlRules = getDynamicControlRules(control, rule, controlIndex)
      if (controlRules.length === 0) {
        return
      }
      insertions.push({
        afterField: getStringProp(control.append),
        afterId: control.append || control.prepend ? undefined : rule.__fcId,
        beforeField: getStringProp(control.prepend),
        rules: createControlledRules(controlRules, formData, depth + 1),
      })
    })
  })
  return insertions
}

function createControlFieldStates(
  rules: NormalizedFormCreateRule[],
  formData: Record<string, any>,
) {
  const states: Record<string, ControlFieldState> = {}
  rules.forEach((rule) => {
    getControlList(rule).forEach((control) => {
      const fields = getFieldControlRules(control)
      if (fields.length === 0) {
        return
      }
      const matched = isControlMatched(control, getRuleValue(rule, formData))
      fields.forEach((field) => {
        states[field] = {
          ...(states[field] || {}),
          ...createControlState(control.method, matched),
        }
      })
    })
  })
  return states
}

function getControlList(rule: NormalizedFormCreateRule) {
  if (!rule.control) {
    return []
  }
  return Array.isArray(rule.control) ? rule.control : [rule.control]
}

function getDynamicControlRules(
  control: FormCreateControl,
  sourceRule: NormalizedFormCreateRule,
  controlIndex: number,
) {
  const rules = control.rule?.filter((rule): rule is FormCreateRule => !!rule && typeof rule === 'object') || []
  return normalizeRules(rules.map((rule, ruleIndex) => ({
    ...rule,
    __fcId: rule.__fcId || `${sourceRule.__fcId}_ctrl_${controlIndex}_${rule.field || rule.type || 'rule'}_${ruleIndex}`,
    __originType: rule.__originType || rule.type,
  })))
}

function getFieldControlRules(control: FormCreateControl) {
  return control.rule?.filter((rule): rule is string => typeof rule === 'string' && !!rule) || []
}

function isControlMatched(control: FormCreateControl, value: any) {
  if (typeof control.handle === 'function') {
    try {
      return control.handle(value) === true
    } catch (error) {
      console.warn('[form-create] control.handle execute failed:', error)
      return false
    }
  }
  return compareValue(value, control.value, control.condition || '==')
}

function compareValue(value: any, compare: any, condition: string) {
  switch (condition) {
    case '!=':
    case '<>':
      return !isEqual(value, compare)
    case '>':
      return value > compare
    case '>=':
      return value >= compare
    case '<':
      return value < compare
    case '<=':
      return value <= compare
    case 'on':
      return Array.isArray(value) ? value.some(item => isEqual(item, compare)) : isEqual(value, compare)
    case 'notOn':
      return !compareValue(value, compare, 'on')
    case 'in':
      return Array.isArray(compare) ? compare.some(item => isEqual(value, item)) : false
    case 'notIn':
      return !compareValue(value, compare, 'in')
    case 'between':
      return Array.isArray(compare) ? value >= compare[0] && value <= compare[1] : false
    case 'notBetween':
      return Array.isArray(compare) ? value < compare[0] || value > compare[1] : false
    case 'empty':
      return isEmptyValue(value)
    case 'notEmpty':
      return !isEmptyValue(value)
    case 'pattern':
      return testPattern(value, compare)
    case '==':
    default:
      return isEqual(value, compare)
  }
}

function testPattern(value: any, compare: any) {
  if (compare === undefined || compare === null || value === undefined || value === null) {
    return false
  }
  if (!['string', 'number', 'boolean', 'bigint'].includes(typeof value)) {
    return false
  }
  try {
    const pattern = compare instanceof RegExp ? compare : new RegExp(compare)
    return pattern.test(String(value ?? ''))
  } catch (error) {
    console.warn('[form-create] control.pattern create RegExp failed:', error)
    return false
  }
}

function isEqual(value: any, compare: any) {
  return JSON.stringify(value) === JSON.stringify(changeType(value, compare))
}

function changeType(value: any, compare: any) {
  if (Array.isArray(value)) {
    return Array.isArray(compare) ? compare.map(item => changeType(value[0], item)) : changeType(value[0], compare)
  }
  if (typeof value === 'number') {
    const numericValue = Number(compare)
    return Number.isNaN(numericValue) ? compare : numericValue
  }
  if (typeof value === 'boolean') {
    return compare === true || compare === 'true'
  }
  if (typeof value === 'string' && compare !== undefined && compare !== null) {
    return String(compare)
  }
  return compare
}

function createControlState(method = 'hidden', matched: boolean): ControlFieldState {
  if (method === 'disabled' || method === 'enabled') {
    return { controlDisabled: !matched }
  }
  if (method === 'required') {
    return { controlRequired: matched }
  }
  return { controlHidden: !matched }
}

function getRuleValue(rule: NormalizedFormCreateRule, formData: Record<string, any>) {
  return rule.field ? formData[rule.field] : rule.value
}

function getStringProp(value: unknown) {
  return typeof value === 'string' && value ? value : undefined
}

function appendFieldInsertions(
  output: NormalizedFormCreateRule[],
  insertions: DynamicInsertion[],
  usedInsertions: Set<DynamicInsertion>,
  key: 'afterField' | 'afterId' | 'beforeField',
  value?: string,
) {
  if (!value) {
    return
  }
  insertions.forEach((insertion) => {
    if (!usedInsertions.has(insertion) && insertion[key] === value) {
      output.push(...insertion.rules)
      usedInsertions.add(insertion)
    }
  })
}
