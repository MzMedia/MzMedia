import { isObject } from './type'

export function deepMerge<T extends Record<string, any>>(...sources: Array<Partial<T> | undefined>): T {
  const target: Record<string, any> = {}
  sources.forEach((source) => {
    if (!source) {
      return
    }
    Object.entries(source).forEach(([key, value]) => {
      if (isObject(value) && isObject(target[key])) {
        target[key] = deepMerge(target[key], value)
      } else if (Array.isArray(value)) {
        target[key] = [...value]
      } else {
        target[key] = value
      }
    })
  })
  return target as T
}
