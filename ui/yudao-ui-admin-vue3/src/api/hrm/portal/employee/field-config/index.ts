import request from '@/config/axios'
import type { HrmEmployeeFieldConfigVO } from '@/api/hrm/employee/config'

// 获得当前员工的档案字段配置
export const getEmployeeFieldConfigList = async () => {
  return await request.get<HrmEmployeeFieldConfigVO[]>({
    url: '/hrm/portal/employee/field-config/list'
  })
}
