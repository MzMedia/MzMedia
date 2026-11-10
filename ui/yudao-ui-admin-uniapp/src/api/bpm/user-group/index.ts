import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/bpm/user-group'

/** 用户组 */
export interface UserGroup {
  id?: number
  name: string // 组名
  description: string // 描述
  userIds: number[] // 成员用户编号数组
  status: number // 状态
  remark: string // 备注
  createTime?: Date // 创建时间
}

/** 获取用户组分页列表 */
export function getUserGroupPage(params: PageParam) {
  return http.get<PageResult<UserGroup>>(`${baseUrl}/page`, params)
}

/** 获取用户组详情 */
export function getUserGroup(id: number) {
  return http.get<UserGroup>(`${baseUrl}/get?id=${id}`)
}

/** 创建用户组 */
export function createUserGroup(data: UserGroup) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新用户组 */
export function updateUserGroup(data: UserGroup) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除用户组 */
export function deleteUserGroup(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}

/** 获取用户组简单列表 */
export function getUserGroupSimpleList() {
  return http.get<UserGroup[]>(`${baseUrl}/simple-list`)
}
