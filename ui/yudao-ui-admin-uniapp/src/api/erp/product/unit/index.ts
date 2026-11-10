import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 产品单位 */
export interface ProductUnit {
  id?: number // 单位编号
  name?: string // 单位名字
  status?: number // 单位状态
  createTime?: Date // 创建时间
}

/** 获取产品单位分页列表 */
export function getProductUnitPage(params: PageParam) {
  return http.get<PageResult<ProductUnit>>('/erp/product-unit/page', params)
}

/** 获取产品单位精简列表 */
export function getProductUnitSimpleList() {
  return http.get<ProductUnit[]>('/erp/product-unit/simple-list')
}

/** 获取产品单位详情 */
export function getProductUnit(id: number) {
  return http.get<ProductUnit>(`/erp/product-unit/get?id=${id}`)
}

/** 创建产品单位 */
export function createProductUnit(data: ProductUnit) {
  return http.post<number>('/erp/product-unit/create', data)
}

/** 更新产品单位 */
export function updateProductUnit(data: ProductUnit) {
  return http.put<boolean>('/erp/product-unit/update', data)
}

/** 删除产品单位 */
export function deleteProductUnit(id: number) {
  return http.delete<boolean>(`/erp/product-unit/delete?id=${id}`)
}
