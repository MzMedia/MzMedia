import type { TransferReq } from '@/api/crm/permission'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 合同产品 */
export interface ContractProduct {
  id?: number
  productId?: number
  productName?: string
  productNo?: string
  productUnit?: number
  productPrice?: number
  contractPrice?: number
  count?: number
  totalPrice?: number
}

/** 合同 */
export interface Contract {
  id?: number
  name: string
  no?: string
  customerId?: number
  customerName?: string
  businessId?: number
  businessName?: string
  contactLastTime?: Date | string | number
  ownerUserId?: number
  ownerUserName?: string
  ownerUserDeptName?: string
  processInstanceId?: number
  auditStatus?: number
  orderDate?: Date | string | number
  startTime?: Date | string | number
  endTime?: Date | string | number
  totalProductPrice?: number
  discountPercent?: number
  totalPrice?: number
  totalReceivablePrice?: number
  signContactId?: number
  signContactName?: string
  signUserId?: number
  signUserName?: string
  remark?: string
  createTime?: Date | string
  creator?: string
  creatorName?: string
  updateTime?: Date | string
  products?: ContractProduct[]
}

/** 查询合同分页列表 */
export function getContractPage(params: PageParam) {
  return http.get<PageResult<Contract>>('/crm/contract/page', params)
}

/** 查询指定客户下的合同分页列表 */
export function getContractPageByCustomer(params: PageParam) {
  return http.get<PageResult<Contract>>('/crm/contract/page-by-customer', params)
}

/** 查询指定商机下的合同分页列表 */
export function getContractPageByBusiness(params: PageParam) {
  return http.get<PageResult<Contract>>('/crm/contract/page-by-business', params)
}

/** 查询合同详情 */
export function getContract(id: number) {
  return http.get<Contract>(`/crm/contract/get?id=${id}`)
}

/** 查询合同精简列表 */
export function getContractSimpleList(customerId: number) {
  return http.get<Contract[]>(`/crm/contract/simple-list?customerId=${customerId}`)
}

/** 新增合同 */
export function createContract(data: Contract) {
  return http.post<number>('/crm/contract/create', data)
}

/** 修改合同 */
export function updateContract(data: Contract) {
  return http.put<boolean>('/crm/contract/update', data)
}

/** 删除合同 */
export function deleteContract(id: number) {
  return http.delete<boolean>(`/crm/contract/delete?id=${id}`)
}

/** 导出合同 Excel */
export function exportContract(params: Record<string, any>) {
  return downloadApiFile('/crm/contract/export-excel', params, '合同.xls')
}

/** 提交合同审核 */
export function submitContract(id: number) {
  return http.put<boolean>(`/crm/contract/submit?id=${id}`)
}

/** 合同转移 */
export function transferContract(data: TransferReq) {
  return http.put<boolean>('/crm/contract/transfer', data)
}

/** 获得待审核合同数量 */
export function getAuditContractCount() {
  return http.get<number>('/crm/contract/audit-count')
}

/** 获得即将到期合同数量 */
export function getRemindContractCount() {
  return http.get<number>('/crm/contract/remind-count')
}
