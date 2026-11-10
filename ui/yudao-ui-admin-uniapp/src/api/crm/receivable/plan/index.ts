import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 回款计划 */
export interface ReceivablePlan {
  id?: number
  period?: number
  receivableId?: number
  price?: number
  returnTime?: Date | string | number
  remindDays?: number
  returnType?: number
  remindTime?: Date | string | number
  customerId?: number
  customerName?: string
  contractId?: number
  contractNo?: string
  ownerUserId?: number
  ownerUserName?: string
  remark?: string
  creator?: string
  creatorName?: string
  createTime?: Date | string
  updateTime?: Date | string
  receivable?: {
    price?: number
    returnTime?: Date | string
  }
}

/** 查询回款计划分页列表 */
export function getReceivablePlanPage(params: PageParam) {
  return http.get<PageResult<ReceivablePlan>>('/crm/receivable-plan/page', params)
}

/** 查询指定客户下的回款计划分页列表 */
export function getReceivablePlanPageByCustomer(params: PageParam) {
  return http.get<PageResult<ReceivablePlan>>('/crm/receivable-plan/page-by-customer', params)
}

/** 查询回款计划详情 */
export function getReceivablePlan(id: number) {
  return http.get<ReceivablePlan>(`/crm/receivable-plan/get?id=${id}`)
}

/** 查询回款计划精简列表 */
export function getReceivablePlanSimpleList(customerId: number, contractId: number) {
  return http.get<ReceivablePlan[]>(`/crm/receivable-plan/simple-list?customerId=${customerId}&contractId=${contractId}`)
}

/** 新增回款计划 */
export function createReceivablePlan(data: ReceivablePlan) {
  return http.post<number>('/crm/receivable-plan/create', data)
}

/** 修改回款计划 */
export function updateReceivablePlan(data: ReceivablePlan) {
  return http.put<boolean>('/crm/receivable-plan/update', data)
}

/** 删除回款计划 */
export function deleteReceivablePlan(id: number) {
  return http.delete<boolean>(`/crm/receivable-plan/delete?id=${id}`)
}

/** 导出回款计划 Excel */
export function exportReceivablePlan(params: Record<string, any>) {
  return downloadApiFile('/crm/receivable-plan/export-excel', params, '回款计划.xls')
}

/** 获得待回款提醒数量 */
export function getReceivablePlanRemindCount() {
  return http.get<number>('/crm/receivable-plan/remind-count')
}
