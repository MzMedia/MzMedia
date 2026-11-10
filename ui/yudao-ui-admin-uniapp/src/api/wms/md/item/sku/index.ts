import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** WMS 商品 SKU */
export interface ItemSku {
  id?: number
  name?: string
  itemId?: number
  itemCode?: string
  itemName?: string
  categoryId?: number
  categoryName?: string
  unit?: string
  brandId?: number
  brandName?: string
  barCode?: string
  code?: string
  length?: number
  width?: number
  height?: number
  grossWeight?: number
  netWeight?: number
  costPrice?: number
  sellingPrice?: number
  createTime?: Date
}

/** 按 SKU 维度分页 */
export function getItemSkuPage(params: PageParam) {
  return http.get<PageResult<ItemSku>>('/wms/item-sku/page', params)
}
