import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 会员签到记录 */
export interface MemberSignInRecord {
  id: number
  userId: number
  nickname?: string
  day?: number
  point?: number
  createTime?: string
}

/** 获取会员签到记录分页列表 */
export function getMemberSignInRecordPage(params: PageParam) {
  return http.get<PageResult<MemberSignInRecord>>('/member/sign-in/record/page', params)
}
