import type { FormCreateOption } from '../../../../types/typing'
import { getConfig as getCoreConfig } from '../../../core/src'
import { deepMerge } from '../../../utils/src'

export default function getConfig(option?: FormCreateOption): FormCreateOption {
  return deepMerge<FormCreateOption>(getCoreConfig(), option || {})
}
