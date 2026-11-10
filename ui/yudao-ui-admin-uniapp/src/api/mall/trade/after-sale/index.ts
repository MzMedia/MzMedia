import type { TradeOrder } from '@/api/mall/trade/order'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 售后商品属性 */
export interface TradeAfterSaleProductProperty {
  propertyId?: number
  propertyName?: string
  valueId?: number
  valueName?: string
}

/** 售后日志 */
export interface TradeAfterSaleLog {
  id?: number
  userId?: number
  userType?: number
  content?: string
  createTime?: string
}

/** 交易售后 */
export interface TradeAfterSale {
  id?: number
  no?: string
  status?: number
  way?: number
  type?: number
  userId?: number
  applyReason?: string
  applyDescription?: string
  applyPicUrls?: string[]
  orderId?: number
  orderNo?: string
  order?: TradeOrder // 关联订单
  orderItemId?: number
  spuId?: number
  spuName?: string
  skuId?: number
  properties?: TradeAfterSaleProductProperty[]
  picUrl?: string
  count?: number
  auditTime?: string
  auditUserId?: number
  auditReason?: string
  refundPrice?: number
  payRefundId?: number
  refundTime?: string
  logisticsId?: number
  logisticsNo?: string
  deliveryTime?: string
  receiveTime?: string
  receiveReason?: string
  createTime?: string
  logs?: TradeAfterSaleLog[]
}

/** 获取交易售后分页列表 */
export function getTradeAfterSalePage(params: PageParam) {
  return http.get<PageResult<TradeAfterSale>>('/trade/after-sale/page', params)
}

/** 获取交易售后详情 */
export function getTradeAfterSale(id: number) {
  return http.get<TradeAfterSale>(`/trade/after-sale/get-detail?id=${id}`)
}

/** 同意售后 */
export function agreeTradeAfterSale(id: number) {
  return http.put<boolean>(`/trade/after-sale/agree?id=${id}`)
}

/** 拒绝售后 */
export function disagreeTradeAfterSale(data: Record<string, any>) {
  return http.put<boolean>('/trade/after-sale/disagree', data)
}

/** 确认收货 */
export function receiveTradeAfterSale(id: number) {
  return http.put<boolean>(`/trade/after-sale/receive?id=${id}`)
}

/** 拒绝收货 */
export function refuseTradeAfterSale(id: number, refuseMemo: string) {
  return http.put<boolean>(`/trade/after-sale/refuse?id=${id}&refuseMemo=${encodeURIComponent(refuseMemo)}`)
}

/** 确认退款 */
export function refundTradeAfterSale(id: number) {
  return http.put<boolean>(`/trade/after-sale/refund?id=${id}`)
}
