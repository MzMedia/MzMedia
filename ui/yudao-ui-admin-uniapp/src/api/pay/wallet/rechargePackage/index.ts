import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 钱包充值套餐 */
export interface PayWalletRechargePackage {
  id?: number
  name?: string
  payPrice?: number
  bonusPrice?: number
  status?: number
  createTime?: string
}

/** 钱包充值套餐分页参数 */
export interface PayWalletRechargePackagePageParam extends PageParam {
  name?: string
  status?: number
  createTime?: string[]
}

/** 获取钱包充值套餐分页列表 */
export function getPayWalletRechargePackagePage(params: PayWalletRechargePackagePageParam) {
  return http.get<PageResult<PayWalletRechargePackage>>('/pay/wallet-recharge-package/page', params)
}

/** 获取钱包充值套餐详情 */
export function getPayWalletRechargePackage(id: number) {
  return http.get<PayWalletRechargePackage>(`/pay/wallet-recharge-package/get?id=${id}`)
}

/** 创建钱包充值套餐 */
export function createPayWalletRechargePackage(data: PayWalletRechargePackage) {
  return http.post<number>('/pay/wallet-recharge-package/create', data)
}

/** 更新钱包充值套餐 */
export function updatePayWalletRechargePackage(data: PayWalletRechargePackage) {
  return http.put<boolean>('/pay/wallet-recharge-package/update', data)
}

/** 删除钱包充值套餐 */
export function deletePayWalletRechargePackage(id: number) {
  return http.delete<boolean>(`/pay/wallet-recharge-package/delete?id=${id}`)
}
