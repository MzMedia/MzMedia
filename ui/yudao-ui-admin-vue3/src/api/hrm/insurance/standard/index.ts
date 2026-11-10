import request from '@/config/axios'

// 标准参保类型 VO
export interface InsuranceStandardTypeVO {
  code: string // 参保方案编码
  name: string // 参保方案名称
}

// 标准参保项目 VO
export interface InsuranceStandardProjectVO {
  type: number // 项目类型
  name: string // 项目名称
  baseAmount?: number // 缴纳基数
  corporateRate?: number // 公司缴纳比例
  personalRate?: number // 个人缴纳比例
  corporateAmount?: number // 公司缴纳金额
  personalAmount?: number // 个人缴纳金额
}

// 查询标准参保类型列表
export const getInsuranceStandardTypeList = async (areaId: number) => {
  return await request.get<InsuranceStandardTypeVO[]>({
    url: '/hrm/insurance/standard/type-list',
    params: { areaId }
  })
}

// 查询标准参保项目列表
export const getInsuranceStandardProjectList = async (params: {
  areaId: number
  typeCode: string
}) => {
  return await request.get<InsuranceStandardProjectVO[]>({
    url: '/hrm/insurance/standard/project-list',
    params
  })
}
