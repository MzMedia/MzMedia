import request from '@/config/axios'

// HRM 员工教育经历 VO
export interface HrmEmployeeEducationExperienceVO {
  id?: number // 教育经历编号
  employeeId?: number // 员工编号
  education?: number // 学历
  graduateSchool?: string // 毕业院校
  major?: string // 专业
  admissionTime?: number // 入学日期
  graduationTime?: number // 毕业日期
  teachingMethods?: number // 教学方式
  firstDegree?: boolean // 是否第一学历
  sort?: number // 排序
  createTime?: Date // 创建时间
}

// 查询员工教育经历列表
export const getEmployeeEducationExperienceList = async (employeeId: number) => {
  return await request.get<HrmEmployeeEducationExperienceVO[]>({
    url: '/hrm/employee/education-experience/list',
    params: { employeeId }
  })
}

// 新增员工教育经历
export const createEmployeeEducationExperience = async (data: HrmEmployeeEducationExperienceVO) => {
  return await request.post<number>({ url: '/hrm/employee/education-experience/create', data })
}

// 修改员工教育经历
export const updateEmployeeEducationExperience = async (data: HrmEmployeeEducationExperienceVO) => {
  return await request.put<boolean>({ url: '/hrm/employee/education-experience/update', data })
}

// 删除员工教育经历
export const deleteEmployeeEducationExperience = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/education-experience/delete',
    params: { id }
  })
}
