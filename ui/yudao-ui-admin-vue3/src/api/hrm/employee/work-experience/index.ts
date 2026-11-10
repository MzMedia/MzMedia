import request from '@/config/axios'

// HRM 员工工作经历 VO
export interface HrmEmployeeWorkExperienceVO {
  id?: number // 工作经历编号
  employeeId?: number // 员工编号
  workUnit?: string // 工作单位
  postName?: string // 职务
  startTime?: number // 工作开始日期
  endTime?: number // 工作结束日期
  reason?: string // 离职原因
  witnessName?: string // 证明人
  witnessPhone?: string // 证明人手机号
  remark?: string // 工作备注
  sort?: number // 排序
  createTime?: Date // 创建时间
}

// 查询员工工作经历列表
export const getEmployeeWorkExperienceList = async (employeeId: number) => {
  return await request.get<HrmEmployeeWorkExperienceVO[]>({
    url: '/hrm/employee/work-experience/list',
    params: { employeeId }
  })
}

// 新增员工工作经历
export const createEmployeeWorkExperience = async (data: HrmEmployeeWorkExperienceVO) => {
  return await request.post<number>({ url: '/hrm/employee/work-experience/create', data })
}

// 修改员工工作经历
export const updateEmployeeWorkExperience = async (data: HrmEmployeeWorkExperienceVO) => {
  return await request.put<boolean>({ url: '/hrm/employee/work-experience/update', data })
}

// 删除员工工作经历
export const deleteEmployeeWorkExperience = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/work-experience/delete',
    params: { id }
  })
}
