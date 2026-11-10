import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/bpm/process-listener'

/** 流程监听器 */
export interface ProcessListener {
  id?: number
  name: string // 监听器名字
  type: string // 监听器类型
  status: number // 监听器状态
  event: string // 监听事件
  valueType: string // 监听器值类型
  value: string // 监听器值
  createTime?: Date // 创建时间
}

/** 获取流程监听器分页列表 */
export function getProcessListenerPage(params: PageParam) {
  return http.get<PageResult<ProcessListener>>(`${baseUrl}/page`, params)
}

/** 获取流程监听器详情 */
export function getProcessListener(id: number) {
  return http.get<ProcessListener>(`${baseUrl}/get?id=${id}`)
}

/** 创建流程监听器 */
export function createProcessListener(data: ProcessListener) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新流程监听器 */
export function updateProcessListener(data: ProcessListener) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除流程监听器 */
export function deleteProcessListener(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}
