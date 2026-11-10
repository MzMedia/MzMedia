import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 分销用户 */
export interface TradeBrokerageUser {
  id?: number
  bindUserId?: number
  bindUserTime?: string
  brokerageEnabled?: boolean
  brokerageTime?: string
  price?: number
  frozenPrice?: number
  nickname?: string
  avatar?: string
  brokerageUserCount?: number // 推广用户数量
  brokerageOrderCount?: number // 推广订单数量
  brokerageOrderPrice?: number // 推广订单金额（分）
  withdrawPrice?: number // 已提现金额（分）
  withdrawCount?: number // 已提现次数
}

/** 获取分销用户分页列表 */
export function getTradeBrokerageUserPage(params: PageParam) {
  return http.get<PageResult<TradeBrokerageUser>>('/trade/brokerage-user/page', params)
}

/** 获取分销用户详情 */
export function getTradeBrokerageUser(id: number) {
  return http.get<TradeBrokerageUser>(`/trade/brokerage-user/get?id=${id}`)
}

/** 创建分销用户 */
export function createTradeBrokerageUser(data: { userId: number, bindUserId: number }) {
  return http.post<number>('/trade/brokerage-user/create', data)
}

/** 修改推广员 */
export function updateTradeBrokerageUser(data: Record<string, any>) {
  return http.put<boolean>('/trade/brokerage-user/update-bind-user', data)
}

/** 清除推广员 */
export function clearTradeBrokerageUserBind(id: number) {
  return http.put<boolean>('/trade/brokerage-user/clear-bind-user', { id })
}

/** 修改推广资格 */
export function updateTradeBrokerageUserEnabled(data: { id: number, enabled: boolean }) {
  return http.put<boolean>('/trade/brokerage-user/update-brokerage-enable', data)
}
