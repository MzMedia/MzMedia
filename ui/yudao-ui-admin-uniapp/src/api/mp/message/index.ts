import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/mp/message'

/** 公众号消息 */
export interface MpArticle {
  title?: string
  description?: string
  picUrl?: string
  url?: string
  digest?: string
  thumbUrl?: string
  thumbMediaUrl?: string
  contentSourceUrl?: string
}

export interface MpMessage {
  id?: number
  userId?: number
  accountId?: number
  openid?: string
  type?: string
  sendFrom?: number
  content?: string
  mediaUrl?: string
  recognition?: string
  format?: string
  title?: string
  description?: string
  url?: string
  event?: string
  eventKey?: string
  articles?: MpArticle[]
  thumbMediaId?: string
  thumbMediaUrl?: string
  musicUrl?: string
  hqMusicUrl?: string
  locationX?: number
  locationY?: number
  scale?: number
  label?: string
  createTime?: Date
}

/** 公众号发送消息请求 */
export interface MpMessageSend {
  userId: number
  type: string
  accountId?: number
  content?: string
  mediaId?: string
  url?: string
  title?: string
  description?: string
  thumbMediaId?: string
  thumbMediaUrl?: string
  articles?: MpArticle[]
  musicUrl?: string
  hqMusicUrl?: string
}

/** 获取公众号消息分页 */
export function getMessagePage(params: PageParam) {
  return http.get<PageResult<MpMessage>>(`${baseUrl}/page`, params)
}

/** 给粉丝发送消息 */
export function sendMessage(data: MpMessageSend) {
  return http.post<MpMessage>(`${baseUrl}/send`, data)
}
