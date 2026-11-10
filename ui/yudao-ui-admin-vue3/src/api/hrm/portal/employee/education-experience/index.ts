import request from '@/config/axios'
import type { HrmEmployeeEducationExperienceVO } from '@/api/hrm/employee/education-experience'

// 获得当前员工的教育经历列表
export const getEmployeeEducationExperienceList = async () => {
  return await request.get<HrmEmployeeEducationExperienceVO[]>({
    url: '/hrm/portal/employee/education-experience/list'
  })
}
