import { http } from '@/http/http'

export interface StatisticsItem {
  refDate?: string
  userSource?: number
  newUser?: number
  cancelUser?: number
  cumulateUser?: number
  msgType?: number
  msgUser?: number
  msgCount?: number
  messageUser?: number
  messageCount?: number
  countInterval?: number
  callbackCount?: number
  failCount?: number
  totalTimeCost?: number
  maxTimeCost?: number
}

/** 获取消息发送概况数据 */
export function getUpstreamMessage(params: Record<string, any>) {
  return http.get<StatisticsItem[]>('/mp/statistics/upstream-message', params)
}

/** 用户增减数据 */
export function getUserSummary(params: Record<string, any>) {
  return http.get<StatisticsItem[]>('/mp/statistics/user-summary', params)
}

/** 获得用户累计数据 */
export function getUserCumulate(params: Record<string, any>) {
  return http.get<StatisticsItem[]>('/mp/statistics/user-cumulate', params)
}

/** 获得接口分析数据 */
export function getInterfaceSummary(params: Record<string, any>) {
  return http.get<StatisticsItem[]>('/mp/statistics/interface-summary', params)
}
