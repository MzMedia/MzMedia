import request from '@/config/axios'
import type {
  FmsReportFormulaUpdateReqVO,
  FmsReportListReqVO,
  FmsReportUnmappedSubjectVO
} from '@/api/fms/report'

/** FMS 资产负债表行信息 */
export interface FmsBalanceSheetRowVO {
  rowId: number // 行编号
  assetId?: number // 资产项目配置编号
  assetName?: string // 资产项目名称
  assetRowNo?: number // 资产项目行次
  assetClosingAmount?: number // 资产项目期末余额
  assetOpeningAmount?: number // 资产项目年初余额
  assetLevel?: number // 资产项目层级
  assetEditable?: boolean // 资产项目是否可编辑
  assetFormula?: string // 资产项目公式
  liabilityId?: number // 负债和所有者权益项目配置编号
  liabilityName?: string // 负债和所有者权益项目名称
  liabilityRowNo?: number // 负债和所有者权益项目行次
  liabilityClosingAmount?: number // 负债和所有者权益项目期末余额
  liabilityOpeningAmount?: number // 负债和所有者权益项目年初余额
  liabilityLevel?: number // 负债和所有者权益项目层级
  liabilityEditable?: boolean // 负债和所有者权益项目是否可编辑
  liabilityFormula?: string // 负债和所有者权益项目公式
}

/** FMS 资产负债表检查结果 */
export interface FmsBalanceSheetCheckVO {
  balanced?: boolean // 报表是否平衡
  initialBalanceBalanced?: boolean // 初始余额是否平衡
  profitLossTransferred?: boolean // 损益是否已结转
  openingDifferenceAmount?: number // 年初余额差额
  closingDifferenceAmount?: number // 期末余额差额
  unmappedSubjects: FmsReportUnmappedSubjectVO[] // 未纳入报表公式的科目数组
}

// FMS 资产负债表 API
export const FmsBalanceSheetApi = {
  // 查询资产负债表
  getBalanceSheet: async (params: FmsReportListReqVO) => {
    return await request.get<FmsBalanceSheetRowVO[]>({
      url: '/fms/report/balance-sheet/get',
      params
    })
  },

  // 导出资产负债表 Excel
  exportBalanceSheet: async (params: FmsReportListReqVO) => {
    return await request.download({ url: '/fms/report/balance-sheet/export-excel', params })
  },

  // 修改资产负债表公式
  updateBalanceSheetFormula: async (data: FmsReportFormulaUpdateReqVO) => {
    return await request.put({ url: '/fms/report/balance-sheet/update', data })
  },

  // 检查资产负债表
  checkBalanceSheet: async (params: FmsReportListReqVO) => {
    return await request.get<FmsBalanceSheetCheckVO>({
      url: '/fms/report/balance-sheet/check',
      params
    })
  }
}
