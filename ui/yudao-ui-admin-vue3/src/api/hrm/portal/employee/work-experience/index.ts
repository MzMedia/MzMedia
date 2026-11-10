import request from '@/config/axios'
import type { HrmEmployeeWorkExperienceVO } from '@/api/hrm/employee/work-experience'

// 获得当前员工的工作经历列表
export const getEmployeeWorkExperienceList = async () => {
  return await request.get<HrmEmployeeWorkExperienceVO[]>({
    url: '/hrm/portal/employee/work-experience/list'
  })
}
