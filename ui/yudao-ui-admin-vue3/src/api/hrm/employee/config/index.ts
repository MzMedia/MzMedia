import request from '@/config/axios'

// HRM 员工字段配置 VO
export interface HrmEmployeeFieldConfigVO {
  name: string // 字段名称
  title: string // 字段标题
  groupName: string // 字段分组名称
  visible: boolean // 是否显示
  editable?: boolean // 是否允许员工编辑
  visibleLocked: boolean // 是否锁定显示
  editableLocked: boolean // 是否锁定编辑
}

// HRM 员工字段显示配置 VO
export interface HrmEmployeeFieldVisibleVO {
  name: string // 字段名称
  visible: boolean // 是否显示
}

// 查询新建员工字段配置
export const getEmployeeCreateFieldConfigList = async (entryStatus: number) => {
  return await request.get<HrmEmployeeFieldConfigVO[]>({
    url: '/hrm/employee/config/create-field/list',
    params: { entryStatus }
  })
}

// 保存新建员工字段配置
export const saveEmployeeCreateFieldConfig = async (
  entryStatus: number,
  fields: HrmEmployeeFieldVisibleVO[]
) => {
  return await request.put<boolean>({
    url: '/hrm/employee/config/create-field/save',
    data: {
      entryStatus,
      fields: fields.map(({ name, visible }) => ({ name, visible }))
    }
  })
}

// 查询员工档案字段配置
export const getEmployeeArchiveFieldConfigList = async () => {
  return await request.get<HrmEmployeeFieldConfigVO[]>({
    url: '/hrm/employee/config/archive-field/list'
  })
}

// 保存员工档案字段配置
export const saveEmployeeArchiveFieldConfig = async (fields: HrmEmployeeFieldConfigVO[]) => {
  return await request.put<boolean>({
    url: '/hrm/employee/config/archive-field/save',
    data: {
      fields: fields.map(({ name, visible, editable }) => ({
        name,
        visible,
        editable
      }))
    }
  })
}
