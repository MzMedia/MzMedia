import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 会员分组 */
export interface MemberGroup {
  id?: number
  name?: string
  remark?: string
  status?: number
  createTime?: string
}

/** 获取会员分组分页列表 */
export function getMemberGroupPage(params: PageParam) {
  return http.get<PageResult<MemberGroup>>('/member/group/page', params)
}

/** 获取会员分组详情 */
export function getMemberGroup(id: number) {
  return http.get<MemberGroup>(`/member/group/get?id=${id}`)
}

/** 获取会员分组精简列表 */
export function getSimpleMemberGroupList() {
  return http.get<MemberGroup[]>('/member/group/list-all-simple')
}

/** 创建会员分组 */
export function createMemberGroup(data: MemberGroup) {
  return http.post<number>('/member/group/create', data)
}

/** 更新会员分组 */
export function updateMemberGroup(data: MemberGroup) {
  return http.put<boolean>('/member/group/update', data)
}

/** 删除会员分组 */
export function deleteMemberGroup(id: number) {
  return http.delete<boolean>(`/member/group/delete?id=${id}`)
}
