import type { TransferReq } from '@/api/crm/permission'
import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import { downloadApiFile } from '@/utils/download'

/** 联系人 */
export interface Contact {
  id?: number
  name: string
  customerId?: number
  customerName?: string
  contactLastTime?: Date | string | number
  contactLastContent?: string
  contactNextTime?: Date | string | number
  ownerUserId?: number
  ownerUserName?: string
  ownerUserDept?: string
  mobile?: string
  telephone?: string
  qq?: string
  wechat?: string
  email?: string
  areaId?: number
  areaName?: string
  detailAddress?: string
  sex?: number
  master?: boolean
  post?: string
  parentId?: number
  parentName?: string
  remark?: string
  businessId?: number
  creator?: string
  creatorName?: string
  createTime?: Date | string
  updateTime?: Date | string
}

export interface ContactBusinessReq {
  contactId: number
  businessIds: number[]
}

export interface ContactBusiness2Req {
  businessId: number
  contactIds: number[]
}

/** 查询联系人分页列表 */
export function getContactPage(params: PageParam) {
  return http.get<PageResult<Contact>>('/crm/contact/page', params)
}

/** 查询指定客户下的联系人分页列表 */
export function getContactPageByCustomer(params: PageParam) {
  return http.get<PageResult<Contact>>('/crm/contact/page-by-customer', params)
}

/** 查询指定客户下的联系人列表（不分页） */
export function getContactListByCustomer(customerId: number) {
  return http.get<Contact[]>(`/crm/contact/list-by-customer?customerId=${customerId}`)
}

/** 查询指定商机下的联系人分页列表 */
export function getContactPageByBusiness(params: PageParam) {
  return http.get<PageResult<Contact>>('/crm/contact/page-by-business', params)
}

/** 查询联系人详情 */
export function getContact(id: number) {
  return http.get<Contact>(`/crm/contact/get?id=${id}`)
}

/** 新增联系人 */
export function createContact(data: Contact) {
  return http.post<number>('/crm/contact/create', data)
}

/** 修改联系人 */
export function updateContact(data: Contact) {
  return http.put<boolean>('/crm/contact/update', data)
}

/** 删除联系人 */
export function deleteContact(id: number) {
  return http.delete<boolean>(`/crm/contact/delete?id=${id}`)
}

/** 导出联系人 Excel */
export function exportContact(params: Record<string, any>) {
  return downloadApiFile('/crm/contact/export-excel', params, '联系人.xls')
}

/** 获得联系人精简列表 */
export function getSimpleContactList() {
  return http.get<Contact[]>('/crm/contact/simple-all-list')
}

/** 批量新增联系人商机关联 */
export function createContactBusinessList(data: ContactBusinessReq) {
  return http.post<boolean>('/crm/contact/create-business-list', data)
}

/** 批量新增商机联系人关联 */
export function createContactBusinessList2(data: ContactBusiness2Req) {
  return http.post<boolean>('/crm/contact/create-business-list2', data)
}

/** 解除联系人商机关联 */
export function deleteContactBusinessList(data: ContactBusinessReq) {
  return http.delete<boolean>('/crm/contact/delete-business-list', data)
}

/** 解除商机联系人关联 */
export function deleteContactBusinessList2(data: ContactBusiness2Req) {
  return http.delete<boolean>('/crm/contact/delete-business-list2', data)
}

/** 联系人转移 */
export function transferContact(data: TransferReq) {
  return http.put<boolean>('/crm/contact/transfer', data)
}
