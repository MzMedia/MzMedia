import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** WMS 库存统计 */
export interface Inventory {
  id?: number
  itemId?: number
  itemCode?: string
  itemName?: string
  unit?: string
  skuId?: number
  skuCode?: string
  skuName?: string
  warehouseId?: number
  warehouseName?: string
  quantity?: number
  remark?: string
  createTime?: Date
}

/** WMS 库存统计列表查询参数 */
export interface InventoryListReq {
  warehouseId: number
}

/** 查询库存统计分页 */
export function getInventoryPage(params: PageParam) {
  return http.get<PageResult<Inventory>>('/wms/inventory/page', params)
}

/** 查询库存统计列表 */
export function getInventoryList(params: InventoryListReq) {
  return http.get<Inventory[]>('/wms/inventory/list', params)
}
