import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 会员经验记录 */
export interface MemberExperienceRecord {
  id: number
  userId: number
  bizId?: string
  bizType?: number
  title?: string
  description?: string
  experience?: number
  totalExperience?: number
  createTime?: string
}

/** 获取会员经验记录分页列表 */
export function getMemberExperienceRecordPage(params: PageParam) {
  return http.get<PageResult<MemberExperienceRecord>>('/member/experience-record/page', params)
}
