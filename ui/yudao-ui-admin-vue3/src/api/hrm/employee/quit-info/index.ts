import request from '@/config/axios'

// HRM 员工离职信息 VO
export interface HrmEmployeeQuitInfoVO {
  id?: number // 离职信息编号
  employeeId?: number // 员工编号
  planQuitTime?: number // 计划离职时间
  applyQuitTime?: number // 申请离职时间
  salarySettlementTime?: number // 薪资结算时间
  type?: number // 离职类型
  reason?: number // 离职原因
  remark?: string // 备注
  oldEmployeeStatus?: number // 原员工状态
  createTime?: Date // 创建时间
}

// 查询员工离职信息
export const getEmployeeQuitInfo = async (employeeId: number) => {
  return await request.get<HrmEmployeeQuitInfoVO>({
    url: '/hrm/employee/quit-info/get',
    params: { employeeId }
  })
}
