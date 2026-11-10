import request from '@/config/axios'
import type { HrmEmployeeTrainingExperienceVO } from '@/api/hrm/employee/training-experience'

// 获得当前员工的培训经历列表
export const getEmployeeTrainingExperienceList = async () => {
  return await request.get<HrmEmployeeTrainingExperienceVO[]>({
    url: '/hrm/portal/employee/training-experience/list'
  })
}
