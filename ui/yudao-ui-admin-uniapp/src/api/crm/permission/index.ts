import { http } from '@/http/http'

/** CRM 数据权限 */
export interface Permission {
  id?: number
  userId: number
  bizType: number
  bizId: number
  level: number
  toBizTypes?: number[]
  deptName?: string
  nickname?: string
  postNames?: string[]
  createTime?: Date | string
  ids?: number[]
}

/** CRM 转移请求 */
export interface TransferReq {
  id: number
  newOwnerUserId: number
  oldOwnerPermissionLevel?: number
  toBizTypes?: number[]
}

/** CRM 业务类型枚举 */
export enum BizTypeEnum {
  CRM_CLUE = 1,
  CRM_CUSTOMER = 2,
  CRM_CONTACT = 3,
  CRM_BUSINESS = 4,
  CRM_CONTRACT = 5,
  CRM_PRODUCT = 6,
  CRM_RECEIVABLE = 7,
  CRM_RECEIVABLE_PLAN = 8,
}

/** CRM 数据权限级别枚举 */
export enum PermissionLevelEnum {
  OWNER = 1,
  READ = 2,
  WRITE = 3,
}

/** CRM 审批状态枚举（对应后端 CrmAuditStatusEnum） */
export enum CrmAuditStatusEnum {
  DRAFT = 0, // 未提交
  PROCESS = 10, // 审批中
  APPROVE = 20, // 审核通过
  REJECT = 30, // 审核不通过
  CANCEL = 40, // 已取消
}

/** CRM 归属场景枚举（列表「我负责的/我参与的/下属负责的」） */
export enum CrmSceneTypeEnum {
  OWNER = 1, // 我负责的
  INVOLVED = 2, // 我参与的
  SUBORDINATE = 3, // 下属负责的
}

/** CRM 归属场景选项（列表归属 tab 与待办筛选共用） */
export const CRM_SCENE_TYPES: { label: string, value: number }[] = [
  { label: '我负责的', value: CrmSceneTypeEnum.OWNER },
  { label: '我参与的', value: CrmSceneTypeEnum.INVOLVED },
  { label: '下属负责的', value: CrmSceneTypeEnum.SUBORDINATE },
]

/** 获得数据权限列表 */
export function getPermissionList(params: Record<string, any>) {
  return http.get<Permission[]>('/crm/permission/list', params)
}

/** 创建数据权限 */
export function createPermission(data: Permission) {
  return http.post<number>('/crm/permission/create', data)
}

/** 编辑数据权限 */
export function updatePermission(data: Partial<Permission>) {
  return http.put<boolean>('/crm/permission/update', data)
}

/** 批量删除数据权限 */
export function deletePermissionBatch(ids: number[]) {
  return http.delete<boolean>(`/crm/permission/delete?ids=${ids.join(',')}`)
}

/** 删除自己的数据权限 */
export function deleteSelfPermission(id: number) {
  return http.delete<boolean>(`/crm/permission/delete-self?id=${id}`)
}
