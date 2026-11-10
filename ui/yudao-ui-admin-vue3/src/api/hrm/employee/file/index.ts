import request from '@/config/axios'

// HRM 员工材料附件 VO
export interface HrmEmployeeFileVO {
  id?: number // 附件编号
  employeeId: number // 员工编号
  type: number // 附件类型
  url: string // 附件地址
  createTime?: Date // 创建时间
}

// 查询员工材料附件列表
export const getEmployeeFileList = async (employeeId: number) => {
  return await request.get<HrmEmployeeFileVO[]>({
    url: '/hrm/employee/file/list',
    params: { employeeId }
  })
}

// 保存员工材料附件
export const saveEmployeeFiles = async (data: {
  employeeId: number
  type: number
  fileUrls: string[]
}) => {
  return await request.put<boolean>({ url: '/hrm/employee/file/save', data })
}
