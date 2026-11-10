import request from '@/config/axios'

/** FMS 结账方案列表查询参数 */
export interface FmsClosingSchemeListReqVO {
  accountSetId: number // 账套编号
  month: string // 会计期间
}

/** FMS 结转损益设置 */
export interface FmsProfitLossSettingsVO {
  accountSetId: number // 账套编号
  voucherWordId?: number // 凭证字编号
  digest: string // 凭证摘要
  voucherType: number // 结转凭证类型
  priorYearAdjustmentSubjectId?: number // 以前年度损益调整科目编号
  adjustmentClosingSubjectId?: number // 以前年度损益调整结转科目编号
  otherClosingSubjectId?: number // 其他损益结转科目编号
  reverseBalance: boolean // 是否按余额反向结转
  closingDay: number // 结转日期
}

/** FMS 结账方案科目规则信息 */
export interface FmsClosingSchemeSubjectRuleVO {
  subjectId?: number // 科目编号
  subjectCode?: string // 科目编码快照
  digest: string // 摘要
  direction: number // 借贷方向
  amountRatio: number // 金额比例
}

/** FMS 结账方案保存参数 */
export interface FmsClosingSchemeSaveReqVO {
  id?: number // 方案编号
  accountSetId: number // 账套编号
  name: string // 方案名称
  periodEnd: boolean // 是否期末结转
  subjectId?: number // 来源科目编号
  formulaRule: number // 取数规则
  timeType: number // 取数时间类型
  voucherWordId?: number // 凭证字编号
  subjects: FmsClosingSchemeSubjectRuleVO[] // 结转科目规则数组
}

/** FMS 结账方案信息 */
export interface FmsClosingSchemeVO extends FmsClosingSchemeSaveReqVO {
  id: number // 方案编号
  type: number // 方案类型
  digest?: string // 凭证摘要
  voucherType?: number // 结转凭证类型
  priorYearAdjustmentSubjectId?: number // 以前年度损益调整科目编号
  adjustmentClosingSubjectId?: number // 以前年度损益调整结转科目编号
  otherClosingSubjectId?: number // 其他损益结转科目编号
  reverseBalance?: boolean // 是否按余额反向结转
  closingDay?: number // 结转日期
  balance: number // 待结转金额
  voucherIds: number[] // 当前期间已生成凭证编号数组
}

/** FMS 专用结转设置 */
export interface FmsSpecialClosingSettingsVO {
  id: number // 方案编号
  accountSetId: number // 账套编号
  voucherWordId?: number // 凭证字编号
  subjects: FmsClosingSchemeSubjectRuleVO[] // 结转科目规则数组
}

// FMS 结账方案 API
export const FmsClosingSchemeApi = {
  // 查询结账方案列表
  getClosingSchemeList: async (params: FmsClosingSchemeListReqVO) => {
    return await request.get<FmsClosingSchemeVO[]>({ url: '/fms/closing/scheme/list', params })
  },

  // 新增结账方案
  createClosingScheme: async (data: FmsClosingSchemeSaveReqVO) => {
    return await request.post<number>({ url: '/fms/closing/scheme/create', data })
  },

  // 修改结账方案
  updateClosingScheme: async (data: FmsClosingSchemeSaveReqVO) => {
    return await request.put({ url: '/fms/closing/scheme/update', data })
  },

  // 保存结转损益设置
  saveProfitLossSettings: async (data: FmsProfitLossSettingsVO) => {
    return await request.put<number>({
      url: '/fms/closing/scheme/update-profit-loss-settings',
      data
    })
  },

  // 保存专用结转设置
  updateSpecialClosingSettings: async (data: FmsSpecialClosingSettingsVO) => {
    return await request.put({ url: '/fms/closing/scheme/update-special-settings', data })
  },

  // 删除结账方案
  deleteClosingScheme: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/closing/scheme/delete',
      params: { accountSetId, id }
    })
  }
}
