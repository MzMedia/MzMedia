import request from '@/config/axios'

// HRM 员工工资卡 VO
export interface HrmEmployeeSalaryCardVO {
  id?: number // 工资卡编号
  employeeId?: number // 员工编号
  bankCardNumber?: string // 银行卡号
  bankAreaId?: number // 开户地区编号
  bankAreaName?: string // 开户地区名称
  bankName?: string // 银行名称
  bankBranchName?: string // 开户支行名称
  createTime?: Date // 创建时间
}

// 查询员工工资卡
export const getEmployeeSalaryCard = async (employeeId: number) => {
  return await request.get<HrmEmployeeSalaryCardVO>({
    url: '/hrm/employee/salary-card/get',
    params: { employeeId }
  })
}

// 保存员工工资卡
export const saveEmployeeSalaryCard = async (data: HrmEmployeeSalaryCardVO) => {
  return await request.put<number>({ url: '/hrm/employee/salary-card/save', data })
}

// 删除员工工资卡
export const deleteEmployeeSalaryCard = async (employeeId: number) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/salary-card/delete',
    params: { employeeId }
  })
}
