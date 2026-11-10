import type { FormCreateRule } from '../../../../types/typing'

export default {
  name: 'hidden',
  parse(rule: FormCreateRule) {
    return {
      ...rule,
      hidden: true,
    }
  },
}
