import request from '@/config/axios'

/** FMS 账套用户信息 */
export interface FmsAccountUserVO {
  userId: number // 后台用户编号
  nickname?: string // 用户昵称
  deptName?: string // 部门名称
  mobile?: string // 手机号码
  email?: string // 用户邮箱
  status?: number // 用户状态
  defaultStatus: boolean // 是否默认账套
  founder: boolean // 是否账套创建人
  level: number // 成员权限级别
}

/** FMS 账套用户权限级别枚举 */
export enum FmsAccountUserLevelEnum {
  OWNER = 1, // 主管
  READ = 2, // 查看者
  WRITE = 3 // 会计
}

/** FMS 账套成员修改参数 */
export interface FmsAccountUserUpdateMemberReqVO {
  userId: number // 后台用户编号
  level: number // 成员权限级别
}

/** FMS 账套用户修改参数 */
export interface FmsAccountUserUpdateReqVO {
  accountSetId: number // 账套编号
  members: FmsAccountUserUpdateMemberReqVO[] // 账套成员数组
}

// FMS 账套用户 API
export const FmsAccountUserApi = {
  // 查询账套用户列表
  getAccountUserList: async (accountSetId: number) => {
    return await request.get<FmsAccountUserVO[]>({
      url: '/fms/config/account-user/list',
      params: { accountSetId }
    })
  },

  // 修改账套用户列表
  updateAccountUserList: async (data: FmsAccountUserUpdateReqVO) => {
    return await request.put({ url: '/fms/config/account-user/update', data })
  },

  // 设置默认账套
  updateAccountSetDefaultStatus: async (accountSetId: number) => {
    return await request.put({
      url: '/fms/config/account-user/update-default-status',
      params: { accountSetId }
    })
  }
}
