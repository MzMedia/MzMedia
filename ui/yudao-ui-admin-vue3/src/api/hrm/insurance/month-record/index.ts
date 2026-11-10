import request from '@/config/axios'

// 月度社保表 VO
export interface InsuranceMonthRecordVO {
  id?: number // 月度社保表编号
  title?: string // 标题
  year?: number // 年份
  month?: number // 月份
  insuredEmployeeCount?: number // 参保人数
  stoppedEmployeeCount?: number // 停止参保人数
  status?: number // 状态
  personalInsuranceAmount?: number // 个人社保金额
  personalProvidentFundAmount?: number // 个人公积金金额
  corporateInsuranceAmount?: number // 公司社保金额
  corporateProvidentFundAmount?: number // 公司公积金金额
  createTime?: Date // 创建时间
}

// 月度社保表创建 Request VO
export interface InsuranceMonthRecordCreateReqVO {
  year: number // 年份
  month: number // 月份
}

// 创建首月社保表
export const createFirstInsuranceMonthRecord = async (data: InsuranceMonthRecordCreateReqVO) => {
  return await request.post<number>({ url: '/hrm/insurance/month-record/create-first', data })
}

// 新建次月社保表
export const createNextInsuranceMonthRecord = async () => {
  return await request.post<number>({ url: '/hrm/insurance/month-record/create-next' })
}

// 删除月度社保表
export const deleteInsuranceMonthRecord = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/insurance/month-record/delete?id=' + id })
}

// 查询月度社保表详情
export const getInsuranceMonthRecord = async (id: number) => {
  return await request.get<InsuranceMonthRecordVO>({
    url: '/hrm/insurance/month-record/get?id=' + id
  })
}

// 查询最近月度社保表
export const getLastInsuranceMonthRecord = async () => {
  return await request.get<InsuranceMonthRecordVO>({ url: '/hrm/insurance/month-record/last' })
}

// 查询月度社保表列表
export const getInsuranceMonthRecordList = async (year?: number) => {
  return await request.get<InsuranceMonthRecordVO[]>({
    url: '/hrm/insurance/month-record/list',
    params: { year }
  })
}
