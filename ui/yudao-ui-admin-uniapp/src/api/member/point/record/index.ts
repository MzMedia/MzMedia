import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 会员积分记录 */
export interface MemberPointRecord {
  id: number
  userId: number
  nickname?: string
  bizId?: string
  bizType?: number
  title?: string
  description?: string
  point?: number
  totalPoint?: number
  createTime?: string
}

/** 获取会员积分记录分页列表 */
export function getMemberPointRecordPage(params: PageParam) {
  return http.get<PageResult<MemberPointRecord>>('/member/point/record/page', params)
}
