import { http } from '@/http/http'

/** 数据对照 */
export interface DataComparison<T> {
  value: T
  reference: T
}

/** 交易订单统计 */
export interface TradeOrderComparison {
  orderPayCount?: number
  orderPayPrice?: number
}

/** 交易订单数量 */
export interface TradeOrderCount {
  undelivered?: number
  pickUp?: number
  afterSaleApply?: number
  auditingWithdraw?: number
}

/** 会员数量统计 */
export interface MemberCountComparison {
  visitUserCount?: string | number
  registerUserCount?: number
}

/** 支付统计 */
export interface PaySummary {
  rechargePrice?: number
}

/** 会员统计 */
export interface MemberSummary {
  userCount?: number
  rechargeUserCount?: number
  rechargePrice?: number
  expensePrice?: number
}

/** 会员分析 */
export interface MemberAnalyse {
  visitUserCount?: number
  orderUserCount?: number
  payUserCount?: number
  atv?: number
  comparison?: DataComparison<Record<string, any>>
}

/** 商品统计 */
export interface ProductStatistics {
  id?: number
  day?: string
  spuId?: number
  spuName?: string
  spuPicUrl?: string
  browseCount?: number
  browseUserCount?: number
  favoriteCount?: number
  cartCount?: number
  orderCount?: number
  orderPayCount?: number
  orderPayPrice?: number
  afterSaleCount?: number
  afterSaleRefundPrice?: number
  browseConvertPercent?: number
}

/** 交易统计 */
export interface TradeSummary {
  yesterdayOrderCount?: number
  monthOrderCount?: number
  yesterdayPayPrice?: number
  monthPayPrice?: number
}

/** 交易趋势 */
export interface TradeTrendSummary {
  time?: string
  turnoverPrice?: number
  orderPayPrice?: number
  rechargePrice?: number
  expensePrice?: number
  walletPayPrice?: number
  brokerageSettlementPrice?: number
  afterSaleRefundPrice?: number
}

/** 获取交易订单数量对照 */
export function getTradeOrderComparison() {
  return http.get<DataComparison<TradeOrderComparison>>('/statistics/trade/order-comparison')
}

/** 获取交易订单数量 */
export function getTradeOrderCount() {
  return http.get<TradeOrderCount>('/statistics/trade/order-count')
}

/** 获取会员数量对照 */
export function getMemberUserCountComparison() {
  return http.get<DataComparison<MemberCountComparison>>('/statistics/member/user-count-comparison')
}

/** 获取钱包充值金额 */
export function getWalletRechargePrice() {
  return http.get<PaySummary>('/statistics/pay/summary')
}

/** 获取会员统计 */
export function getMemberSummary() {
  return http.get<MemberSummary>('/statistics/member/summary')
}

/** 获取会员分析数据 */
export function getMemberAnalyse(params: Record<string, any>) {
  return http.get<MemberAnalyse>('/statistics/member/analyse', params)
}

/** 获取会员地区统计列表 */
export function getMemberAreaStatisticsList() {
  return http.get<Record<string, any>[]>('/statistics/member/area-statistics-list')
}

/** 获取会员性别统计列表 */
export function getMemberSexStatisticsList() {
  return http.get<Record<string, any>[]>('/statistics/member/sex-statistics-list')
}

/** 获取会员终端统计列表 */
export function getMemberTerminalStatisticsList() {
  return http.get<Record<string, any>[]>('/statistics/member/terminal-statistics-list')
}

/** 获取会员注册数量列表 */
export function getMemberRegisterCountList(params: Record<string, any>) {
  return http.get<Record<string, any>[]>('/statistics/member/register-count-list', params)
}

/** 获取交易统计 */
export function getTradeStatisticsSummary() {
  return http.get<DataComparison<TradeSummary>>('/statistics/trade/summary')
}

/** 获取交易状况统计 */
export function getTradeStatisticsAnalyse(params: Record<string, any>) {
  return http.get<DataComparison<TradeTrendSummary>>('/statistics/trade/analyse', params)
}

/** 获取交易状况明细 */
export function getTradeStatisticsList(params: Record<string, any>) {
  return http.get<TradeTrendSummary[]>('/statistics/trade/list', params)
}

/** 获取订单量趋势统计 */
export function getTradeOrderCountTrendComparison(params: Record<string, any>) {
  return http.get<DataComparison<Record<string, any>>[]>('/statistics/trade/order-count-trend', params)
}

/** 获取商品统计分析 */
export function getProductStatisticsAnalyse(params: Record<string, any>) {
  return http.get<DataComparison<ProductStatistics>>('/statistics/product/analyse', params)
}

/** 获取商品状况明细 */
export function getProductStatisticsList(params: Record<string, any>) {
  return http.get<ProductStatistics[]>('/statistics/product/list', params)
}

/** 获取商品排行榜分页 */
export function getProductStatisticsRankPage(params: Record<string, any>) {
  return http.get<any>('/statistics/product/rank-page', params)
}
