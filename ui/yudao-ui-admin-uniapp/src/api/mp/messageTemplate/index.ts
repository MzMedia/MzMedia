import { http } from '@/http/http'

const baseUrl = '/mp/message-template'

/** 公众号消息模板 */
export interface MsgTemplate {
  id: number
  accountId: number
  appId: string
  templateId: string
  title: string
  content: string
  example: string
  primaryIndustry: string
  deputyIndustry: string
  createTime?: Date
}

/** 发送消息模板请求 */
export interface MsgTemplateSend {
  id: number
  userId: number
  data?: Record<string, string>
  url?: string
  miniProgramAppId?: string
  miniProgramPagePath?: string
  miniprogram?: string
}

/** 获取消息模板列表 */
export function getMessageTemplateList(params: Record<string, any>) {
  return http.get<MsgTemplate[]>(`${baseUrl}/list`, params)
}

/** 获取消息模板详情 */
export function getMessageTemplate(id: number) {
  return http.get<MsgTemplate>(`${baseUrl}/get?id=${id}`)
}

/** 删除消息模板 */
export function deleteMessageTemplate(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}

/** 同步公众号模板 */
export function syncMessageTemplate(accountId: number) {
  return http.post<boolean>(`${baseUrl}/sync?accountId=${accountId}`)
}

/** 发送消息模板 */
export function sendMessageTemplate(data: MsgTemplateSend) {
  return http.post<boolean>(`${baseUrl}/send`, data)
}
