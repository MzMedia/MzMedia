import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品规格属性 */
export interface ProductSkuProperty {
  propertyId?: number
  propertyName?: string
  valueId?: number
  valueName?: string
}

/** 商品 SKU */
export interface ProductSku {
  id?: number
  name?: string
  spuId?: number
  properties?: ProductSkuProperty[]
  price?: number | string
  marketPrice?: number | string
  costPrice?: number | string
  barCode?: string
  picUrl?: string
  stock?: number
  weight?: number
  volume?: number
  firstBrokeragePrice?: number | string
  secondBrokeragePrice?: number | string
  salesCount?: number
}

/** 商品 SPU */
export interface ProductSpu {
  id?: number
  name?: string
  categoryId?: number
  keyword?: string
  picUrl?: string
  sliderPicUrls?: string[]
  introduction?: string
  deliveryTypes?: number[]
  deliveryTemplateId?: number
  brandId?: number
  specType?: boolean
  subCommissionType?: boolean
  skus?: ProductSku[]
  description?: string
  sort?: number
  giveIntegral?: number
  virtualSalesCount?: number
  price?: number
  combinationPrice?: number
  seckillPrice?: number
  salesCount?: number
  marketPrice?: number
  costPrice?: number
  stock?: number
  status?: number
  createTime?: string
}

/** 获取商品 SPU 分页列表 */
export function getProductSpuPage(params: PageParam) {
  return http.get<PageResult<ProductSpu>>('/product/spu/page', params)
}

/** 获取商品 SPU 详情 */
export function getProductSpu(id: number) {
  return http.get<ProductSpu>(`/product/spu/get-detail?id=${id}`)
}

/** 创建商品 SPU */
export function createProductSpu(data: ProductSpu) {
  return http.post<number>('/product/spu/create', data as Record<string, any>)
}

/** 更新商品 SPU */
export function updateProductSpu(data: ProductSpu) {
  return http.put<boolean>('/product/spu/update', data as Record<string, any>)
}

/** 删除商品 SPU */
export function deleteProductSpu(id: number) {
  return http.delete<boolean>(`/product/spu/delete?id=${id}`)
}

/** 更新商品 SPU 状态 */
export function updateProductSpuStatus(data: { id: number, status: number }) {
  return http.put<boolean>('/product/spu/update-status', data)
}

/** 获取商品状态数量 */
export function getProductSpuTabsCount(params?: Record<string, any>) {
  return http.get<Record<string, number>>('/product/spu/get-count', params)
}

/** 获取商品 SPU 精简列表 */
export function getSimpleProductSpuList() {
  return http.get<ProductSpu[]>('/product/spu/list-all-simple')
}

/** 获取商品 SPU 详情列表 */
export function getProductSpuDetailList(ids: number[]) {
  return http.get<ProductSpu[]>(`/product/spu/list?spuIds=${ids.join(',')}`)
}
