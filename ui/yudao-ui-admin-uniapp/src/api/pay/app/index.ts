import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 支付应用 */
export interface PayApp {
  id?: number
  appKey?: string
  name?: string
  status?: number
  remark?: string
  orderNotifyUrl?: string
  payNotifyUrl?: string
  refundNotifyUrl?: string
  transferNotifyUrl?: string
  merchantId?: number
  merchantName?: string
  channelCodes?: string[]
  createTime?: string
}

/** 支付应用分页参数 */
export interface PayAppPageParam extends PageParam {
  name?: string
  status?: number
  createTime?: string[]
}

/** 支付应用状态修改参数 */
export interface PayAppUpdateStatusReq {
  id: number
  status: number
}

/** 获取支付应用分页列表 */
export function getPayAppPage(params: PayAppPageParam) {
  return http.get<PageResult<PayApp>>('/pay/app/page', params)
}

/** 获取支付应用详情 */
export function getPayApp(id: number) {
  return http.get<PayApp>(`/pay/app/get?id=${id}`)
}

/** 创建支付应用 */
export function createPayApp(data: PayApp) {
  return http.post<number>('/pay/app/create', data)
}

/** 更新支付应用 */
export function updatePayApp(data: PayApp) {
  return http.put<boolean>('/pay/app/update', data)
}

/** 修改支付应用状态 */
export function updatePayAppStatus(data: PayAppUpdateStatusReq) {
  return http.put<boolean>('/pay/app/update-status', data)
}

/** 删除支付应用 */
export function deletePayApp(id: number) {
  return http.delete<boolean>(`/pay/app/delete?id=${id}`)
}

/** 获取支付应用列表 */
export function getPayAppList() {
  return http.get<PayApp[]>('/pay/app/list')
}
