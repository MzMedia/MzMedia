import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'
import type { TradeOrder } from '@/api/mall/trade/order'

/** 获取自提订单分页列表 */
export function getDeliveryPickUpOrderPage(params: PageParam) {
  return http.get<PageResult<TradeOrder>>('/trade/order/page', {
    ...params,
    deliveryType: params.deliveryType ?? 2,
  })
}
