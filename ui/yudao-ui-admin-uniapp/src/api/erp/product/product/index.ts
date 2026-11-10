import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 产品 */
export interface Product {
  id?: number // 产品编号
  name?: string // 产品名称
  barCode?: string // 产品条码
  categoryId?: number // 产品分类编号
  categoryName?: string // 产品分类名称
  unitId?: number // 单位编号
  unitName?: string // 单位名字
  status?: number // 产品状态
  standard?: string // 产品规格
  remark?: string // 产品备注
  expiryDay?: number // 保质期天数
  weight?: number // 重量（kg）
  purchasePrice?: number // 采购价格，单位：元
  salePrice?: number // 销售价格，单位：元
  minPrice?: number // 最低价格，单位：元
  createTime?: Date // 创建时间
}

/** 获取产品分页列表 */
export function getProductPage(params: PageParam) {
  return http.get<PageResult<Product>>('/erp/product/page', params)
}

/** 获取产品精简列表 */
export function getProductSimpleList() {
  return http.get<Product[]>('/erp/product/simple-list')
}

/** 获取产品详情 */
export function getProduct(id: number) {
  return http.get<Product>(`/erp/product/get?id=${id}`)
}

/** 创建产品 */
export function createProduct(data: Product) {
  return http.post<number>('/erp/product/create', data)
}

/** 更新产品 */
export function updateProduct(data: Product) {
  return http.put<boolean>('/erp/product/update', data)
}

/** 删除产品 */
export function deleteProduct(id: number) {
  return http.delete<boolean>(`/erp/product/delete?id=${id}`)
}
