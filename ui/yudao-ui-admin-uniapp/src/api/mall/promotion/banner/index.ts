import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** Banner */
export interface PromotionBanner {
  id?: number
  title: string
  picUrl?: string
  status: number
  url?: string
  position?: number
  sort?: number
  memo?: string
  createTime?: string
}

/** 获取 Banner 分页列表 */
export function getPromotionBannerPage(params: PageParam) {
  return http.get<PageResult<PromotionBanner>>('/promotion/banner/page', params)
}

/** 获取 Banner 详情 */
export function getPromotionBanner(id: number) {
  return http.get<PromotionBanner>(`/promotion/banner/get?id=${id}`)
}

/** 创建 Banner */
export function createPromotionBanner(data: PromotionBanner) {
  return http.post<number>('/promotion/banner/create', data)
}

/** 更新 Banner */
export function updatePromotionBanner(data: PromotionBanner) {
  return http.put<boolean>('/promotion/banner/update', data)
}

/** 删除 Banner */
export function deletePromotionBanner(id: number) {
  return http.delete<boolean>(`/promotion/banner/delete?id=${id}`)
}
