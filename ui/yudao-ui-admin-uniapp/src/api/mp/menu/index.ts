import { http } from '@/http/http'

const baseUrl = '/mp/menu'

/** 公众号菜单回复 */
export interface MenuReply {
  type?: string
  accountId?: number
  content?: string
  mediaId?: string
  url?: string
  title?: string
  description?: string
  thumbMediaId?: string
  thumbMediaUrl?: string
  articles?: any[]
  musicUrl?: string
  hqMusicUrl?: string
}

/** 公众号菜单 */
export interface MpMenu {
  id?: number
  parentId?: number
  accountId?: number
  name?: string
  menuKey?: string
  type?: string
  url?: string
  miniProgramAppId?: string
  miniProgramPagePath?: string
  articleId?: string
  reply?: MenuReply
  children?: MpMenu[]
  replyMessageType?: string
  replyContent?: string
  replyMediaId?: string
  replyMediaUrl?: string
  replyTitle?: string
  replyDescription?: string
  replyThumbMediaId?: string
  replyThumbMediaUrl?: string
  replyArticles?: any[]
  replyMusicUrl?: string
  replyHqMusicUrl?: string
}

/** 获取公众号菜单列表 */
export function getMenuList(accountId: number) {
  return http.get<MpMenu[]>(`${baseUrl}/list?accountId=${accountId}`)
}

/** 保存公众号菜单 */
export function saveMenu(accountId: number, menus: MpMenu[]) {
  return http.post<boolean>(`${baseUrl}/save`, { accountId, menus })
}

/** 删除公众号菜单 */
export function deleteMenu(accountId: number) {
  return http.delete<boolean>(`${baseUrl}/delete?accountId=${accountId}`)
}
