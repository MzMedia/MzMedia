import request from '@/config/axios'

/** FMS 账套信息 */
export interface FmsAccountSetVO {
  id?: number // 账套编号
  companyCode: string // 公司编码
  companyName: string // 公司名称
  companyProfile?: string // 公司简介
  industry?: string // 所在行业
  location?: string // 所在地
  legalRepresentative?: string // 法人代表
  legalRepresentativeIdNumber?: string // 法人身份证号
  businessLicenseNumber?: string // 营业执照号
  organizationCode?: string // 组织机构代码
  remark?: string // 备注
  contactName?: string // 联系人
  officeTelephone?: string // 办公电话
  mobile?: string // 手机号码
  faxNumber?: string // 传真号码
  qqNumber?: string // QQ 号码
  email?: string // 邮箱
  otherContact?: string // 其他联系方式
  address?: string // 详细地址
  currencyId?: number // 币种编号
  startTime?: number // 启用期间，格式为 yyyyMM
  standard?: number // 会计制度
  initialized?: boolean // 是否已初始化
  defaultStatus?: boolean // 是否默认账套
  founder?: boolean // 是否账套创建人
  level?: number // 当前用户的成员权限级别
  createTime?: Date // 创建时间
}

/** FMS 账套初始化参数 */
export interface FmsAccountSetInitializeReqVO {
  accountSetId: number // 账套编号
  currencyCode: string // 本位币编码
  startTime: number // 启用期间，格式为 yyyyMM
  standard: number // 会计制度
  level: number // 科目层级
  subjectCodeRule: string // 科目编码规则
  ledgerBalanceMode: number // 账簿余额方向模式
}

// FMS 账套 API
export const FmsAccountSetApi = {
  // 查询当前用户的账套列表
  getAccountSetList: async () => {
    return await request.get<FmsAccountSetVO[]>({ url: '/fms/config/account-set/list' })
  },

  // 查询账套详情
  getAccountSet: async (id: number) => {
    return await request.get<FmsAccountSetVO>({ url: '/fms/config/account-set/get?id=' + id })
  },

  // 新增账套
  createAccountSet: async (data: FmsAccountSetVO) => {
    return await request.post<number>({ url: '/fms/config/account-set/create', data })
  },

  // 修改账套
  updateAccountSet: async (data: FmsAccountSetVO) => {
    return await request.put({ url: '/fms/config/account-set/update', data })
  },

  // 初始化账套
  initializeAccountSet: async (data: FmsAccountSetInitializeReqVO) => {
    return await request.put({ url: '/fms/config/account-set/initialize', data })
  }
}
