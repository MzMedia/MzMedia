import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

export interface FreePublishArticle {
  title?: string
  author?: string
  digest?: string
  contentSourceUrl?: string
  url?: string // 已发布图文的微信文章链接（列表预览用）
  thumbUrl?: string
}

export interface FreePublish {
  accountId?: number
  articleId?: string
  mediaId?: string
  articles?: FreePublishArticle[]
  content?: {
    newsItem?: FreePublishArticle[]
  }
  updateTime?: string
}

/** 获得公众号发布分页 */
export function getFreePublishPage(params: PageParam) {
  return http.get<PageResult<FreePublish>>('/mp/free-publish/page', params)
}

/** 删除公众号发布记录 */
export function deleteFreePublish(accountId: number, articleId: string) {
  return http.delete<boolean>(`/mp/free-publish/delete?accountId=${accountId}&articleId=${articleId}`)
}

/** 发布公众号草稿（返回发布任务 publishId） */
export function submitFreePublish(accountId: number, mediaId: string) {
  return http.post<string>(`/mp/free-publish/submit?accountId=${accountId}&mediaId=${mediaId}`)
}
