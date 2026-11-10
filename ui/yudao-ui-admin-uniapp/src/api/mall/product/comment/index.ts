import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 商品评论 */
export interface ProductComment {
  id?: number
  userId?: number
  userNickname?: string
  userAvatar?: string
  anonymous?: boolean
  orderId?: number
  orderItemId?: number
  spuId?: number
  spuName?: string
  skuId?: number
  visible?: boolean
  scores?: number
  descriptionScores?: number
  benefitScores?: number
  content?: string
  picUrls?: string[]
  replyStatus?: boolean
  replyUserId?: number
  replyContent?: string
  replyTime?: string
  createTime?: string
}

/** 获取商品评论分页列表 */
export function getProductCommentPage(params: PageParam) {
  return http.get<PageResult<ProductComment>>('/product/comment/page', params)
}

/** 获取商品评论详情 */
export function getProductComment(id: number) {
  return http.get<ProductComment>(`/product/comment/get?id=${id}`)
}

/** 创建商品评论 */
export function createProductComment(data: ProductComment) {
  return http.post<number>('/product/comment/create', data as Record<string, any>)
}

/** 显示或隐藏商品评论 */
export function updateProductCommentVisible(data: { id: number, visible: boolean }) {
  return http.put<boolean>('/product/comment/update-visible', data)
}

/** 商家回复商品评论 */
export function replyProductComment(data: { id: number, replyContent: string }) {
  return http.put<boolean>('/product/comment/reply', data)
}
