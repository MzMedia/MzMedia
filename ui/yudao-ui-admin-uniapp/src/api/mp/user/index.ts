import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/mp/user'

/** 公众号粉丝 */
export interface MpUser {
  id?: number
  accountId?: number
  openid?: string
  nickname?: string
  remark?: string
  headImageUrl?: string
  tagIds?: number[]
  subscribeStatus?: number
  subscribeTime?: Date
  createTime?: Date
}

/** 更新公众号粉丝 */
export function updateUser(data: MpUser) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 获取公众号粉丝详情 */
export function getUser(id: number) {
  return http.get<MpUser>(`${baseUrl}/get?id=${id}`)
}

/** 获取公众号粉丝分页 */
export function getUserPage(params: PageParam) {
  return http.get<PageResult<MpUser>>(`${baseUrl}/page`, params)
}

/** 同步公众号粉丝 */
export function syncUser(accountId: number) {
  return http.post<boolean>(`${baseUrl}/sync?accountId=${accountId}`)
}
