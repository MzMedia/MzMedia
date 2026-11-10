import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商机状态组 */
export interface BusinessStatusType {
  id?: number
  name: string
  deptIds?: number[]
  deptNames?: string[]
  statuses?: BusinessStatus[]
}

/** 商机阶段 */
export interface BusinessStatus {
  id?: number
  name: string
  percent?: number
  endStatus?: number
  key?: string
}

/** 默认结束阶段 */
export const DEFAULT_STATUSES: BusinessStatus[] = [
  { endStatus: 1, key: '结束', name: '赢单', percent: 100 },
  { endStatus: 2, key: '结束', name: '输单', percent: 0 },
  { endStatus: 3, key: '结束', name: '无效', percent: 0 },
]

/** 查询商机状态组分页列表 */
export function getBusinessStatusPage(params: PageParam) {
  return http.get<PageResult<BusinessStatusType>>('/crm/business-status/page', params)
}

/** 新增商机状态组 */
export function createBusinessStatus(data: BusinessStatusType) {
  return http.post<number>('/crm/business-status/create', data)
}

/** 修改商机状态组 */
export function updateBusinessStatus(data: BusinessStatusType) {
  return http.put<boolean>('/crm/business-status/update', data)
}

/** 查询商机状态组详情 */
export function getBusinessStatus(id: number) {
  return http.get<BusinessStatusType>(`/crm/business-status/get?id=${id}`)
}

/** 删除商机状态组 */
export function deleteBusinessStatus(id: number) {
  return http.delete<boolean>(`/crm/business-status/delete?id=${id}`)
}

/** 获得商机状态组精简列表 */
export function getBusinessStatusTypeSimpleList() {
  return http.get<BusinessStatusType[]>('/crm/business-status/type-simple-list')
}

/** 获得商机阶段精简列表 */
export function getBusinessStatusSimpleList(typeId: number) {
  return http.get<BusinessStatus[]>('/crm/business-status/status-simple-list', { typeId })
}
