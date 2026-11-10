import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 定时任务信息 */
export interface Job {
  id?: number
  name: string
  status: number
  handlerName: string
  handlerParam: string
  cronExpression: string
  retryCount: number
  retryInterval: number
  monitorTimeout: number
  createTime?: Date
  nextTimes?: Date[]
}

/** 获取定时任务分页列表 */
export function getJobPage(params: PageParam) {
  return http.get<PageResult<Job>>('/infra/job/page', params)
}

/** 获取定时任务详情 */
export function getJob(id: number) {
  return http.get<Job>(`/infra/job/get?id=${id}`)
}

/** 创建定时任务 */
export function createJob(data: Job) {
  return http.post<number>('/infra/job/create', data)
}

/** 更新定时任务 */
export function updateJob(data: Job) {
  return http.put<boolean>('/infra/job/update', data)
}

/** 删除定时任务 */
export function deleteJob(id: number) {
  return http.delete<boolean>(`/infra/job/delete?id=${id}`)
}

/** 更新定时任务状态 */
export function updateJobStatus(id: number, status: number) {
  return http.put<boolean>('/infra/job/update-status', undefined, { id, status })
}

/** 同步定时任务 */
export function syncJob() {
  return http.post<boolean>('/infra/job/sync')
}

/** 立即执行一次定时任务 */
export function runJob(id: number) {
  return http.put<boolean>(`/infra/job/trigger?id=${id}`)
}

/** 获取定时任务的下 n 次执行时间 */
export function getJobNextTimes(id: number) {
  return http.get<Date[]>(`/infra/job/get_next_times?id=${id}`)
}
