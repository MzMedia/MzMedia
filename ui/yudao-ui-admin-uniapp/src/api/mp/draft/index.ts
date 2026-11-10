import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

export interface DraftArticle {
  title?: string
  author?: string
  digest?: string
  content?: string
  contentSourceUrl?: string
  url?: string // 图文在微信侧的可访问链接（列表预览用）
  thumbMediaId?: string
  thumbUrl?: string
  showCoverPic?: number
  needOpenComment?: number
  onlyFansCanComment?: number
}

/** 公众号图文 */
export type NewsItem = DraftArticle

export interface Draft {
  accountId?: number
  mediaId?: string
  articles?: DraftArticle[]
  content?: {
    newsItem?: DraftArticle[]
  }
  updateTime?: number
}

/** 创建空图文 */
export function createEmptyNewsItem(): NewsItem {
  return {
    title: '',
    author: '',
    digest: '',
    content: '',
    contentSourceUrl: '',
    thumbMediaId: '',
    thumbUrl: '',
    showCoverPic: 1,
    needOpenComment: 0,
    onlyFansCanComment: 0,
  }
}

/** 获得公众号草稿分页 */
export function getDraftPage(params: PageParam) {
  return http.get<PageResult<Draft>>('/mp/draft/page', params)
}

/** 创建公众号草稿 */
export function createDraft(accountId: number, articles: DraftArticle[]) {
  return http.post<string>(`/mp/draft/create?accountId=${accountId}`, { articles })
}

/** 更新公众号草稿 */
export function updateDraft(accountId: number, mediaId: string, articles: DraftArticle[]) {
  return http.put<boolean>(`/mp/draft/update?accountId=${accountId}&mediaId=${mediaId}`, articles)
}

/** 删除公众号草稿 */
export function deleteDraft(accountId: number, mediaId: string) {
  return http.delete<boolean>(`/mp/draft/delete?accountId=${accountId}&mediaId=${mediaId}`)
}
