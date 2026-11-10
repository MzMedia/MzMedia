import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 装修页面 */
export interface PromotionDiyPage {
  id?: number
  templateId?: number
  name?: string
  remark?: string
  previewPicUrls?: string[]
  property?: string
  createTime?: string
}

/** 获取装修页面分页列表 */
export function getPromotionDiyPagePage(params: PageParam) {
  return http.get<PageResult<PromotionDiyPage>>('/promotion/diy-page/page', params)
}

/** 获取装修页面详情 */
export function getPromotionDiyPage(id: number) {
  return http.get<PromotionDiyPage>(`/promotion/diy-page/get?id=${id}`)
}

/** 创建装修页面 */
export function createPromotionDiyPage(data: PromotionDiyPage) {
  return http.post<number>('/promotion/diy-page/create', data as Record<string, any>)
}

/** 更新装修页面 */
export function updatePromotionDiyPage(data: PromotionDiyPage) {
  return http.put<boolean>('/promotion/diy-page/update', data as Record<string, any>)
}

/** 删除装修页面 */
export function deletePromotionDiyPage(id: number) {
  return http.delete<boolean>(`/promotion/diy-page/delete?id=${id}`)
}

/** 获取装修页面属性 */
export function getPromotionDiyPageProperty(id: number) {
  return http.get<PromotionDiyPage>(`/promotion/diy-page/get-property?id=${id}`)
}

/** 更新装修页面属性 */
export function updatePromotionDiyPageProperty(data: PromotionDiyPage) {
  return http.put<boolean>('/promotion/diy-page/update-property', data as Record<string, any>)
}
