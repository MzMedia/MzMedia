import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 佣金记录 */
export interface TradeBrokerageRecord {
  id?: number
  userId?: number
  bizType?: number
  bizId?: string
  title?: string
  price?: number
  totalPrice?: number
  description?: string // 说明
  status?: number
  frozenDays?: number
  unfreezeTime?: string
  createTime?: string
  userNickname?: string // 用户昵称
  userAvatar?: string // 用户头像
  sourceUserNickname?: string // 来源用户昵称
}

/** 获取佣金记录分页列表 */
export function getTradeBrokerageRecordPage(params: PageParam) {
  return http.get<PageResult<TradeBrokerageRecord>>('/trade/brokerage-record/page', params)
}

/** 获取佣金记录详情 */
export function getTradeBrokerageRecord(id: number) {
  return http.get<TradeBrokerageRecord>(`/trade/brokerage-record/get?id=${id}`)
}
