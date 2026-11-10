import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 用户钱包查询参数 */
export interface PayWalletUserReq {
  userId: number
}

/** 钱包余额 */
export interface PayWallet {
  id?: number
  userId?: number
  userType?: number
  balance?: number
  totalExpense?: number
  totalRecharge?: number
  freezePrice?: number
  createTime?: string
}

/** 钱包余额分页参数 */
export interface PayWalletPageParam extends PageParam {
  userId?: number
  userType?: number
  createTime?: string[]
}

/** 查询用户钱包详情 */
export function getPayWallet(params: PayWalletUserReq) {
  return http.get<PayWallet>('/pay/wallet/get', params)
}

/** 查询钱包列表 */
export function getPayWalletPage(params: PayWalletPageParam) {
  return http.get<PageResult<PayWallet>>('/pay/wallet/page', params)
}

/** 修改钱包余额 */
export function updatePayWalletBalance(data: Record<string, any>) {
  return http.put<boolean>('/pay/wallet/update-balance', data)
}
