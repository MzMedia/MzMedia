import { http } from '@/http/http'

/** ERP 采购全局统计响应 */
export interface ErpPurchaseSummaryResp {
  todayPrice: number // 今日采购金额
  yesterdayPrice: number // 昨日采购金额
  monthPrice: number // 本月采购金额
  yearPrice: number // 今年采购金额
}

/** ERP 采购时间段统计响应 */
export interface ErpPurchaseTimeSummaryResp {
  time: string // 时间
  price: number // 采购金额
}

/** 获取采购统计 */
export function getPurchaseSummary() {
  return http.get<ErpPurchaseSummaryResp>('/erp/purchase-statistics/summary')
}

/** 获取采购时间段统计 */
export function getPurchaseTimeSummary() {
  return http.get<ErpPurchaseTimeSummaryResp[]>('/erp/purchase-statistics/time-summary')
}
