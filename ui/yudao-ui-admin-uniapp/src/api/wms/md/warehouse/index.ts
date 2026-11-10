import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** WMS 仓库 */
export interface Warehouse {
  id?: number
  code?: string
  name?: string
  remark?: string
  sort?: number
  createTime?: Date
}

/** 查询仓库分页 */
export function getWarehousePage(params: PageParam) {
  return http.get<PageResult<Warehouse>>('/wms/warehouse/page', params)
}

/** 查询仓库精简列表 */
export function getSimpleWarehouseList() {
  return http.get<Warehouse[]>('/wms/warehouse/simple-list')
}

/** 查询仓库详情 */
export function getWarehouse(id: number) {
  return http.get<Warehouse>(`/wms/warehouse/get?id=${id}`)
}

/** 新增仓库 */
export function createWarehouse(data: Warehouse) {
  return http.post<number>('/wms/warehouse/create', data)
}

/** 修改仓库 */
export function updateWarehouse(data: Warehouse) {
  return http.put<boolean>('/wms/warehouse/update', data)
}

/** 删除仓库 */
export function deleteWarehouse(id: number) {
  return http.delete<boolean>(`/wms/warehouse/delete?id=${id}`)
}
