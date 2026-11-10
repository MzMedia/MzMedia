import request from '@/config/axios'
import type {
  FmsReportFormulaUpdateReqVO,
  FmsReportItemVO,
  FmsReportListReqVO,
  FmsReportUnmappedSubjectVO
} from '@/api/fms/report'

/** FMS 现金流量表检查结果 */
export interface FmsCashFlowCheckVO {
  balanced?: boolean // 资产负债表是否平衡
  initialBalanceBalanced?: boolean // 初始余额是否平衡
  profitLossTransferred?: boolean // 损益是否已结转
  balanceSheetReady?: boolean // 资产负债表是否满足现金流量表取数条件
  openingDifferenceAmount?: number // 年初余额差额
  closingDifferenceAmount?: number // 期末余额差额
  unmappedSubjects: FmsReportUnmappedSubjectVO[] // 未纳入报表公式的科目数组
}

/** FMS 现金流量辅助数据信息 */
export interface FmsCashFlowAdjustmentVO {
  id: number // 数据编号
  name: string // 项目名称
  rowNo: number // 行次
  formula: string // 公式
  remark?: string // 说明
  editable: boolean // 是否可编辑
  currentAmount: number // 本期金额
  yearAmount: number // 本年累计金额
  level: number // 层级
}

/** FMS 现金流量表修改项参数 */
export interface FmsCashFlowStatementUpdateItemReqVO {
  id: number // 报表项目编号
  currentAmount: number // 本期金额
  yearAmount: number // 本年累计金额
}

/** FMS 现金流量表修改参数 */
export interface FmsCashFlowStatementUpdateReqVO extends FmsReportListReqVO {
  items: FmsCashFlowStatementUpdateItemReqVO[] // 现金流量表项目数组
}

/** FMS 现金流量辅助数据修改项参数 */
export interface FmsCashFlowAdjustmentUpdateItemReqVO {
  id: number // 数据编号
  currentAmount: number // 本期金额
  yearAmount: number // 本年累计金额
}

/** FMS 现金流量辅助数据修改参数 */
export interface FmsCashFlowAdjustmentUpdateReqVO {
  accountSetId: number // 账套编号
  items: FmsCashFlowAdjustmentUpdateItemReqVO[] // 辅助数据项数组
}

// FMS 现金流量表 API
export const FmsCashFlowStatementApi = {
  // 查询现金流量表
  getCashFlowStatement: async (params: FmsReportListReqVO) => {
    return await request.get<FmsReportItemVO[]>({
      url: '/fms/report/cash-flow-statement/get',
      params
    })
  },

  // 修改现金流量表
  updateCashFlowStatement: async (data: FmsCashFlowStatementUpdateReqVO) => {
    return await request.put({ url: '/fms/report/cash-flow-statement/update', data })
  },

  // 导出现金流量表 Excel
  exportCashFlowStatement: async (params: FmsReportListReqVO) => {
    return await request.download({ url: '/fms/report/cash-flow-statement/export-excel', params })
  },

  // 检查现金流量表
  checkCashFlowStatement: async (params: FmsReportListReqVO) => {
    return await request.get<FmsCashFlowCheckVO>({
      url: '/fms/report/cash-flow-statement/check',
      params
    })
  },

  // 查询现金流量辅助数据列表
  getCashFlowAdjustmentList: async (params: FmsReportListReqVO) => {
    return await request.get<FmsCashFlowAdjustmentVO[]>({
      url: '/fms/report/cash-flow-statement/adjustment/list',
      params
    })
  },

  // 修改现金流量辅助数据
  updateCashFlowAdjustment: async (data: FmsCashFlowAdjustmentUpdateReqVO) => {
    return await request.put({ url: '/fms/report/cash-flow-statement/adjustment/update', data })
  },

  // 修改现金流量辅助数据公式
  updateCashFlowAdjustmentFormula: async (data: FmsReportFormulaUpdateReqVO) => {
    return await request.put({
      url: '/fms/report/cash-flow-statement/adjustment/update-formula',
      data
    })
  }
}
