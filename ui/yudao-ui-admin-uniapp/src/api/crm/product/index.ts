import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 产品 */
export interface Product {
  id?: number
  name: string
  no?: string
  unit?: number
  price?: number
  status?: number
  categoryId?: number
  categoryName?: string
  description?: string
  ownerUserId?: number
}

/** 查询产品分页列表 */
export function getProductPage(params: PageParam) {
  return http.get<PageResult<Product>>('/crm/product/page', params)
}

/** 获得产品精简列表 */
export function getProductSimpleList() {
  return http.get<Product[]>('/crm/product/simple-list')
}

/** 查询产品详情 */
export function getProduct(id: number) {
  return http.get<Product>(`/crm/product/get?id=${id}`)
}

/** 新增产品 */
export function createProduct(data: Product) {
  return http.post<number>('/crm/product/create', data)
}

/** 修改产品 */
export function updateProduct(data: Product) {
  return http.put<boolean>('/crm/product/update', data)
}

/** 删除产品 */
export function deleteProduct(id: number) {
  return http.delete<boolean>(`/crm/product/delete?id=${id}`)
}

/** 导出产品 Excel */
export function exportProduct(params: Record<string, any>) {
  return downloadApiFile('/crm/product/export-excel', params, '产品.xls')
}
