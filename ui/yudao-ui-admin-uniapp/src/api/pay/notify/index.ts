import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 支付通知 */
export interface PayNotifyTask {
  id?: number
  appId?: number
  appName?: string
  type?: number
  dataId?: number
  status?: number
  merchantOrderId?: string
  merchantRefundId?: string
  merchantTransferId?: string
  notifyTimes?: number
  maxNotifyTimes?: number
  lastExecuteTime?: string
  nextNotifyTime?: string
  createTime?: string
  updateTime?: string
  logs?: PayNotifyLog[]
}

/** 支付通知日志 */
export interface PayNotifyLog {
  id?: number
  taskId?: number
  status?: number
  notifyTimes?: number
  request?: string
  response?: string
  createTime?: string
}

/** 支付通知分页参数 */
export interface PayNotifyPageParam extends PageParam {
  appId?: number
  type?: number
  dataId?: number
  status?: number
  merchantOrderId?: string
  merchantRefundId?: string
  merchantTransferId?: string
  createTime?: string[]
}

/** 获取支付通知明细 */
export function getPayNotifyTaskDetail(id: number) {
  return http.get<PayNotifyTask>(`/pay/notify/get-detail?id=${id}`)
}

/** 获取支付通知分页 */
export function getPayNotifyTaskPage(params: PayNotifyPageParam) {
  return http.get<PageResult<PayNotifyTask>>('/pay/notify/page', params)
}
