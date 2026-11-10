import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品属性 */
export interface ProductProperty {
  id?: number
  name: string
  remark?: string
  createTime?: string
}

/** 商品属性值 */
export interface ProductPropertyValue {
  id?: number
  propertyId?: number
  name: string
  remark?: string
  createTime?: string
}

/** 获取商品属性分页列表 */
export function getProductPropertyPage(params: PageParam) {
  return http.get<PageResult<ProductProperty>>('/product/property/page', params)
}

/** 获取商品属性详情 */
export function getProductProperty(id: number) {
  return http.get<ProductProperty>(`/product/property/get?id=${id}`)
}

/** 创建商品属性 */
export function createProductProperty(data: ProductProperty) {
  return http.post<number>('/product/property/create', data)
}

/** 更新商品属性 */
export function updateProductProperty(data: ProductProperty) {
  return http.put<boolean>('/product/property/update', data)
}

/** 删除商品属性 */
export function deleteProductProperty(id: number) {
  return http.delete<boolean>(`/product/property/delete?id=${id}`)
}

/** 获取商品属性精简列表 */
export function getSimpleProductPropertyList() {
  return http.get<ProductProperty[]>('/product/property/simple-list')
}

/** 获取商品属性值分页列表 */
export function getProductPropertyValuePage(params: PageParam) {
  return http.get<PageResult<ProductPropertyValue>>('/product/property/value/page', params)
}

/** 获取商品属性值详情 */
export function getProductPropertyValue(id: number) {
  return http.get<ProductPropertyValue>(`/product/property/value/get?id=${id}`)
}

/** 创建商品属性值 */
export function createProductPropertyValue(data: ProductPropertyValue) {
  return http.post<number>('/product/property/value/create', data)
}

/** 更新商品属性值 */
export function updateProductPropertyValue(data: ProductPropertyValue) {
  return http.put<boolean>('/product/property/value/update', data)
}

/** 删除商品属性值 */
export function deleteProductPropertyValue(id: number) {
  return http.delete<boolean>(`/product/property/value/delete?id=${id}`)
}

/** 获取商品属性值精简列表 */
export function getSimpleProductPropertyValueList(propertyId: number) {
  return http.get<ProductPropertyValue[]>('/product/property/value/simple-list', { propertyId })
}
