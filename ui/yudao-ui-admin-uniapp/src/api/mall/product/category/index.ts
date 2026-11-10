import { http } from '@/http/http'

/** 商品分类 */
export interface ProductCategory {
  id?: number
  parentId?: number
  name: string
  picUrl?: string
  sort: number
  status: number
  createTime?: string
}

/** 获取商品分类列表 */
export function getProductCategoryList(params?: Record<string, any>) {
  return http.get<ProductCategory[]>('/product/category/list', params)
}

/** 获取商品分类详情 */
export function getProductCategory(id: number) {
  return http.get<ProductCategory>(`/product/category/get?id=${id}`)
}

/** 创建商品分类 */
export function createProductCategory(data: ProductCategory) {
  return http.post<number>('/product/category/create', data)
}

/** 更新商品分类 */
export function updateProductCategory(data: ProductCategory) {
  return http.put<boolean>('/product/category/update', data)
}

/** 删除商品分类 */
export function deleteProductCategory(id: number) {
  return http.delete<boolean>(`/product/category/delete?id=${id}`)
}
