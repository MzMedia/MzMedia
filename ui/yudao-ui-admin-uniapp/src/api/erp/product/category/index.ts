import { http } from '@/http/http'

/** ERP 产品分类 */
export interface ProductCategory {
  id?: number // 分类编号
  parentId?: number // 父分类编号
  name?: string // 分类名称
  code?: string // 分类编码
  sort?: number // 分类排序
  status?: number // 开启状态
  createTime?: Date // 创建时间
  children?: ProductCategory[] // 子分类
}

/** 获取产品分类列表 */
export function getProductCategoryList(params?: { name?: string, status?: number }) {
  return http.get<ProductCategory[]>('/erp/product-category/list', params)
}

/** 获取产品分类精简列表 */
export function getProductCategorySimpleList() {
  return http.get<ProductCategory[]>('/erp/product-category/simple-list')
}

/** 获取产品分类详情 */
export function getProductCategory(id: number) {
  return http.get<ProductCategory>(`/erp/product-category/get?id=${id}`)
}

/** 创建产品分类 */
export function createProductCategory(data: ProductCategory) {
  return http.post<number>('/erp/product-category/create', data)
}

/** 更新产品分类 */
export function updateProductCategory(data: ProductCategory) {
  return http.put<boolean>('/erp/product-category/update', data)
}

/** 删除产品分类 */
export function deleteProductCategory(id: number) {
  return http.delete<boolean>(`/erp/product-category/delete?id=${id}`)
}
