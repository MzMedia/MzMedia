import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/bpm/category'

/** 流程分类 */
export interface Category {
  id?: number
  name: string // 分类名
  code: string // 分类标志
  status: number // 分类状态
  description?: string // 分类描述
  sort: number // 分类排序
  createTime?: Date // 创建时间
}

/** 获取流程分类分页列表 */
export function getCategoryPage(params: PageParam) {
  return http.get<PageResult<Category>>(`${baseUrl}/page`, params)
}

/** 获取流程分类详情 */
export function getCategory(id: number) {
  return http.get<Category>(`${baseUrl}/get?id=${id}`)
}

/** 创建流程分类 */
export function createCategory(data: Category) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新流程分类 */
export function updateCategory(data: Category) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除流程分类 */
export function deleteCategory(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}

/** 获取流程分类简单列表 */
export function getCategorySimpleList() {
  return http.get<Category[]>(`${baseUrl}/simple-list`)
}

/** 批量修改流程分类的排序 */
export function updateCategorySortBatch(ids: number[]) {
  const params = ids.join(',')
  return http.put<boolean>(`${baseUrl}/update-sort-batch?ids=${params}`)
}
