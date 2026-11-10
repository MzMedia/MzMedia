import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 文章分类 */
export interface PromotionArticleCategory {
  id?: number
  name: string
  picUrl?: string
  status: number
  sort?: number
  createTime?: string
}

/** 获取文章分类分页列表 */
export function getPromotionArticleCategoryPage(params: PageParam) {
  return http.get<PageResult<PromotionArticleCategory>>('/promotion/article-category/page', params)
}

/** 获取文章分类精简列表 */
export function getSimplePromotionArticleCategoryList() {
  return http.get<PromotionArticleCategory[]>('/promotion/article-category/list-all-simple')
}

/** 获取文章分类详情 */
export function getPromotionArticleCategory(id: number) {
  return http.get<PromotionArticleCategory>(`/promotion/article-category/get?id=${id}`)
}

/** 创建文章分类 */
export function createPromotionArticleCategory(data: PromotionArticleCategory) {
  return http.post<number>('/promotion/article-category/create', data)
}

/** 更新文章分类 */
export function updatePromotionArticleCategory(data: PromotionArticleCategory) {
  return http.put<boolean>('/promotion/article-category/update', data)
}

/** 删除文章分类 */
export function deletePromotionArticleCategory(id: number) {
  return http.delete<boolean>(`/promotion/article-category/delete?id=${id}`)
}
