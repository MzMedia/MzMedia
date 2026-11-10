import request from '@/config/axios'

// 员工端社保项目 VO
export interface InsuranceSchemeProjectVO {
  schemeProjectId?: number // 社保项目编号
  type?: number // 社保项目类型
  name: string // 社保项目名称
  baseAmount?: number // 默认缴纳基数
  corporateRate?: number // 企业缴纳比例
  personalRate?: number // 个人缴纳比例
  corporateAmount?: number // 企业缴纳金额
  personalAmount?: number // 个人缴纳金额
}

// 员工端社保记录 VO
export interface InsuranceRecordVO {
  id: number // 员工月度社保记录编号
  monthRecordId?: number // 月度社保表编号
  employeeId: number // 员工编号
  schemeId?: number // 社保方案编号
  schemeName?: string // 社保方案名称
  schemeType?: number // 社保方案类型
  schemeCity?: string // 参保城市
  year: number // 年份
  month: number // 月份
  personalInsuranceAmount?: number // 个人社保金额
  personalProvidentFundAmount?: number // 个人公积金金额
  corporateInsuranceAmount?: number // 企业社保金额
  corporateProvidentFundAmount?: number // 企业公积金金额
  status?: number // 参保状态
  createTime?: Date // 创建时间
  projects?: InsuranceSchemeProjectVO[] // 社保项目列表，仅详情返回
}

// 员工端社保记录列表 Request VO
export interface InsuranceRecordListReqVO {
  year?: number // 年份
}

// 获得我的社保记录列表
export const getInsuranceRecordList = async (params?: InsuranceRecordListReqVO) => {
  return await request.get<InsuranceRecordVO[]>({
    url: '/hrm/portal/insurance/record/list',
    params
  })
}

// 获得我的社保记录详情
export const getInsuranceRecord = async (id: number) => {
  return await request.get<InsuranceRecordVO>({
    url: '/hrm/portal/insurance/record/get',
    params: { id }
  })
}
