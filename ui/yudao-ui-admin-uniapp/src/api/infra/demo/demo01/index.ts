import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 示例联系人（单表） */
export interface Demo01Contact {
  id?: number
  name: string // 名字
  sex?: number // 性别
  birthday?: number // 出生年
  description?: string // 简介
  avatar?: string // 头像
  createTime?: number // 创建时间
}

/** 获取示例联系人分页 */
export function getDemo01ContactPage(params: PageParam) {
  return http.get<PageResult<Demo01Contact>>('/infra/demo01-contact/page', params)
}

/** 获取示例联系人详情 */
export function getDemo01Contact(id: number) {
  return http.get<Demo01Contact>(`/infra/demo01-contact/get?id=${id}`)
}

/** 创建示例联系人 */
export function createDemo01Contact(data: Demo01Contact) {
  return http.post<number>('/infra/demo01-contact/create', data)
}

/** 更新示例联系人 */
export function updateDemo01Contact(data: Demo01Contact) {
  return http.put<boolean>('/infra/demo01-contact/update', data)
}

/** 删除示例联系人 */
export function deleteDemo01Contact(id: number) {
  return http.delete<boolean>(`/infra/demo01-contact/delete?id=${id}`)
}
