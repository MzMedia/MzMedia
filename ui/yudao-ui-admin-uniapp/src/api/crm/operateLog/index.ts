import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** CRM 操作日志 */
export interface OperateLog {
  id?: number
  bizType: number
  bizId: number
  type?: string
  subType?: string
  action?: string
  userId?: number
  userName?: string
  userType?: number
  createTime?: Date | string
}

/** 获得操作日志分页 */
export function getOperateLogPage(params: PageParam & { bizType: number, bizId: number }) {
  return http.get<PageResult<OperateLog>>('/crm/operate-log/page', params)
}
