import { http } from '@/http/http'

/** 角色菜单权限分配请求 */
export interface PermissionAssignRoleMenuReq {
  roleId: number
  menuIds: number[]
}

/** 角色数据权限分配请求 */
export interface PermissionAssignRoleDataScopeReq {
  roleId: number
  dataScope: number
  dataScopeDeptIds: number[]
}

/** 获取角色拥有的菜单列表 */
export function getRoleMenuList(roleId: number) {
  return http.get<number[]>(`/system/permission/list-role-menus?roleId=${roleId}`)
}

/** 分配角色菜单权限 */
export function assignRoleMenu(data: PermissionAssignRoleMenuReq) {
  return http.post<boolean>('/system/permission/assign-role-menu', data)
}

/** 分配角色数据权限 */
export function assignRoleDataScope(data: PermissionAssignRoleDataScopeReq) {
  return http.post<boolean>('/system/permission/assign-role-data-scope', data)
}
