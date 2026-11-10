import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 回款 */
export interface Receivable {
  id?: number
  no?: string
  planId?: number
  customerId?: number
  customerName?: string
  contractId?: number
  contract?: {
    id?: number
    name?: string
    no?: string
    totalPrice?: number
  }
  auditStatus?: number
  processInstanceId?: number
  returnTime?: Date | string | number
  returnType?: number
  price?: number
  ownerUserId?: number
  ownerUserName?: string
  remark?: string
  creator?: string
  creatorName?: string
  createTime?: Date | string
  updateTime?: Date | string
}

/** 查询回款分页列表 */
export function getReceivablePage(params: PageParam) {
  return http.get<PageResult<Receivable>>('/crm/receivable/page', params)
}

/** 查询指定客户下的回款分页列表 */
export function getReceivablePageByCustomer(params: PageParam) {
  return http.get<PageResult<Receivable>>('/crm/receivable/page-by-customer', params)
}

/** 查询回款详情 */
export function getReceivable(id: number) {
  return http.get<Receivable>(`/crm/receivable/get?id=${id}`)
}

/** 新增回款 */
export function createReceivable(data: Receivable) {
  return http.post<number>('/crm/receivable/create', data)
}

/** 修改回款 */
export function updateReceivable(data: Receivable) {
  return http.put<boolean>('/crm/receivable/update', data)
}

/** 删除回款 */
export function deleteReceivable(id: number) {
  return http.delete<boolean>(`/crm/receivable/delete?id=${id}`)
}

/** 导出回款 Excel */
export function exportReceivable(params: Record<string, any>) {
  return downloadApiFile('/crm/receivable/export-excel', params, '回款.xls')
}

/** 提交回款审核 */
export function submitReceivable(id: number) {
  return http.put<boolean>(`/crm/receivable/submit?id=${id}`)
}

/** 获得待审核回款数量 */
export function getAuditReceivableCount() {
  return http.get<number>('/crm/receivable/audit-count')
}
