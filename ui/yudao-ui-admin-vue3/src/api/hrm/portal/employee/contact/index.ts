import request from '@/config/axios'
import type { HrmEmployeeContactVO } from '@/api/hrm/employee/contact'

// 获得当前员工的联系人列表
export const getEmployeeContactList = async () => {
  return await request.get<HrmEmployeeContactVO[]>({
    url: '/hrm/portal/employee/contact/list'
  })
}
