import request from '@/config/axios'
import type { HrmEmployeeQuitInfoVO } from '@/api/hrm/employee/quit-info'

// 获得当前员工的离职信息
export const getEmployeeQuitInfo = async () => {
  return await request.get<HrmEmployeeQuitInfoVO>({
    url: '/hrm/portal/employee/quit-info/get'
  })
}
