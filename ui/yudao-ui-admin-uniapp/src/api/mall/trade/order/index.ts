import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 订单项属性 */
export interface TradeOrderItemProperty {
  propertyId?: number
  propertyName?: string
  valueId?: number
  valueName?: string
}

/** 订单项 */
export interface TradeOrderItem {
  id?: number
  userId?: number
  orderId?: number
  spuId?: number
  spuName?: string
  skuId?: number
  picUrl?: string
  count?: number
  price?: number
  discountPrice?: number
  payPrice?: number
  orderPartPrice?: number
  orderDividePrice?: number
  afterSaleStatus?: number
  properties?: TradeOrderItemProperty[]
}

/** 订单日志 */
export interface TradeOrderLog {
  content?: string
  createTime?: string
  userType?: number
}

/** 交易订单 */
export interface TradeOrder {
  id?: number
  no?: string
  createTime?: string
  type?: number
  terminal?: number
  userId?: number
  userIp?: string
  userRemark?: string
  status?: number
  productCount?: number
  finishTime?: string
  cancelTime?: string
  cancelType?: number
  remark?: string
  payOrderId?: number
  payStatus?: boolean
  payTime?: string
  payChannelCode?: string
  totalPrice?: number
  discountPrice?: number
  deliveryPrice?: number
  adjustPrice?: number
  payPrice?: number
  deliveryType?: number
  pickUpStoreId?: number
  pickUpVerifyCode?: string
  deliveryTemplateId?: number
  logisticsId?: number
  logisticsNo?: string
  deliveryTime?: string
  receiveTime?: string
  receiverName?: string
  receiverMobile?: string
  receiverPostCode?: number
  receiverAreaId?: number
  receiverAreaName?: string
  receiverDetailAddress?: string
  afterSaleStatus?: number
  refundPrice?: number
  couponId?: number
  couponPrice?: number
  pointPrice?: number
  vipPrice?: number
  items?: TradeOrderItem[]
  user?: {
    id?: number
    nickname?: string
    avatar?: string
  }
  brokerageUser?: {
    id?: number
    nickname?: string
    avatar?: string
  }
  logs?: TradeOrderLog[]
}

/** 交易订单统计 */
export interface TradeOrderSummary {
  orderCount?: number
  orderPayPrice?: string
  afterSaleCount?: number
  afterSalePrice?: string
}

/** 获取交易订单分页列表 */
export function getTradeOrderPage(params: PageParam) {
  return http.get<PageResult<TradeOrder>>('/trade/order/page', params)
}

/** 获取交易订单统计 */
export function getTradeOrderSummary(params: Record<string, any>) {
  return http.get<TradeOrderSummary>('/trade/order/summary', params)
}

/** 获取交易订单详情 */
export function getTradeOrder(id: number) {
  return http.get<TradeOrder>(`/trade/order/get-detail?id=${id}`)
}

/** 获取交易订单物流轨迹 */
export function getTradeOrderExpressTrackList(id: number) {
  return http.get<Record<string, any>[]>(`/trade/order/get-express-track-list?id=${id}`)
}

/** 订单发货 */
export function deliveryTradeOrder(data: { id?: number, logisticsId: number | null, logisticsNo: string }) {
  return http.put<boolean>('/trade/order/delivery', data)
}

/** 更新订单备注 */
export function updateTradeOrderRemark(data: Record<string, any>) {
  return http.put<boolean>('/trade/order/update-remark', data)
}

/** 更新订单价格 */
export function updateTradeOrderPrice(data: Record<string, any>) {
  return http.put<boolean>('/trade/order/update-price', data)
}

/** 更新订单地址 */
export function updateTradeOrderAddress(data: Record<string, any>) {
  return http.put<boolean>('/trade/order/update-address', data)
}

/** 订单核销 */
export function pickUpTradeOrder(id: number) {
  return http.put<boolean>(`/trade/order/pick-up-by-id?id=${id}`)
}

/** 根据核销码核销订单 */
export function pickUpTradeOrderByVerifyCode(pickUpVerifyCode: string) {
  return http.put<boolean>('/trade/order/pick-up-by-verify-code', undefined, {
    pickUpVerifyCode,
  })
}

/** 根据核销码查询订单 */
export function getTradeOrderByPickUpVerifyCode(pickUpVerifyCode: string) {
  return http.get<TradeOrder>('/trade/order/get-by-pick-up-verify-code', {
    pickUpVerifyCode,
  })
}
