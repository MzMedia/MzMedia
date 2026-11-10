import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品收藏 */
export interface ProductFavorite {
  id?: number
  userId?: number
  spuId?: number
  picUrl?: string
  name?: string
  price?: number
  salesCount?: number
  status?: number
  createTime?: string
}

/** 获取商品收藏分页列表 */
export function getProductFavoritePage(params: PageParam) {
  return http.get<PageResult<ProductFavorite>>('/product/favorite/page', params)
}
