import type { MpArticle } from '@/api/mp/message'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

export interface AutoReply {
  id?: number
  accountId?: number
  type?: number
  requestKeyword?: string
  requestMatch?: number
  requestMessageType?: string
  responseMessageType?: string
  responseContent?: string
  responseMediaId?: string
  responseMediaUrl?: string
  responseTitle?: string
  responseDescription?: string
  responseArticles?: MpArticle[]
  responseThumbMediaId?: string
  responseThumbMediaUrl?: string
  responseMusicUrl?: string
  responseHqMusicUrl?: string
  createTime?: string | Date
}

/** 创建公众号的自动回复 */
export function createAutoReply(data: AutoReply) {
  return http.post<number>('/mp/auto-reply/create', data)
}

/** 更新公众号的自动回复 */
export function updateAutoReply(data: AutoReply) {
  return http.put<boolean>('/mp/auto-reply/update', data)
}

/** 删除公众号的自动回复 */
export function deleteAutoReply(id: number) {
  return http.delete<boolean>(`/mp/auto-reply/delete?id=${id}`)
}

/** 获得公众号的自动回复 */
export function getAutoReply(id: number) {
  return http.get<AutoReply>(`/mp/auto-reply/get?id=${id}`)
}

/** 获得公众号的自动回复分页 */
export function getAutoReplyPage(params: PageParam) {
  return http.get<PageResult<AutoReply>>('/mp/auto-reply/page', params)
}
