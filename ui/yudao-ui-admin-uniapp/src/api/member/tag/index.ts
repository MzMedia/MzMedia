import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 会员标签 */
export interface MemberTag {
  id?: number
  name?: string
  createTime?: string
}

/** 获取会员标签分页列表 */
export function getMemberTagPage(params: PageParam) {
  return http.get<PageResult<MemberTag>>('/member/tag/page', params)
}

/** 获取会员标签详情 */
export function getMemberTag(id: number) {
  return http.get<MemberTag>(`/member/tag/get?id=${id}`)
}

/** 获取会员标签精简列表 */
export function getSimpleMemberTagList() {
  return http.get<MemberTag[]>('/member/tag/list-all-simple')
}

/** 创建会员标签 */
export function createMemberTag(data: MemberTag) {
  return http.post<number>('/member/tag/create', data)
}

/** 更新会员标签 */
export function updateMemberTag(data: MemberTag) {
  return http.put<boolean>('/member/tag/update', data)
}

/** 删除会员标签 */
export function deleteMemberTag(id: number) {
  return http.delete<boolean>(`/member/tag/delete?id=${id}`)
}
