import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品浏览记录 */
export interface ProductBrowseHistory {
  id?: number
  userId?: number
  spuId?: number
  userDeleted?: boolean
  createTime?: string
}

/** 商品浏览记录分页参数 */
export interface ProductBrowseHistoryPageParam extends PageParam {
  userId?: number
  userDeleted?: boolean
}

/** 获取商品浏览记录分页 */
export function getProductBrowseHistoryPage(params: ProductBrowseHistoryPageParam) {
  return http.get<PageResult<ProductBrowseHistory>>('/product/browse-history/page', params)
}
