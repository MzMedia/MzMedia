import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 定时任务日志信息 */
export interface JobLog {
  id?: number
  jobId: number
  handlerName: string
  handlerParam: string
  cronExpression: string
  executeIndex: string
  beginTime: Date
  endTime: Date
  duration: string
  status: number
  createTime?: string
  result: string
}

/** 获取定时任务日志分页列表 */
export function getJobLogPage(params: PageParam) {
  return http.get<PageResult<JobLog>>('/infra/job-log/page', params)
}

/** 获取定时任务日志详情 */
export function getJobLog(id: number) {
  return http.get<JobLog>(`/infra/job-log/get?id=${id}`)
}
