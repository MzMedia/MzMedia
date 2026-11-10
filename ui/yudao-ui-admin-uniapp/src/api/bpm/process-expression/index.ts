import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/bpm/process-expression'

/** 流程表达式 */
export interface ProcessExpression {
  id?: number
  name: string // 表达式名字
  status: number // 表达式状态
  expression: string // 表达式
  createTime?: Date // 创建时间
}

/** 获取流程表达式分页列表 */
export function getProcessExpressionPage(params: PageParam) {
  return http.get<PageResult<ProcessExpression>>(`${baseUrl}/page`, params)
}

/** 获取流程表达式详情 */
export function getProcessExpression(id: number) {
  return http.get<ProcessExpression>(`${baseUrl}/get?id=${id}`)
}

/** 创建流程表达式 */
export function createProcessExpression(data: ProcessExpression) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新流程表达式 */
export function updateProcessExpression(data: ProcessExpression) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除流程表达式 */
export function deleteProcessExpression(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}
