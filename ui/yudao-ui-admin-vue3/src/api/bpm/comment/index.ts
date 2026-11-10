import request from '@/config/axios'

export interface CommentVO {
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
  createTime: string
  user?: {
    id: number
    nickname: string
    avatar?: string
    deptName?: string
  }
}

// 获得指定流程实例的评论列表
export const getCommentListByProcessInstanceId = async (processInstanceId: string) => {
  return await request.get({
    url: '/bpm/comment/list-by-process-instance-id?processInstanceId=' + processInstanceId
  })
}

// 创建流程评论
export const createComment = async (taskId: string, message: string) => {
  return await request.post({ url: '/bpm/comment/create', data: { taskId, message } })
}
