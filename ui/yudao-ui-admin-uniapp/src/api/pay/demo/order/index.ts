import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 示例订单 */
export interface PayDemoOrder {
  id?: number
  spuId?: number
  spuName?: string
  userId?: number
  price?: number
  refundPrice?: number
  payOrderId?: number
  payStatus?: boolean | string
  payTime?: string
  payRefundId?: number
  refundTime?: string
  createTime?: string
}

/** 创建示例订单 */
export function createPayDemoOrder(data: { spuId?: number }) {
  return http.post<number>('/pay/demo-order/create', data)
}

/** 获取示例订单分页 */
export function getPayDemoOrderPage(params: PageParam) {
  return http.get<PageResult<PayDemoOrder>>('/pay/demo-order/page', params)
}

/** 退款示例订单 */
export function refundPayDemoOrder(id: number) {
  return http.put<boolean>(`/pay/demo-order/refund?id=${id}`)
}
