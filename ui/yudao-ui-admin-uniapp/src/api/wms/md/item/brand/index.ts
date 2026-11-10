import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** WMS 商品品牌 */
export interface ItemBrand {
  id?: number
  code?: string
  name?: string
  createTime?: Date
}

/** 查询商品品牌分页 */
export function getItemBrandPage(params: PageParam) {
  return http.get<PageResult<ItemBrand>>('/wms/item-brand/page', params)
}

/** 查询商品品牌精简列表 */
export function getSimpleItemBrandList() {
  return http.get<ItemBrand[]>('/wms/item-brand/simple-list')
}

/** 查询商品品牌详情 */
export function getItemBrand(id: number) {
  return http.get<ItemBrand>(`/wms/item-brand/get?id=${id}`)
}

/** 新增商品品牌 */
export function createItemBrand(data: ItemBrand) {
  return http.post<number>('/wms/item-brand/create', data)
}

/** 修改商品品牌 */
export function updateItemBrand(data: ItemBrand) {
  return http.put<boolean>('/wms/item-brand/update', data)
}

/** 删除商品品牌 */
export function deleteItemBrand(id: number) {
  return http.delete<boolean>(`/wms/item-brand/delete?id=${id}`)
}
