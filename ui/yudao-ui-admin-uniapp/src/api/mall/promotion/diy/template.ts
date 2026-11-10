import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import type { PromotionDiyPage } from './page'

/** 装修模板 */
export interface PromotionDiyTemplate {
  id?: number
  name?: string
  used?: boolean
  usedTime?: string
  remark?: string
  previewPicUrls?: string[]
  property?: string
  pages?: PromotionDiyPage[]
  createTime?: string
}

/** 获取装修模板分页列表 */
export function getPromotionDiyTemplatePage(params: PageParam) {
  return http.get<PageResult<PromotionDiyTemplate>>('/promotion/diy-template/page', params)
}

/** 获取装修模板详情 */
export function getPromotionDiyTemplate(id: number) {
  return http.get<PromotionDiyTemplate>(`/promotion/diy-template/get?id=${id}`)
}

/** 创建装修模板 */
export function createPromotionDiyTemplate(data: PromotionDiyTemplate) {
  return http.post<number>('/promotion/diy-template/create', data as Record<string, any>)
}

/** 更新装修模板 */
export function updatePromotionDiyTemplate(data: PromotionDiyTemplate) {
  return http.put<boolean>('/promotion/diy-template/update', data as Record<string, any>)
}

/** 删除装修模板 */
export function deletePromotionDiyTemplate(id: number) {
  return http.delete<boolean>(`/promotion/diy-template/delete?id=${id}`)
}

/** 使用装修模板 */
export function usePromotionDiyTemplate(id: number) {
  return http.put<boolean>(`/promotion/diy-template/use?id=${id}`)
}

/** 获取装修模板属性 */
export function getPromotionDiyTemplateProperty(id: number) {
  return http.get<PromotionDiyTemplate>(`/promotion/diy-template/get-property?id=${id}`)
}

/** 更新装修模板属性 */
export function updatePromotionDiyTemplateProperty(data: PromotionDiyTemplate) {
  return http.put<boolean>('/promotion/diy-template/update-property', data as Record<string, any>)
}
