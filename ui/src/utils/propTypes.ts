import { CSSProperties } from 'vue';
import VueTypes, { createTypes, toValidableType, VueTypeValidableDef, VueTypesInterface } from 'vue-types';

type PropTypes = VueTypesInterface & {
  readonly style: VueTypeValidableDef<CSSProperties>;
};

const propTypes = createTypes({
  func: undefined,
  bool: undefined,
  string: undefined,
  number: undefined,
  object: undefined,
  integer: undefined
}) as PropTypes;

export default class ProjectTypes extends VueTypes {
  static get style() {
    return toValidableType('style', {
      type: [String, Object],
      default: undefined
    });
  }
}
export { propTypes };
