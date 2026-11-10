import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 产品库存 */
export interface Stock {
  id?: number // 编号
  productId?: number // 产品编号
  productName?: string // 产品名称
  categoryName?: string // 产品分类名称
  unitName?: string // 产品单位名称
  warehouseId?: number // 仓库编号
  warehouseName?: string // 仓库名称
  count?: number // 库存数量
}

/** 获取产品库存分页列表 */
export function getStockPage(params: PageParam) {
  return http.get<PageResult<Stock>>('/erp/stock/page', params)
}

/** 获取产品库存详情 */
export function getStock(id: number) {
  return http.get<Stock>(`/erp/stock/get?id=${id}`)
}

/** 根据产品和仓库获取库存详情 */
export function getStockByProductAndWarehouse(productId: number, warehouseId: number) {
  return http.get<Stock>('/erp/stock/get', { productId, warehouseId })
}

/** 获取产品库存数量 */
export function getStockCount(productId: number) {
  return http.get<number>('/erp/stock/get-count', { productId })
}
