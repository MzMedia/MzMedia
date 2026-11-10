import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** WMS 往来企业 */
export interface Merchant {
  id?: number
  code?: string
  name?: string
  type?: number
  level?: string
  bankName?: string
  bankAccount?: string
  address?: string
  mobile?: string
  telephone?: string
  contact?: string
  email?: string
  remark?: string
  createTime?: Date
}

/** WMS 往来企业精简列表查询参数 */
export interface MerchantSimpleListReq {
  types?: number[]
}

/** 查询往来企业分页 */
export function getMerchantPage(params: PageParam) {
  return http.get<PageResult<Merchant>>('/wms/merchant/page', params)
}

/** 查询往来企业精简列表 */
export function getSimpleMerchantList(params?: MerchantSimpleListReq) {
  return http.get<Merchant[]>('/wms/merchant/simple-list', params)
}

/** 查询往来企业详情 */
export function getMerchant(id: number) {
  return http.get<Merchant>(`/wms/merchant/get?id=${id}`)
}

/** 新增往来企业 */
export function createMerchant(data: Merchant) {
  return http.post<number>('/wms/merchant/create', data)
}

/** 修改往来企业 */
export function updateMerchant(data: Merchant) {
  return http.put<boolean>('/wms/merchant/update', data)
}

/** 删除往来企业 */
export function deleteMerchant(id: number) {
  return http.delete<boolean>(`/wms/merchant/delete?id=${id}`)
}
