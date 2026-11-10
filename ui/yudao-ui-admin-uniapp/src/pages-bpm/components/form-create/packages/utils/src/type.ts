export function isObject(value: unknown): value is Record<string, any> {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}

export function hasOwn(value: Record<string, any>, key: string) {
  return Object.prototype.hasOwnProperty.call(value, key)
}

export function isEmptyValue(value: unknown) {
  return value === undefined || value === null || value === '' || (Array.isArray(value) && value.length === 0)
}

export function toArray<T>(value?: T | T[]): T[] {
  if (value === undefined || value === null) {
    return []
  }
  return Array.isArray(value) ? value : [value]
}
