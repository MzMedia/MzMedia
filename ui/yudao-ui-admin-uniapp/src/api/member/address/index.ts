import { http } from '@/http/http'

/** 会员收货地址 */
export interface MemberAddress {
  id: number
  name?: string
  mobile?: string
  areaId?: number
  areaName?: string
  detailAddress?: string
  defaultStatus?: boolean
  createTime?: string
}

/** 获取会员收货地址列表 */
export function getMemberAddressList(params: { userId: number }) {
  return http.get<MemberAddress[]>('/member/address/list', params)
}
