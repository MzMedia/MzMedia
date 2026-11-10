import type { User } from '@/api/bpm/processInstance'
import { http } from '@/http/http'

/** 流程评论 */
export interface Comment {
  id: string
  taskId?: string
  task?: {
    id: string
    name: string
    taskDefinitionKey: string
  }
  processInstanceId: string
  type: string
  message: string
  createTime: Date | string
  user?: User
}

/** 获得指定流程实例的评论列表 */
export function getCommentListByProcessInstanceId(processInstanceId: string) {
  return http.get<Comment[]>('/bpm/comment/list-by-process-instance-id', { processInstanceId })
}

/** 创建流程评论 */
export function createComment(taskId: string, message: string) {
  return http.post<boolean>('/bpm/comment/create', { taskId, message })
}
