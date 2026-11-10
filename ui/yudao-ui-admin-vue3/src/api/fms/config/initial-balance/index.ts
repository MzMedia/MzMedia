import request from '@/config/axios'

/** FMS 初始余额金额信息 */
export interface FmsInitialBalanceAmountsVO {
  openingAmount: number // 期初金额
  openingQuantity: number // 期初数量
  yearDebitAmount: number // 本年累计借方金额
  yearDebitQuantity: number // 本年累计借方数量
  yearCreditAmount: number // 本年累计贷方金额
  yearCreditQuantity: number // 本年累计贷方数量
  yearOpeningAmount: number // 年初金额
  yearOpeningQuantity: number // 年初数量
  profitLossAmount: number // 实际损益发生额
  profitLossQuantity: number // 实际损益发生数量
}

/** FMS 初始余额信息 */
export interface FmsInitialBalanceVO extends FmsInitialBalanceAmountsVO {
  id?: number // 初始余额编号
  subjectId: number // 科目编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  parentId?: number // 上级科目编号
  type: number // 科目类型
  balanceDirection: number // 余额方向
  quantityAccounting: boolean // 是否启用数量核算
  quantityUnit?: string // 数量单位
  auxiliaryAccounting: boolean // 是否启用辅助核算
  auxiliaryConfigs: FmsInitialBalanceAuxiliaryConfigVO[] // 辅助核算配置数组
  assistBalances: FmsInitialBalanceAssistVO[] // 辅助核算余额数组
}

/** FMS 初始余额辅助核算配置信息，等价后端 FmsInitialBalanceRespVO.AuxiliaryConfig */
export interface FmsInitialBalanceAuxiliaryConfigVO {
  auxiliaryTypeId: number // 辅助核算类别编号
  type: number // 辅助核算类型
  name: string // 辅助核算类别名称
}

/** FMS 初始余额辅助核算余额信息，等价后端 FmsInitialBalanceRespVO.AssistBalance */
export interface FmsInitialBalanceAssistVO extends FmsInitialBalanceAmountsVO {
  assistCombinationId?: number // 辅助核算组合编号
  auxiliaries: FmsInitialBalanceAuxiliaryItemVO[] // 辅助核算项目数组
}

/** FMS 初始余额辅助核算项目信息，等价后端 FmsInitialBalanceRespVO.AuxiliaryItem */
export interface FmsInitialBalanceAuxiliaryItemVO {
  type: number // 辅助核算类型
  typeId: number // 辅助核算类别编号
  itemId: number // 辅助核算项目编号
  name: string // 辅助核算项目名称
}

/** FMS 初始余额修改信息 */
export interface FmsInitialBalanceUpdateVO extends FmsInitialBalanceAmountsVO {
  subjectId: number // 科目编号
  assistBalances: FmsInitialBalanceAssistUpdateVO[] // 辅助核算余额数组
}

/** FMS 初始余额辅助核算修改信息，等价后端 FmsInitialBalanceSaveReqVO.AssistBalance */
export interface FmsInitialBalanceAssistUpdateVO extends FmsInitialBalanceAmountsVO {
  auxiliaryItemIds: number[] // 辅助核算项目编号数组
}

/** FMS 试算平衡信息 */
export interface FmsTrialBalanceVO {
  openingDebitAmount: number // 期初借方金额
  openingCreditAmount: number // 期初贷方金额
  openingDifferenceAmount: number // 期初差额
  yearDebitAmount: number // 本年累计借方金额
  yearCreditAmount: number // 本年累计贷方金额
  yearDifferenceAmount: number // 本年累计差额
  balanced: boolean // 是否平衡
}

// FMS 初始余额 API
export const FmsInitialBalanceApi = {
  // 查询初始余额列表
  getInitialBalanceList: async (accountSetId: number, subjectType: number) => {
    return await request.get<FmsInitialBalanceVO[]>({
      url: '/fms/config/initial-balance/list',
      params: { accountSetId, subjectType }
    })
  },

  // 保存初始余额
  saveInitialBalance: async (accountSetId: number, balances: FmsInitialBalanceUpdateVO[]) => {
    return await request.put({
      url: '/fms/config/initial-balance/save',
      data: { accountSetId, balances }
    })
  },

  // 查询试算平衡结果
  getTrialBalance: async (accountSetId: number) => {
    return await request.get<FmsTrialBalanceVO>({
      url: '/fms/config/initial-balance/trial-balance',
      params: { accountSetId }
    })
  },

  // 导出初始余额 Excel
  exportInitialBalance: async (accountSetId: number) => {
    return await request.download({
      url: '/fms/config/initial-balance/export-excel',
      params: { accountSetId }
    })
  },

  // 下载初始余额导入模板
  getInitialBalanceImportTemplate: async (accountSetId: number) => {
    return await request.download({
      url: '/fms/config/initial-balance/get-import-template',
      params: { accountSetId }
    })
  },

  // 导入初始余额
  importInitialBalance: async (accountSetId: number, file: File) => {
    const data = new FormData()
    data.append('accountSetId', String(accountSetId))
    data.append('file', file)
    return await request.post<number>({
      url: '/fms/config/initial-balance/import',
      data,
      headersType: 'multipart/form-data'
    })
  }
}
