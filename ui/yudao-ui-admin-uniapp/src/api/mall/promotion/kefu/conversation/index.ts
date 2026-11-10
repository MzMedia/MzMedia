import { http } from '@/http/http'

/** 客服会话 */
export interface PromotionKefuConversation {
  id?: number
  userId?: number
  userAvatar?: string
  userNickname?: string
  lastMessageTime?: string
  lastMessageContent?: string
  lastMessageContentType?: number
  adminPinned?: boolean
  userDeleted?: boolean
  adminDeleted?: boolean
  adminUnreadMessageCount?: number
  createTime?: string
}

/** 获取客服会话列表 */
export function getPromotionKefuConversationList() {
  return http.get<PromotionKefuConversation[]>('/promotion/kefu-conversation/list')
}

/** 获取客服会话详情 */
export function getPromotionKefuConversation(id: number) {
  return http.get<PromotionKefuConversation>(`/promotion/kefu-conversation/get?id=${id}`)
}

/** 更新客服会话置顶状态 */
export function updatePromotionKefuConversationPinned(data: Record<string, any>) {
  return http.put<boolean>('/promotion/kefu-conversation/update-conversation-pinned', data)
}

/** 删除客服会话 */
export function deletePromotionKefuConversation(id: number) {
  return http.delete<boolean>(`/promotion/kefu-conversation/delete?id=${id}`)
}
