import request from '@/config/axios'
import type { HrmEmployeeCertificateVO } from '@/api/hrm/employee/certificate'

// 获得当前员工的证书列表
export const getEmployeeCertificateList = async () => {
  return await request.get<HrmEmployeeCertificateVO[]>({
    url: '/hrm/portal/employee/certificate/list'
  })
}
