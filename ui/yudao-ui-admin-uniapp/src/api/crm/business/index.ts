import type { TransferReq } from '@/api/crm/permission'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 商机产品 */
export interface BusinessProduct {
  id?: number
  productId?: number
  productName?: string
  productNo?: string
  productUnit?: number
  productPrice?: number
  businessPrice?: number
  count?: number
  totalPrice?: number
}

/** 商机 */
export interface Business {
  id?: number
  name: string
  customerId?: number
  customerName?: string
  contactId?: number // 在【联系人详情】新增商机时传递，用于后端自动关联两者
  followUpStatus?: boolean
  contactLastTime?: Date | string | number
  contactNextTime?: Date | string | number
  ownerUserId?: number
  ownerUserName?: string
  ownerUserDept?: string
  statusTypeId?: number
  statusTypeName?: string
  statusId?: number
  statusName?: string
  endStatus?: number
  endRemark?: string
  dealTime?: Date | string | number
  totalProductPrice?: number
  totalPrice?: number
  discountPercent?: number
  remark?: string
  creator?: string
  creatorName?: string
  createTime?: Date | string
  updateTime?: Date | string
  products?: BusinessProduct[]
}

/** 查询商机分页列表 */
export function getBusinessPage(params: PageParam) {
  return http.get<PageResult<Business>>('/crm/business/page', params)
}

/** 查询指定客户下的商机分页列表 */
export function getBusinessPageByCustomer(params: PageParam) {
  return http.get<PageResult<Business>>('/crm/business/page-by-customer', params)
}

/** 查询指定客户下的商机列表（不分页） */
export function getBusinessListByCustomer(customerId: number) {
  return http.get<Business[]>(`/crm/business/list-by-customer?customerId=${customerId}`)
}

/** 查询商机详情 */
export function getBusiness(id: number) {
  return http.get<Business>(`/crm/business/get?id=${id}`)
}

/** 获得商机精简列表 */
export function getSimpleBusinessList() {
  return http.get<Business[]>('/crm/business/simple-all-list')
}

/** 新增商机 */
export function createBusiness(data: Business) {
  return http.post<number>('/crm/business/create', data)
}

/** 修改商机 */
export function updateBusiness(data: Business) {
  return http.put<boolean>('/crm/business/update', data)
}

/** 修改商机状态 */
export function updateBusinessStatus(data: Business) {
  return http.put<boolean>('/crm/business/update-status', data)
}

/** 删除商机 */
export function deleteBusiness(id: number) {
  return http.delete<boolean>(`/crm/business/delete?id=${id}`)
}

/** 导出商机 Excel */
export function exportBusiness(params: Record<string, any>) {
  return downloadApiFile('/crm/business/export-excel', params, '商机.xls')
}

/** 查询指定联系人下的商机分页列表 */
export function getBusinessPageByContact(params: PageParam) {
  return http.get<PageResult<Business>>('/crm/business/page-by-contact', params)
}

/** 查询指定联系人下的商机列表（不分页） */
export function getBusinessListByContact(contactId: number) {
  return http.get<Business[]>(`/crm/business/list-by-contact?contactId=${contactId}`)
}

/** 商机转移 */
export function transferBusiness(data: TransferReq) {
  return http.put<boolean>('/crm/business/transfer', data)
}
