import { http } from '@/http/http'

/** 交易中心配置 */
export interface TradeConfig {
  // 售后
  afterSaleRefundReasons?: string[]
  afterSaleReturnReasons?: string[]
  // 配送
  deliveryExpressFreeEnabled?: boolean
  deliveryExpressFreePrice?: number
  deliveryPickUpEnabled?: boolean
  // 分销
  brokerageEnabled?: boolean
  brokerageEnabledCondition?: number
  brokerageBindMode?: number
  brokeragePosterUrls?: string[]
  brokerageFirstPercent?: number
  brokerageSecondPercent?: number
  brokerageWithdrawMinPrice?: number
  brokerageWithdrawFeePercent?: number
  brokerageFrozenDays?: number
  brokerageWithdrawTypes?: number[]
}

/** 获取交易中心配置 */
export function getTradeConfig() {
  return http.get<TradeConfig>('/trade/config/get')
}

/** 保存交易中心配置 */
export function saveTradeConfig(data: TradeConfig) {
  return http.put<boolean>('/trade/config/save', data as Record<string, any>)
}
