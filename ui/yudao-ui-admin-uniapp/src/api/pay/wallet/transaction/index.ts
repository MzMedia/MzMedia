import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 钱包流水 */
export interface PayWalletTransaction {
  id?: number
  walletId?: number
  title?: string
  price?: number
  balance?: number
  createTime?: string
}

/** 钱包流水分页参数 */
export interface PayWalletTransactionPageParam extends PageParam {
  walletId?: number
}

/** 获取钱包流水分页列表 */
export function getPayWalletTransactionPage(params: PayWalletTransactionPageParam) {
  return http.get<PageResult<PayWalletTransaction>>('/pay/wallet-transaction/page', params)
}
