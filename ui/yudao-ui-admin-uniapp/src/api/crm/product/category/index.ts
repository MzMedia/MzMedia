import { http } from '@/http/http'

/** 产品分类 */
export interface ProductCategory {
  id?: number
  name: string
  parentId?: number
  createTime?: Date | string
  children?: ProductCategory[]
}

/** 查询产品分类详情 */
export function getProductCategory(id: number) {
  return http.get<ProductCategory>(`/crm/product-category/get?id=${id}`)
}

/** 新增产品分类 */
export function createProductCategory(data: ProductCategory) {
  return http.post<number>('/crm/product-category/create', data)
}

/** 修改产品分类 */
export function updateProductCategory(data: ProductCategory) {
  return http.put<boolean>('/crm/product-category/update', data)
}

/** 删除产品分类 */
export function deleteProductCategory(id: number) {
  return http.delete<boolean>(`/crm/product-category/delete?id=${id}`)
}

/** 产品分类列表 */
export function getProductCategoryList(params?: Record<string, any>) {
  return http.get<ProductCategory[]>('/crm/product-category/list', params)
}
