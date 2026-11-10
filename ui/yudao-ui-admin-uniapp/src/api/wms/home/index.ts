import { http } from '@/http/http'

/** WMS 首页统计查询参数 */
export interface WmsHomeStatisticsReq {
  warehouseId?: number
  goodsLimit?: number
  warehouseLimit?: number
}

/** WMS 首页单据状态统计 */
export interface WmsHomeOrderStatusResp {
  status: number
  count: number
}

/** WMS 首页单据汇总统计 */
export interface WmsHomeOrderSummaryResp {
  type: number
  total: number
  statuses: WmsHomeOrderStatusResp[]
}

/** WMS 首页单据趋势 */
export interface WmsHomeOrderTrendResp {
  time: string | Date
  receiptCount: number
  shipmentCount: number
  movementCount: number
  checkCount: number
}

/** WMS 首页商品库存排行 */
export interface WmsHomeInventoryItemRankResp {
  id: number
  name: string
  quantity: number
}

/** WMS 首页仓库库存排行 */
export interface WmsHomeInventoryWarehouseRankResp {
  id: number
  name: string
  quantity: number
}

/** WMS 首页库存汇总统计 */
export interface WmsHomeInventorySummaryResp {
  totalQuantity: number
  goodsShareList: WmsHomeInventoryItemRankResp[]
  warehouseDistributionList: WmsHomeInventoryWarehouseRankResp[]
}

/** 获得首页单据汇总统计 */
export function getWmsHomeOrderSummary(params?: WmsHomeStatisticsReq) {
  return http.get<WmsHomeOrderSummaryResp[]>('/wms/home-statistics/order-summary', params)
}

/** 获得首页单据趋势 */
export function getWmsHomeOrderTrend(days?: number, params?: WmsHomeStatisticsReq) {
  return http.get<WmsHomeOrderTrendResp[]>('/wms/home-statistics/order-trend', { ...params, days })
}

/** 获得首页库存汇总统计 */
export function getWmsHomeInventorySummary(params?: WmsHomeStatisticsReq) {
  return http.get<WmsHomeInventorySummaryResp>('/wms/home-statistics/inventory-summary', params)
}
