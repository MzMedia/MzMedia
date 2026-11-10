import type { TransferReq } from '@/api/crm/permission'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 线索 */
export interface Clue {
  id?: number
  name: string
  followUpStatus?: boolean
  contactLastTime?: Date | string | number
  contactLastContent?: string
  contactNextTime?: Date | string | number
  ownerUserId?: number
  ownerUserName?: string
  ownerUserDept?: string
  transformStatus?: boolean
  customerId?: number
  customerName?: string
  mobile?: string
  telephone?: string
  qq?: string
  wechat?: string
  email?: string
  areaId?: number
  areaName?: string
  detailAddress?: string
  industryId?: number
  level?: number
  source?: number
  remark?: string
  creator?: string
  creatorName?: string
  createTime?: Date | string
  updateTime?: Date | string
}

/** 查询线索分页列表 */
export function getCluePage(params: PageParam) {
  return http.get<PageResult<Clue>>('/crm/clue/page', params)
}

/** 查询线索详情 */
export function getClue(id: number) {
  return http.get<Clue>(`/crm/clue/get?id=${id}`)
}

/** 新增线索 */
export function createClue(data: Clue) {
  return http.post<number>('/crm/clue/create', data)
}

/** 修改线索 */
export function updateClue(data: Clue) {
  return http.put<boolean>('/crm/clue/update', data)
}

/** 删除线索 */
export function deleteClue(id: number) {
  return http.delete<boolean>(`/crm/clue/delete?id=${id}`)
}

/** 导出线索 Excel */
export function exportClue(params: Record<string, any>) {
  return downloadApiFile('/crm/clue/export-excel', params, '线索.xls')
}

/** 线索转移 */
export function transferClue(data: TransferReq) {
  return http.put<boolean>('/crm/clue/transfer', data)
}

/** 线索转化为客户 */
export function transformClue(id: number) {
  return http.put<boolean>('/crm/clue/transform', undefined, { id })
}

/** 获得分配给我且待跟进的线索数量 */
export function getFollowClueCount() {
  return http.get<number>('/crm/clue/follow-count')
}
