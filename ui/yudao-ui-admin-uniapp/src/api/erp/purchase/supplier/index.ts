import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** ERP 供应商 */
export interface Supplier {
  id?: number // 供应商编号
  name?: string // 供应商名称
  contact?: string // 联系人
  mobile?: string // 手机号码
  telephone?: string // 联系电话
  email?: string // 电子邮箱
  fax?: string // 传真
  remark?: string // 备注
  status?: number // 开启状态
  sort?: number // 排序
  taxNo?: string // 纳税人识别号
  taxPercent?: number // 税率
  bankName?: string // 开户行
  bankAccount?: string // 开户账号
  bankAddress?: string // 开户地址
  createTime?: Date // 创建时间
}

/** 获取供应商分页列表 */
export function getSupplierPage(params: PageParam) {
  return http.get<PageResult<Supplier>>('/erp/supplier/page', params)
}

/** 获取供应商精简列表 */
export function getSupplierSimpleList() {
  return http.get<Supplier[]>('/erp/supplier/simple-list')
}

/** 获取供应商详情 */
export function getSupplier(id: number) {
  return http.get<Supplier>(`/erp/supplier/get?id=${id}`)
}

/** 创建供应商 */
export function createSupplier(data: Supplier) {
  return http.post<number>('/erp/supplier/create', data)
}

/** 更新供应商 */
export function updateSupplier(data: Supplier) {
  return http.put<boolean>('/erp/supplier/update', data)
}

/** 删除供应商 */
export function deleteSupplier(id: number) {
  return http.delete<boolean>(`/erp/supplier/delete?id=${id}`)
}
