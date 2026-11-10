import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

const baseUrl = '/mp/account'

/** 公众号账号 */
export interface Account {
  id?: number
  name: string
  account: string
  appId: string
  appSecret: string
  token: string
  aesKey?: string
  qrCodeUrl?: string
  remark?: string
  createTime?: Date
}

/** 公众号账号精简信息 */
export interface AccountSimple {
  id: number
  name: string
}

/** 创建公众号账号 */
export function createAccount(data: Account) {
  return http.post<number>(`${baseUrl}/create`, data)
}

/** 更新公众号账号 */
export function updateAccount(data: Account) {
  return http.put<boolean>(`${baseUrl}/update`, data)
}

/** 删除公众号账号 */
export function deleteAccount(id: number) {
  return http.delete<boolean>(`${baseUrl}/delete?id=${id}`)
}

/** 获取公众号账号详情 */
export function getAccount(id: number) {
  return http.get<Account>(`${baseUrl}/get?id=${id}`)
}

/** 获取公众号账号分页 */
export function getAccountPage(params: PageParam) {
  return http.get<PageResult<Account>>(`${baseUrl}/page`, params)
}

/** 获取公众号账号精简列表 */
export function getSimpleAccountList() {
  return http.get<AccountSimple[]>(`${baseUrl}/list-all-simple`)
}

/** 生成公众号二维码 */
export function generateAccountQrCode(id: number) {
  return http.put<boolean>(`${baseUrl}/generate-qr-code?id=${id}`)
}

/** 清空公众号 API 配额 */
export function clearAccountQuota(id: number) {
  return http.put<boolean>(`${baseUrl}/clear-quota?id=${id}`)
}
