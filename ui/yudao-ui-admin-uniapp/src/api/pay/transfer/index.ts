import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 转账单 */
export interface PayTransfer {
  id?: number
  no?: string
  appId?: number
  appName?: string
  channelId?: number
  channelCode?: string
  merchantTransferId?: string
  status?: number
  successTime?: string
  price?: number
  subject?: string
  userName?: string
  userAccount?: string
  channelTransferNo?: string
  channelErrorCode?: string
  channelErrorMsg?: string
  notifyUrl?: string
  userIp?: string
  channelNotifyData?: string
  createTime?: string
}

/** 转账单分页参数 */
export interface PayTransferPageParam extends PageParam {
  no?: string
  channelCode?: string
  merchantOrderId?: string
  status?: number | string
  userName?: string
  userAccount?: string
  channelTransferNo?: string
  createTime?: string[]
}

/** 获取转账单分页列表 */
export function getPayTransferPage(params: PayTransferPageParam) {
  return http.get<PageResult<PayTransfer>>('/pay/transfer/page', params)
}

/** 获取转账单详情 */
export function getPayTransfer(id: number) {
  return http.get<PayTransfer>(`/pay/transfer/get?id=${id}`)
}
