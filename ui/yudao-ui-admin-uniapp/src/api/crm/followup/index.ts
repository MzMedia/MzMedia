import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 跟进记录 */
export interface FollowUpRecord {
  id?: number
  bizType: number
  bizId: number
  type?: number
  content?: string
  picUrls?: string[]
  fileUrls?: string[]
  nextTime?: Date | string | number
  businessIds?: number[]
  businesses?: { id: number, name: string }[]
  contactIds?: number[]
  contacts?: { id: number, name: string }[]
  creator?: string
  creatorName?: string
  createTime?: Date | string
}

/** 查询跟进记录分页 */
export function getFollowUpRecordPage(params: PageParam) {
  return http.get<PageResult<FollowUpRecord>>('/crm/follow-up-record/page', params)
}

/** 新增跟进记录 */
export function createFollowUpRecord(data: FollowUpRecord) {
  return http.post<number>('/crm/follow-up-record/create', data)
}

/** 删除跟进记录 */
export function deleteFollowUpRecord(id: number) {
  return http.delete<boolean>(`/crm/follow-up-record/delete?id=${id}`)
}
