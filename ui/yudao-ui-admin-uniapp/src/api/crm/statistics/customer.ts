import { http } from '@/http/http'

export interface CrmStatisticsCustomerSummaryByDateResp {
  time: string
  customerCreateCount: number
  customerDealCount: number
}

export interface CrmStatisticsCustomerSummaryByUserResp {
  ownerUserName: string
  customerCreateCount: number
  customerDealCount: number
  contractPrice: number
  receivablePrice: number
}

export interface CrmStatisticsFollowUpSummaryByDateResp {
  time: string
  followUpRecordCount: number
  followUpCustomerCount: number
}

export interface CrmStatisticsFollowUpSummaryByUserResp {
  ownerUserName: string
  followupRecordCount: number
  followupCustomerCount: number
}

export interface CrmStatisticsFollowUpSummaryByTypeResp {
  followUpType: string
  followUpRecordCount: number
}

export interface CrmStatisticsCustomerContractSummaryResp {
  customerName: string
  contractName: string
  totalPrice: number
  receivablePrice: number
  customerType: string
  customerSource: string
  ownerUserName: string
  creatorUserName: string
  createTime: Date | string
  orderDate: Date | string
}

export interface CrmStatisticsPoolSummaryByDateResp {
  time: string
  customerPutCount: number
  customerTakeCount: number
}

export interface CrmStatisticsPoolSummaryByUserResp {
  ownerUserName: string
  customerPutCount: number
  customerTakeCount: number
}

export interface CrmStatisticsCustomerDealCycleByDateResp {
  time: string
  customerDealCycle: number
}

export interface CrmStatisticsCustomerDealCycleByUserResp {
  ownerUserName: string
  customerDealCycle: number
  customerDealCount: number
}

export interface CrmStatisticsCustomerDealCycleByAreaResp {
  areaName: string
  customerDealCycle: number
  customerDealCount: number
}

export interface CrmStatisticsCustomerDealCycleByProductResp {
  productName: string
  customerDealCycle: number
  customerDealCount: number
}

/** 获取客户总量分析，按日期 */
export function getCustomerSummaryByDate(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerSummaryByDateResp[]>('/crm/statistics-customer/get-customer-summary-by-date', params)
}

/** 获取客户总量分析，按用户 */
export function getCustomerSummaryByUser(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerSummaryByUserResp[]>('/crm/statistics-customer/get-customer-summary-by-user', params)
}

/** 获取客户跟进次数分析，按日期 */
export function getFollowUpSummaryByDate(params: Record<string, any>) {
  return http.get<CrmStatisticsFollowUpSummaryByDateResp[]>('/crm/statistics-customer/get-follow-up-summary-by-date', params)
}

/** 获取客户跟进次数分析，按用户 */
export function getFollowUpSummaryByUser(params: Record<string, any>) {
  return http.get<CrmStatisticsFollowUpSummaryByUserResp[]>('/crm/statistics-customer/get-follow-up-summary-by-user', params)
}

/** 获取客户跟进方式统计数 */
export function getFollowUpSummaryByType(params: Record<string, any>) {
  return http.get<CrmStatisticsFollowUpSummaryByTypeResp[]>('/crm/statistics-customer/get-follow-up-summary-by-type', params)
}

/** 获取合同摘要信息 */
export function getContractSummary(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerContractSummaryResp[]>('/crm/statistics-customer/get-contract-summary', params)
}

/** 获取客户公海分析，按日期 */
export function getPoolSummaryByDate(params: Record<string, any>) {
  return http.get<CrmStatisticsPoolSummaryByDateResp[]>('/crm/statistics-customer/get-pool-summary-by-date', params)
}

/** 获取客户公海分析，按用户 */
export function getPoolSummaryByUser(params: Record<string, any>) {
  return http.get<CrmStatisticsPoolSummaryByUserResp[]>('/crm/statistics-customer/get-pool-summary-by-user', params)
}

/** 获取客户成交周期，按日期 */
export function getCustomerDealCycleByDate(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerDealCycleByDateResp[]>('/crm/statistics-customer/get-customer-deal-cycle-by-date', params)
}

/** 获取客户成交周期，按用户 */
export function getCustomerDealCycleByUser(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerDealCycleByUserResp[]>('/crm/statistics-customer/get-customer-deal-cycle-by-user', params)
}

/** 获取客户成交周期，按地区 */
export function getCustomerDealCycleByArea(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerDealCycleByAreaResp[]>('/crm/statistics-customer/get-customer-deal-cycle-by-area', params)
}

/** 获取客户成交周期，按产品 */
export function getCustomerDealCycleByProduct(params: Record<string, any>) {
  return http.get<CrmStatisticsCustomerDealCycleByProductResp[]>('/crm/statistics-customer/get-customer-deal-cycle-by-product', params)
}
