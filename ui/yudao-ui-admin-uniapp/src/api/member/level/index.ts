import { http } from '@/http/http'

/** 会员等级 */
export interface MemberLevel {
  id?: number
  name?: string
  level?: number
  experience?: number
  discountPercent?: number
  icon?: string
  backgroundUrl?: string
  status?: number
  createTime?: string
}

/** 获取会员等级列表 */
export function getMemberLevelList(params?: Record<string, any>) {
  return http.get<MemberLevel[]>('/member/level/list', params)
}

/** 获取会员等级详情 */
export function getMemberLevel(id: number) {
  return http.get<MemberLevel>(`/member/level/get?id=${id}`)
}

/** 获取会员等级精简列表 */
export function getSimpleMemberLevelList() {
  return http.get<MemberLevel[]>('/member/level/list-all-simple')
}

/** 创建会员等级 */
export function createMemberLevel(data: MemberLevel) {
  return http.post<number>('/member/level/create', data)
}

/** 更新会员等级 */
export function updateMemberLevel(data: MemberLevel) {
  return http.put<boolean>('/member/level/update', data)
}

/** 删除会员等级 */
export function deleteMemberLevel(id: number) {
  return http.delete<boolean>(`/member/level/delete?id=${id}`)
}
