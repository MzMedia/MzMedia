import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/mp/tag'

/** 公众号标签 */
export interface Tag {
  id?: number
  tagId?: number
  name: string
  accountId: number
  count?: number
  createTime?: Date
}

/** 创建公众号标签 */
export function createTag(data: Tag) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新公众号标签 */
export function updateTag(data: Tag) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除公众号标签 */
export function deleteTag(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}

/** 获取公众号标签详情 */
export function getTag(id: number) {
  return http.get<Tag>(`${baseUrl}/get?id=${id}`)
}

/** 获取公众号标签分页 */
export function getTagPage(params: PageParam) {
  return http.get<PageResult<Tag>>(`${baseUrl}/page`, params)
}

/** 获取公众号标签精简列表 */
export function getSimpleTagList() {
  return http.get<Tag[]>(`${baseUrl}/list-all-simple`)
}

/** 同步公众号标签 */
export function syncTag(accountId: number) {
  return http.post<boolean>(`${baseUrl}/sync?accountId=${accountId}`)
}
