import { http } from '@/http/http'

/** 会员配置 */
export interface MemberConfig {
  id?: number
  pointTradeDeductEnable: boolean
  pointTradeDeductUnitPrice: number
  pointTradeDeductMaxPrice: number
  pointTradeGivePoint: number
}

/** 获取会员配置 */
export function getMemberConfig() {
  return http.get<MemberConfig | null>('/member/config/get')
}

/** 保存会员配置 */
export function saveMemberConfig(data: MemberConfig) {
  return http.put<boolean>('/member/config/save', data)
}
