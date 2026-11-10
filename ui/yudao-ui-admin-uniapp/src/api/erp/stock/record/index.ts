import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 库存明细 */
export interface StockRecord {
  id?: number // 编号
  productId?: number // 产品编号
  productName?: string // 产品名称
  categoryName?: string // 产品分类名称
  unitName?: string // 产品单位名称
  warehouseId?: number // 仓库编号
  warehouseName?: string // 仓库名称
  count?: number // 出入库数量
  totalCount?: number // 总库存量
  bizType?: number // 业务类型
  bizId?: number // 业务编号
  bizItemId?: number // 业务项编号
  bizNo?: string // 业务单号
  creator?: string // 创建人
  creatorName?: string // 创建人名称
  createTime?: Date // 创建时间
}

/** 获取库存明细分页列表 */
export function getStockRecordPage(params: PageParam) {
  return http.get<PageResult<StockRecord>>('/erp/stock-record/page', params)
}

/** 获取库存明细详情 */
export function getStockRecord(id: number) {
  return http.get<StockRecord>(`/erp/stock-record/get?id=${id}`)
}
