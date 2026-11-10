import request from '@/config/axios'

// HRM 员工联系人 VO
export interface HrmEmployeeContactVO {
  id?: number // 联系人编号
  employeeId?: number // 员工编号
  name?: string // 联系人姓名
  relation?: string // 关系
  phone?: string // 联系人电话
  workUnit?: string // 联系人工作单位
  postName?: string // 联系人职务
  address?: string // 联系人地址
  sort?: number // 排序
  createTime?: Date // 创建时间
}

// 查询员工联系人列表
export const getEmployeeContactList = async (employeeId: number) => {
  return await request.get<HrmEmployeeContactVO[]>({
    url: '/hrm/employee/contact/list',
    params: { employeeId }
  })
}

// 新增员工联系人
export const createEmployeeContact = async (data: HrmEmployeeContactVO) => {
  return await request.post<number>({ url: '/hrm/employee/contact/create', data })
}

// 修改员工联系人
export const updateEmployeeContact = async (data: HrmEmployeeContactVO) => {
  return await request.put<boolean>({ url: '/hrm/employee/contact/update', data })
}

// 删除员工联系人
export const deleteEmployeeContact = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/employee/contact/delete', params: { id } })
}
