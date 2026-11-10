import type { PageParam, PageResult } from '@/http/types'
import type { ItemSku } from './sku'
import { http } from '@/http/http'

/** WMS 商品 */
export interface Item {
  id?: number
  code?: string
  name?: string
  categoryId?: number
  categoryName?: string
  unit?: string
  brandId?: number
  brandName?: string
  remark?: string
  skus?: ItemSku[]
  createTime?: Date
}

/** 查询商品分页 */
export function getItemPage(params: PageParam) {
  return http.get<PageResult<Item>>('/wms/item/page', params)
}

/** 查询商品详情 */
export function getItem(id: number) {
  return http.get<Item>(`/wms/item/get?id=${id}`)
}

/** 新增商品 */
export function createItem(data: Item) {
  return http.post<number>('/wms/item/create', data)
}

/** 修改商品 */
export function updateItem(data: Item) {
  return http.put<boolean>('/wms/item/update', data)
}

/** 删除商品 */
export function deleteItem(id: number) {
  return http.delete<boolean>(`/wms/item/delete?id=${id}`)
}
