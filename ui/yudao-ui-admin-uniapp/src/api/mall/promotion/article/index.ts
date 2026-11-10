import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 文章 */
export interface PromotionArticle {
  id?: number
  categoryId?: number
  categoryName?: string
  title: string
  author?: string
  picUrl?: string
  introduction?: string
  browseCount?: string | number
  sort?: number
  status: number
  spuId?: number
  recommendHot?: boolean
  recommendBanner?: boolean
  content?: string
  createTime?: string
}

/** 获取文章分页列表 */
export function getPromotionArticlePage(params: PageParam) {
  return http.get<PageResult<PromotionArticle>>('/promotion/article/page', params)
}

/** 获取文章详情 */
export function getPromotionArticle(id: number) {
  return http.get<PromotionArticle>(`/promotion/article/get?id=${id}`)
}

/** 创建文章 */
export function createPromotionArticle(data: PromotionArticle) {
  return http.post<number>('/promotion/article/create', data)
}

/** 更新文章 */
export function updatePromotionArticle(data: PromotionArticle) {
  return http.put<boolean>('/promotion/article/update', data)
}

/** 删除文章 */
export function deletePromotionArticle(id: number) {
  return http.delete<boolean>(`/promotion/article/delete?id=${id}`)
}
