import { http } from '@/http/http'

/** 客服消息 */
export interface PromotionKefuMessage {
  id?: number
  conversationId?: number
  senderId?: number
  senderAvatar?: string
  senderType?: number
  receiverId?: number
  receiverType?: number
  contentType?: number
  content?: string
  readStatus?: boolean
  createTime?: string
}

/** 发送客服消息 */
export function sendPromotionKefuMessage(data: Record<string, any>) {
  return http.post<boolean>('/promotion/kefu-message/send', data)
}

/** 更新客服消息已读状态 */
export function updatePromotionKefuMessageReadStatus(conversationId: number) {
  return http.put<boolean>(`/promotion/kefu-message/update-read-status?conversationId=${conversationId}`)
}

/** 获取客服消息列表 */
export function getPromotionKefuMessageList(params: Record<string, any>) {
  return http.get<PromotionKefuMessage[]>('/promotion/kefu-message/list', params)
}
