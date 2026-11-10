import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 优惠券 */
export interface PromotionCoupon {
  id?: number
  templateId?: number
  name?: string
  userId?: number
  discountType?: number
  discountPercent?: number
  discountPrice?: number
  discountLimitPrice?: number
  usePrice?: number
  takeType?: number
  status?: number
  createTime?: string
  useTime?: string
}

/** 获取优惠券分页列表 */
export function getPromotionCouponPage(params: PageParam) {
  return http.get<PageResult<PromotionCoupon>>('/promotion/coupon/page', params)
}

/** 获取优惠券详情 */
export function getPromotionCoupon(id: number) {
  return http.get<PromotionCoupon>(`/promotion/coupon/get?id=${id}`)
}

/** 回收优惠券 */
export function deletePromotionCoupon(id: number) {
  return http.delete<boolean>(`/promotion/coupon/delete?id=${id}`)
}

/** 发送优惠券 */
export function sendPromotionCoupon(data: { templateId: number, userIds: number[] }) {
  return http.post<boolean>('/promotion/coupon/send', data)
}
