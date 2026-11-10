import request from '@/config/axios'

// HRM 员工培训经历 VO
export interface HrmEmployeeTrainingExperienceVO {
  id?: number // 培训经历编号
  employeeId?: number // 员工编号
  course?: string // 培训课程
  organizationName?: string // 培训机构名称
  startTime?: number // 培训开始日期
  endTime?: number // 培训结束日期
  duration?: string // 培训时长
  result?: string // 培训成绩
  certificateName?: string // 培训证书名称
  remark?: string // 备注
  sort?: number // 排序
  createTime?: Date // 创建时间
}

// 查询员工培训经历列表
export const getEmployeeTrainingExperienceList = async (employeeId: number) => {
  return await request.get<HrmEmployeeTrainingExperienceVO[]>({
    url: '/hrm/employee/training-experience/list',
    params: { employeeId }
  })
}

// 新增员工培训经历
export const createEmployeeTrainingExperience = async (data: HrmEmployeeTrainingExperienceVO) => {
  return await request.post<number>({ url: '/hrm/employee/training-experience/create', data })
}

// 修改员工培训经历
export const updateEmployeeTrainingExperience = async (data: HrmEmployeeTrainingExperienceVO) => {
  return await request.put<boolean>({ url: '/hrm/employee/training-experience/update', data })
}

// 删除员工培训经历
export const deleteEmployeeTrainingExperience = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/training-experience/delete',
    params: { id }
  })
}
