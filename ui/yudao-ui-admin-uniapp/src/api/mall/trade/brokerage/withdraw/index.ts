import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 佣金提现 */
export interface TradeBrokerageWithdraw {
  id?: number
  userId?: number
  price?: number
  feePrice?: number
  totalPrice?: number
  type?: number
  userNickname?: string
  userName?: string
  userAccount?: string
  bankName?: string
  bankAddress?: string
  qrCodeUrl?: string
  status?: number
  auditReason?: string
  auditTime?: string
  remark?: string
  payTransferId?: number
  transferChannelCode?: string
  transferTime?: string
  transferErrorMsg?: string
  createTime?: string
}

/** 获取佣金提现分页列表 */
export function getTradeBrokerageWithdrawPage(params: PageParam) {
  return http.get<PageResult<TradeBrokerageWithdraw>>('/trade/brokerage-withdraw/page', params)
}

/** 获取佣金提现详情 */
export function getTradeBrokerageWithdraw(id: number) {
  return http.get<TradeBrokerageWithdraw>(`/trade/brokerage-withdraw/get?id=${id}`)
}

/** 通过佣金提现 */
export function approveTradeBrokerageWithdraw(id: number) {
  return http.put<boolean>(`/trade/brokerage-withdraw/approve?id=${id}`)
}

/** 驳回佣金提现 */
export function rejectTradeBrokerageWithdraw(data: TradeBrokerageWithdraw) {
  return http.put<boolean>('/trade/brokerage-withdraw/reject', data as Record<string, any>)
}
