import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 支付订单 */
export interface PayOrder {
  id?: number
  no?: string
  merchantId?: number
  appId?: number
  appName?: string
  channelId?: number
  channelCode?: string
  merchantOrderId?: string
  subject?: string
  body?: string
  notifyUrl?: string
  notifyStatus?: number
  price?: number
  channelFeeRate?: number
  channelFeePrice?: number
  status?: number
  userIp?: string
  expireTime?: string
  successTime?: string
  notifyTime?: string
  refundStatus?: number
  refundPrice?: number
  refundTimes?: number
  channelUserId?: string
  channelOrderNo?: string
  createTime?: string
  updateTime?: string
  extension?: {
    channelNotifyData?: string
  }
}

/** 支付订单分页参数 */
export interface PayOrderPageParam extends PageParam {
  appId?: number
  channelCode?: string
  merchantOrderId?: string
  no?: string
  channelOrderNo?: string
  status?: number
  createTime?: string[]
}

/** 获取支付订单分页列表 */
export function getPayOrderPage(params: PayOrderPageParam) {
  return http.get<PageResult<PayOrder>>('/pay/order/page', params)
}

/** 获取支付订单详情 */
export function getPayOrder(id: number, sync?: boolean) {
  return http.get<PayOrder>('/pay/order/get', { id, sync })
}

/** 获取支付订单明细 */
export function getPayOrderDetail(id: number) {
  return http.get<PayOrder>(`/pay/order/get-detail?id=${id}`)
}

/** 提交支付订单 */
export function submitPayOrder(data: Record<string, any>) {
  return http.post<any>('/pay/order/submit', data)
}
