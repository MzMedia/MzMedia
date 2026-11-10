import { http } from '@/http/http'

/** 示例分类（树表） */
export interface Demo02Category {
  id?: number
  name: string // 名字
  parentId?: number // 父级编号
  createTime?: number // 创建时间
  children?: Demo02Category[] // 子分类（前端建树用）
}

/** 获取示例分类列表（无分页，前端建树） */
export function getDemo02CategoryList(params?: { name?: string }) {
  return http.get<Demo02Category[]>('/infra/demo02-category/list', params)
}

/** 获取示例分类详情 */
export function getDemo02Category(id: number) {
  return http.get<Demo02Category>(`/infra/demo02-category/get?id=${id}`)
}

/** 创建示例分类 */
export function createDemo02Category(data: Demo02Category) {
  return http.post<number>('/infra/demo02-category/create', data)
}

/** 更新示例分类 */
export function updateDemo02Category(data: Demo02Category) {
  return http.put<boolean>('/infra/demo02-category/update', data)
}

/** 删除示例分类 */
export function deleteDemo02Category(id: number) {
  return http.delete<boolean>(`/infra/demo02-category/delete?id=${id}`)
}
