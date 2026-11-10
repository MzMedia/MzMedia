import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 积分商城商品配置 */
export interface PromotionPointProduct {
  id?: number
  activityId?: number
  spuId?: number
  skuId?: number
  count?: number
  point?: number
  price?: number
  stock?: number
  activityStatus?: number
}

/** 积分商城活动 */
export interface PromotionPointActivity {
  id?: number
  spuId?: number
  status?: number
  stock?: number
  totalStock?: number
  remark?: string
  sort?: number
  createTime?: string
  products?: PromotionPointProduct[]
  spuName?: string
  picUrl?: string
  marketPrice?: number
  point?: number
  price?: number
}

/** 获取积分商城活动分页列表 */
export function getPromotionPointActivityPage(params: PageParam) {
  return http.get<PageResult<PromotionPointActivity>>('/promotion/point-activity/page', params)
}

/** 获取积分商城活动详情 */
export function getPromotionPointActivity(id: number) {
  return http.get<PromotionPointActivity>(`/promotion/point-activity/get?id=${id}`)
}

/** 创建积分商城活动 */
export function createPromotionPointActivity(data: PromotionPointActivity) {
  return http.post<number>('/promotion/point-activity/create', data as Record<string, any>)
}

/** 更新积分商城活动 */
export function updatePromotionPointActivity(data: PromotionPointActivity) {
  return http.put<boolean>('/promotion/point-activity/update', data as Record<string, any>)
}

/** 关闭积分商城活动 */
export function closePromotionPointActivity(id: number) {
  return http.put<boolean>(`/promotion/point-activity/close?id=${id}`)
}

/** 删除积分商城活动 */
export function deletePromotionPointActivity(id: number) {
  return http.delete<boolean>(`/promotion/point-activity/delete?id=${id}`)
}

/** 获取积分商城活动列表 */
export function getPromotionPointActivityListByIds(ids: number[]) {
  return http.get<PromotionPointActivity[]>(`/promotion/point-activity/list-by-ids?ids=${ids.join(',')}`)
}
