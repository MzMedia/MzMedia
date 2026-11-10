import request from '@/config/axios'

// 社保方案项目 VO
export interface InsuranceSchemeProjectVO {
  id?: number // 社保方案项目编号
  schemeId?: number // 社保方案编号
  type?: number // 项目类型
  name?: string // 项目名称
  baseAmount?: number // 缴纳基数
  corporateRate?: number // 公司缴纳比例
  personalRate?: number // 个人缴纳比例
  corporateAmount?: number // 公司缴纳金额
  personalAmount?: number // 个人缴纳金额
  createTime?: Date // 创建时间
}

// 社保方案 VO
export interface InsuranceSchemeVO {
  id?: number // 社保方案编号
  name: string // 方案名称
  areaId?: number // 参保地区编号
  areaName?: string // 参保地区
  householdType?: string // 户籍类型
  type?: number // 方案类型
  projectList?: InsuranceSchemeProjectVO[] // 全部社保项目
  socialSecurityProjectList?: InsuranceSchemeProjectVO[] // 社保项目
  providentFundProjectList?: InsuranceSchemeProjectVO[] // 公积金项目
  personalInsuranceAmount?: number // 个人社保金额
  corporateInsuranceAmount?: number // 公司社保金额
  personalProvidentFundAmount?: number // 个人公积金金额
  corporateProvidentFundAmount?: number // 公司公积金金额
  useCount?: number // 使用人数
  monthRecordCount?: number // 历史月记录数
  createTime?: Date // 创建时间
}

// 创建社保方案
export const createInsuranceScheme = async (data: InsuranceSchemeVO) => {
  return await request.post<number>({ url: '/hrm/insurance/scheme/create', data })
}

// 修改社保方案
export const updateInsuranceScheme = async (data: InsuranceSchemeVO) => {
  return await request.put<boolean>({ url: '/hrm/insurance/scheme/update', data })
}

// 删除社保方案
export const deleteInsuranceScheme = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/insurance/scheme/delete?id=' + id })
}

// 查询社保方案详情
export const getInsuranceScheme = async (id: number) => {
  return await request.get<InsuranceSchemeVO>({ url: '/hrm/insurance/scheme/get?id=' + id })
}

// 查询社保方案列表
export const getInsuranceSchemeList = async () => {
  return await request.get<InsuranceSchemeVO[]>({ url: '/hrm/insurance/scheme/list' })
}

// 查询社保方案精简列表
export const getInsuranceSchemeSimpleList = async () => {
  return await request.get<InsuranceSchemeVO[]>({ url: '/hrm/insurance/scheme/simple-list' })
}
