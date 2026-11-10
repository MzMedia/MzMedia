import request from '@/config/axios'

// 员工端工资条薪资项 VO
export interface SalarySlipOptionVO {
  name: string // 薪资项名称
  type?: number // 薪资项类型
  code?: number // 薪资项编码
  value?: number // 薪资项金额
  remark?: string // 备注
  sort?: number // 排序
  children?: SalarySlipOptionVO[] // 子薪资项
}

// 员工端工资条 VO
export interface SalarySlipVO {
  id: number // 工资条编号
  sendRecordId?: number // 工资条批次编号
  monthEmployeeRecordId?: number // 员工月度工资记录编号
  employeeId: number // 员工编号
  year: number // 年份
  month: number // 月份
  readStatus?: number // 阅读状态
  realPaySalary?: number // 实发工资
  remark?: string // 备注
  createTime?: Date // 创建时间
  options: SalarySlipOptionVO[] // 薪资项列表
}

// 员工端工资条列表 Request VO
export interface SalarySlipListReqVO {
  startMonth?: string // 开始月份
  endMonth?: string // 结束月份
  orderType?: number // 排序字段类型
  order?: number // 排序方式
}

// 员工端未读工资条概况 VO
export interface SalarySlipUnreadSummaryVO {
  unreadCount: number // 未读工资条数量
  reminder?: string // 最新未读工资条提醒
}

// 获得我的工资条列表
export const getSalarySlipList = async (params?: SalarySlipListReqVO) => {
  return await request.get<SalarySlipVO[]>({ url: '/hrm/portal/salary/slip/list', params })
}

// 获得我的未读工资条概况
export const getUnreadSalarySlipSummary = async () => {
  return await request.get<SalarySlipUnreadSummaryVO>({
    url: '/hrm/portal/salary/slip/unread-summary'
  })
}

// 标记我的工资条为已读
export const markSalarySlipRead = async (ids: number[]) => {
  return await request.put<boolean>({
    url: '/hrm/portal/salary/slip/read',
    params: { ids: ids.join(',') }
  })
}
