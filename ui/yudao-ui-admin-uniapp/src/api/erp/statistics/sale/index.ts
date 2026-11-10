import { http } from '@/http/http'

/** ERP 销售全局统计响应 */
export interface ErpSaleSummaryResp {
  todayPrice: number // 今日销售金额
  yesterdayPrice: number // 昨日销售金额
  monthPrice: number // 本月销售金额
  yearPrice: number // 今年销售金额
}

/** ERP 销售时间段统计响应 */
export interface ErpSaleTimeSummaryResp {
  time: string // 时间
  price: number // 销售金额
}

/** 获取销售统计 */
export function getSaleSummary() {
  return http.get<ErpSaleSummaryResp>('/erp/sale-statistics/summary')
}

/** 获取销售时间段统计 */
export function getSaleTimeSummary() {
  return http.get<ErpSaleTimeSummaryResp[]>('/erp/sale-statistics/time-summary')
}
