import { http } from '@/http/http'

export interface StatisticsRankResp {
  count: number
  nickname: string
  deptName: string
}

/** 获得合同排行榜 */
export function getContractPriceRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-contract-price-rank', params)
}

/** 获得回款排行榜 */
export function getReceivablePriceRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-receivable-price-rank', params)
}

/** 获得签约合同排行 */
export function getContractCountRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-contract-count-rank', params)
}

/** 获得产品销量排行 */
export function getProductSalesRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-product-sales-rank', params)
}

/** 获得新增客户数排行 */
export function getCustomerCountRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-customer-count-rank', params)
}

/** 获得新增联系人数排行 */
export function getContactsCountRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-contacts-count-rank', params)
}

/** 获得跟进次数排行 */
export function getFollowCountRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-follow-count-rank', params)
}

/** 获得跟进客户数排行 */
export function getFollowCustomerCountRank(params: Record<string, any>) {
  return http.get<StatisticsRankResp[]>('/crm/statistics-rank/get-follow-customer-count-rank', params)
}
