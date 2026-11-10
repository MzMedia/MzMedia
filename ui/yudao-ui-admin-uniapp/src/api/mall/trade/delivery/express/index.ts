import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 快递公司 */
export interface DeliveryExpress {
  id?: number
  code: string
  name: string
  logo?: string
  sort: number
  status: number
  createTime?: string
}

/** 获取快递公司分页列表 */
export function getDeliveryExpressPage(params: PageParam) {
  return http.get<PageResult<DeliveryExpress>>('/trade/delivery/express/page', params)
}

/** 获取快递公司详情 */
export function getDeliveryExpress(id: number) {
  return http.get<DeliveryExpress>(`/trade/delivery/express/get?id=${id}`)
}

/** 获取快递公司精简列表 */
export function getSimpleDeliveryExpressList() {
  return http.get<DeliveryExpress[]>('/trade/delivery/express/list-all-simple')
}

/** 创建快递公司 */
export function createDeliveryExpress(data: DeliveryExpress) {
  return http.post<number>('/trade/delivery/express/create', data)
}

/** 更新快递公司 */
export function updateDeliveryExpress(data: DeliveryExpress) {
  return http.put<boolean>('/trade/delivery/express/update', data)
}

/** 删除快递公司 */
export function deleteDeliveryExpress(id: number) {
  return http.delete<boolean>(`/trade/delivery/express/delete?id=${id}`)
}
