import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 请假申请 */
export interface Leave {
  id: number
  status: number
  type: number
  reason: string
  processInstanceId: string
  startTime: number
  endTime: number
  createTime: Date
  startUserSelectAssignees?: Record<string, number[]>
}

/** 创建请假申请 */
export function createLeave(data: Partial<Leave>) {
  return http.post<number>('/bpm/oa/leave/create', data)
}

/** 获得请假申请 */
export function getLeave(id: number) {
  return http.get<Leave>(`/bpm/oa/leave/get?id=${id}`)
}

/** 获得请假申请分页 */
export function getLeavePage(params: PageParam) {
  return http.get<PageResult<Leave>>('/bpm/oa/leave/page', params)
}
