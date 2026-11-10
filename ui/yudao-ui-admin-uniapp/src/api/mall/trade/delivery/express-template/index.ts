import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 运费模板计费区域 */
export interface DeliveryExpressTemplateCharge {
  areaIds?: number[]
  startCount?: number
  startPrice?: number
  extraCount?: number
  extraPrice?: number
}

/** 运费模板包邮区域 */
export interface DeliveryExpressTemplateFree {
  areaIds?: number[]
  freeCount?: number
  freePrice?: number
}

/** 运费模板 */
export interface DeliveryExpressTemplate {
  id?: number
  name?: string
  chargeMode?: number
  sort?: number
  charges?: DeliveryExpressTemplateCharge[]
  frees?: DeliveryExpressTemplateFree[]
  createTime?: string
}

/** 获取运费模板分页列表 */
export function getDeliveryExpressTemplatePage(params: PageParam) {
  return http.get<PageResult<DeliveryExpressTemplate>>('/trade/delivery/express-template/page', params)
}

/** 获取运费模板详情 */
export function getDeliveryExpressTemplate(id: number) {
  return http.get<DeliveryExpressTemplate>(`/trade/delivery/express-template/get?id=${id}`)
}

/** 获取运费模板精简列表 */
export function getSimpleDeliveryExpressTemplateList() {
  return http.get<DeliveryExpressTemplate[]>('/trade/delivery/express-template/list-all-simple')
}

/** 创建运费模板 */
export function createDeliveryExpressTemplate(data: DeliveryExpressTemplate) {
  return http.post<number>('/trade/delivery/express-template/create', data as Record<string, any>)
}

/** 更新运费模板 */
export function updateDeliveryExpressTemplate(data: DeliveryExpressTemplate) {
  return http.put<boolean>('/trade/delivery/express-template/update', data as Record<string, any>)
}

/** 删除运费模板 */
export function deleteDeliveryExpressTemplate(id: number) {
  return http.delete<boolean>(`/trade/delivery/express-template/delete?id=${id}`)
}
