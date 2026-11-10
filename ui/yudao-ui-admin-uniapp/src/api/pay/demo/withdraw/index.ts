import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 示例提现单 */
export interface PayDemoWithdraw {
  id?: number
  subject?: string
  price?: number
  userName?: string
  userAccount?: string
  type?: number
  status?: number
  payTransferId?: number
  transferChannelCode?: string
  transferTime?: string
  transferErrorMsg?: string
  createTime?: string
}

/** 获取示例提现单分页列表 */
export function getPayDemoWithdrawPage(params: PageParam) {
  return http.get<PageResult<PayDemoWithdraw>>('/pay/demo-withdraw/page', params)
}

/** 创建示例提现单 */
export function createPayDemoWithdraw(data: PayDemoWithdraw) {
  return http.post<number>('/pay/demo-withdraw/create', data)
}

/** 发起示例提现单转账 */
export function transferPayDemoWithdraw(id: number) {
  return http.post<number>('/pay/demo-withdraw/transfer', undefined, { id })
}
