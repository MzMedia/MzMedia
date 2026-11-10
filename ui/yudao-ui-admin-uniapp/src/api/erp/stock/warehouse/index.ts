import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 仓库 */
export interface Warehouse {
  id?: number // 仓库编号
  name?: string // 仓库名称
  address?: string // 仓库地址
  sort?: number // 排序
  remark?: string // 备注
  principal?: string // 负责人
  warehousePrice?: number // 仓储费，单位：元
  truckagePrice?: number // 搬运费，单位：元
  status?: number // 开启状态
  defaultStatus?: boolean // 是否默认
  createTime?: Date // 创建时间
}

/** 获取仓库分页列表 */
export function getWarehousePage(params: PageParam) {
  return http.get<PageResult<Warehouse>>('/erp/warehouse/page', params)
}

/** 获取仓库精简列表 */
export function getWarehouseSimpleList() {
  return http.get<Warehouse[]>('/erp/warehouse/simple-list')
}

/** 获取仓库详情 */
export function getWarehouse(id: number) {
  return http.get<Warehouse>(`/erp/warehouse/get?id=${id}`)
}

/** 创建仓库 */
export function createWarehouse(data: Warehouse) {
  return http.post<number>('/erp/warehouse/create', data)
}

/** 更新仓库 */
export function updateWarehouse(data: Warehouse) {
  return http.put<boolean>('/erp/warehouse/update', data)
}

/** 修改仓库默认状态 */
export function updateWarehouseDefaultStatus(id: number, defaultStatus: boolean) {
  return http.put<boolean>('/erp/warehouse/update-default-status', undefined, { id, defaultStatus })
}

/** 删除仓库 */
export function deleteWarehouse(id: number) {
  return http.delete<boolean>(`/erp/warehouse/delete?id=${id}`)
}
