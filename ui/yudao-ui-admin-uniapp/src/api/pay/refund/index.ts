import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 退款订单 */
export interface PayRefund {
  id?: number
  no?: string
  merchantId?: number
  appId?: number
  appName?: string
  channelId?: number
  channelCode?: string
  orderId?: number | string
  tradeNo?: string
  merchantOrderId?: string
  merchantRefundId?: string
  notifyUrl?: string
  notifyStatus?: number
  status?: number
  type?: number
  payPrice?: number
  refundPrice?: number
  reason?: string
  userIp?: string
  channelOrderNo?: string
  channelRefundNo?: string
  channelErrorCode?: string
  channelErrorMsg?: string
  channelNotifyData?: string
  expireTime?: string
  successTime?: string
  notifyTime?: string
  createTime?: string
}

/** 退款订单分页参数 */
export interface PayRefundPageParam extends PageParam {
  appId?: number
  channelCode?: string
  merchantOrderId?: string
  merchantRefundId?: string
  channelOrderNo?: string
  channelRefundNo?: string
  status?: number
  createTime?: string[]
}

/** 获取退款订单分页列表 */
export function getPayRefundPage(params: PayRefundPageParam) {
  return http.get<PageResult<PayRefund>>('/pay/refund/page', params)
}

/** 获取退款订单详情 */
export function getPayRefund(id: number) {
  return http.get<PayRefund>(`/pay/refund/get?id=${id}`)
}

/** 创建退款订单 */
export function createPayRefund(data: PayRefund) {
  return http.post<number>('/pay/refund/create', data)
}

/** 更新退款订单 */
export function updatePayRefund(data: PayRefund) {
  return http.put<boolean>('/pay/refund/update', data)
}

/** 删除退款订单 */
export function deletePayRefund(id: number) {
  return http.delete<boolean>(`/pay/refund/delete?id=${id}`)
}
