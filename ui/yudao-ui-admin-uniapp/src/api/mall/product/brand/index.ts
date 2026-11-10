import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品品牌 */
export interface ProductBrand {
  id?: number
  name: string
  picUrl?: string
  sort?: number
  description?: string
  status: number
  createTime?: string
}

/** 获取商品品牌分页列表 */
export function getProductBrandPage(params: PageParam) {
  return http.get<PageResult<ProductBrand>>('/product/brand/page', params)
}

/** 获取商品品牌详情 */
export function getProductBrand(id: number) {
  return http.get<ProductBrand>(`/product/brand/get?id=${id}`)
}

/** 创建商品品牌 */
export function createProductBrand(data: ProductBrand) {
  return http.post<number>('/product/brand/create', data)
}

/** 更新商品品牌 */
export function updateProductBrand(data: ProductBrand) {
  return http.put<boolean>('/product/brand/update', data)
}

/** 删除商品品牌 */
export function deleteProductBrand(id: number) {
  return http.delete<boolean>(`/product/brand/delete?id=${id}`)
}

/** 获取商品品牌精简列表 */
export function getSimpleProductBrandList() {
  return http.get<ProductBrand[]>('/product/brand/list-all-simple')
}
