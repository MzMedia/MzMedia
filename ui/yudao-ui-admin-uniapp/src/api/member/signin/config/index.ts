import { http } from '@/http/http'

/** 会员签到规则 */
export interface MemberSignInConfig {
  id?: number
  day?: number
  point?: number
  experience?: number
  status?: number
  createTime?: string
}

/** 获取会员签到规则列表 */
export function getMemberSignInConfigList() {
  return http.get<MemberSignInConfig[]>('/member/sign-in/config/list')
}

/** 获取会员签到规则详情 */
export function getMemberSignInConfig(id: number) {
  return http.get<MemberSignInConfig>(`/member/sign-in/config/get?id=${id}`)
}

/** 创建会员签到规则 */
export function createMemberSignInConfig(data: MemberSignInConfig) {
  return http.post<number>('/member/sign-in/config/create', data)
}

/** 更新会员签到规则 */
export function updateMemberSignInConfig(data: MemberSignInConfig) {
  return http.put<boolean>('/member/sign-in/config/update', data)
}

/** 删除会员签到规则 */
export function deleteMemberSignInConfig(id: number) {
  return http.delete<boolean>(`/member/sign-in/config/delete?id=${id}`)
}
