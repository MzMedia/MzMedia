import request from '@/config/axios'

// HRM 员工参保信息 VO
export interface HrmInsuranceEmployeeInfoVO {
  id?: number // 员工参保信息编号
  employeeId?: number // 员工编号
  firstSocialSecurity?: boolean // 是否本地首次缴纳社保
  firstAccumulationFund?: boolean // 是否本地首次缴纳公积金
  socialSecurityNumber?: string // 社保账号
  accumulationFundNumber?: string // 公积金账号
  socialSecurityStartMonth?: number // 社保起缴月份
  schemeId?: number // 社保方案编号
  schemeName?: string // 社保方案名称
  createTime?: Date // 创建时间
}

// 查询员工参保信息
export const getInsuranceEmployeeInfo = async (employeeId: number) => {
  return await request.get<HrmInsuranceEmployeeInfoVO>({
    url: '/hrm/insurance/employee-info/get',
    params: { employeeId }
  })
}

// 保存员工参保信息
export const saveInsuranceEmployeeInfo = async (data: HrmInsuranceEmployeeInfoVO) => {
  return await request.put<number>({ url: '/hrm/insurance/employee-info/save', data })
}

// 更新员工参保方案
export const updateEmployeeScheme = async (employeeId: number, schemeId: number) => {
  return await request.put<boolean>({
    url: '/hrm/insurance/employee-info/update-scheme',
    data: { employeeId, schemeId }
  })
}
