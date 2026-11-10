import request from '@/config/axios'
import type {
  FmsReportFormulaUpdateReqVO,
  FmsReportItemVO,
  FmsReportListReqVO,
  FmsReportUnmappedSubjectVO
} from '@/api/fms/report'

/** FMS 利润表检查结果 */
export interface FmsIncomeStatementCheckVO {
  balanced?: boolean // 净利润与未分配利润变动是否一致
  differenceAmount?: number // 利润表与资产负债表勾稽差额
  unmappedSubjects: FmsReportUnmappedSubjectVO[] // 未纳入报表公式的科目数组
}

// FMS 利润表 API
export const FmsIncomeStatementApi = {
  // 查询利润表
  getIncomeStatement: async (params: FmsReportListReqVO) => {
    return await request.get<FmsReportItemVO[]>({
      url: '/fms/report/income-statement/get',
      params
    })
  },

  // 导出利润表 Excel
  exportIncomeStatement: async (params: FmsReportListReqVO) => {
    return await request.download({ url: '/fms/report/income-statement/export-excel', params })
  },

  // 修改利润表公式
  updateIncomeStatementFormula: async (data: FmsReportFormulaUpdateReqVO) => {
    return await request.put({ url: '/fms/report/income-statement/update', data })
  },

  // 检查利润表
  checkIncomeStatement: async (params: FmsReportListReqVO) => {
    return await request.get<FmsIncomeStatementCheckVO>({
      url: '/fms/report/income-statement/check',
      params
    })
  }
}
