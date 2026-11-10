import request from '@/config/axios'
import type {
  FmsLedgerDetailVO,
  FmsLedgerGeneralVO,
  FmsLedgerAuxiliaryListReqVO,
  FmsLedgerListReqVO,
  FmsSubjectBalanceVO
} from './types'

/** FMS 辅助核算余额信息 */
export interface FmsLedgerAuxiliaryBalanceVO {
  auxiliaryItemId: number // 辅助核算项目编号
  code: string // 项目编码
  name: string // 项目名称
  openingDebitAmount: number // 期初借方余额
  openingCreditAmount: number // 期初贷方余额
  periodDebitAmount: number // 本期借方发生额
  periodCreditAmount: number // 本期贷方发生额
  yearDebitAmount: number // 本年累计借方发生额
  yearCreditAmount: number // 本年累计贷方发生额
  endingDebitAmount: number // 期末借方余额
  endingCreditAmount: number // 期末贷方余额
}

/** FMS 多栏账科目信息 */
export interface FmsMultiColumnSubjectVO {
  subjectId: number // 科目编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  balanceDirection: number // 余额方向
}

/** FMS 多栏账信息 */
export interface FmsMultiColumnVO {
  columns: FmsMultiColumnSubjectVO[] // 动态科目列数组
  rows: FmsLedgerDetailVO[] // 账簿行数组
}

// FMS 账簿 API
export const FmsLedgerApi = {
  // 查询明细账
  getDetailList: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsLedgerDetailVO[]>({
      url: '/fms/ledger/detail/list',
      params
    })
  },

  // 导出明细账 Excel
  exportDetail: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/detail/export-excel',
      params
    })
  },

  // 查询总账
  getGeneralList: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsLedgerGeneralVO[]>({
      url: '/fms/ledger/general/list',
      params
    })
  },

  // 导出总账 Excel
  exportGeneral: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/general/export-excel',
      params
    })
  },

  // 查询科目余额表
  getSubjectBalanceList: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsSubjectBalanceVO[]>({
      url: '/fms/ledger/subject-balance/list',
      params
    })
  },

  // 导出科目余额表 Excel
  exportSubjectBalance: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/subject-balance/export-excel',
      params
    })
  },

  // 查询多栏账
  getMultiColumn: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsMultiColumnVO>({
      url: '/fms/ledger/multi-column/list',
      params
    })
  },

  // 导出多栏账 Excel
  exportMultiColumn: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/multi-column/export-excel',
      params
    })
  },

  // 查询核算项目明细账
  getAuxiliaryDetailList: async (params: FmsLedgerAuxiliaryListReqVO) => {
    return await request.get<FmsLedgerDetailVO[]>({
      url: '/fms/ledger/auxiliary-detail/list',
      params
    })
  },

  // 导出核算项目明细账 Excel
  exportAuxiliaryDetail: async (params: FmsLedgerAuxiliaryListReqVO) => {
    return await request.download({
      url: '/fms/ledger/auxiliary-detail/export-excel',
      params
    })
  },

  // 查询核算项目余额表
  getAuxiliaryBalanceList: async (params: FmsLedgerAuxiliaryListReqVO) => {
    return await request.get<FmsLedgerAuxiliaryBalanceVO[]>({
      url: '/fms/ledger/auxiliary-balance/list',
      params
    })
  },

  // 导出核算项目余额表 Excel
  exportAuxiliaryBalance: async (params: FmsLedgerAuxiliaryListReqVO) => {
    return await request.download({
      url: '/fms/ledger/auxiliary-balance/export-excel',
      params
    })
  },

  // 查询数量金额明细账
  getQuantityDetailList: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsLedgerDetailVO[]>({
      url: '/fms/ledger/quantity-detail/list',
      params
    })
  },

  // 导出数量金额明细账 Excel
  exportQuantityDetail: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/quantity-detail/export-excel',
      params
    })
  },

  // 查询数量金额总账
  getQuantityGeneralList: async (params: FmsLedgerListReqVO) => {
    return await request.get<FmsSubjectBalanceVO[]>({
      url: '/fms/ledger/quantity-general/list',
      params
    })
  },

  // 导出数量金额总账 Excel
  exportQuantityGeneral: async (params: FmsLedgerListReqVO) => {
    return await request.download({
      url: '/fms/ledger/quantity-general/export-excel',
      params
    })
  }
}
