import { http } from '@/http/http'

export interface CrmStatisticCustomerBaseResp {
  customerCount: number
  dealCount: number
  dealPortion: string | number
}

export interface CrmStatisticCustomerIndustryResp extends CrmStatisticCustomerBaseResp {
  industryId: number
  industryPortion: string | number
}

export interface CrmStatisticCustomerSourceResp extends CrmStatisticCustomerBaseResp {
  source: number
  sourcePortion: string | number
}

export interface CrmStatisticCustomerLevelResp extends CrmStatisticCustomerBaseResp {
  level: number
  levelPortion: string | number
}

export interface CrmStatisticCustomerAreaResp extends CrmStatisticCustomerBaseResp {
  areaId: number
  areaName: string
  areaPortion: string | number
}

/** 获取客户行业统计数据 */
export function getCustomerIndustry(params: Record<string, any>) {
  return http.get<CrmStatisticCustomerIndustryResp[]>('/crm/statistics-portrait/get-customer-industry-summary', params)
}

/** 获取客户来源统计数据 */
export function getCustomerSource(params: Record<string, any>) {
  return http.get<CrmStatisticCustomerSourceResp[]>('/crm/statistics-portrait/get-customer-source-summary', params)
}

/** 获取客户级别统计数据 */
export function getCustomerLevel(params: Record<string, any>) {
  return http.get<CrmStatisticCustomerLevelResp[]>('/crm/statistics-portrait/get-customer-level-summary', params)
}

/** 获取客户地区统计数据 */
export function getCustomerArea(params: Record<string, any>) {
  return http.get<CrmStatisticCustomerAreaResp[]>('/crm/statistics-portrait/get-customer-area-summary', params)
}
