export type CustomSelectValue = string | number | boolean

export interface CustomSelectOption {
  label: string
  value: CustomSelectValue
  avatar?: string
  description?: string
  disabled?: boolean
  displayLabel?: string
  level?: number
  path?: string
  raw?: Record<string, any>
}

export interface FlattenedDept {
  id?: number
  name: string
  parentId?: number
  path: string
  level: number
  raw: Record<string, any>
}
