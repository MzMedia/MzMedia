import request from '@/config/axios'

/** FMS 结转凭证生成参数 */
export interface FmsClosingVoucherGenerateReqVO {
  accountSetId: number // 账套编号
  month: string // 会计期间
}

/** FMS 结账方案凭证生成参数 */
export interface FmsClosingSchemeVoucherGenerateReqVO extends FmsClosingVoucherGenerateReqVO {
  id: number // 方案编号
}

/** FMS 结转凭证批量生成参数 */
export interface FmsClosingVoucherBatchGenerateReqVO extends FmsClosingVoucherGenerateReqVO {
  ids: number[] // 方案编号数组
}

// FMS 结转凭证 API
export const FmsClosingVoucherApi = {
  // 生成结转损益凭证
  generateProfitLossVoucher: async (data: FmsClosingVoucherGenerateReqVO) => {
    return await request.post<number>({
      url: '/fms/closing/voucher/generate-profit-loss',
      data
    })
  },

  // 生成结账方案凭证
  generateClosingSchemeVoucher: async (data: FmsClosingSchemeVoucherGenerateReqVO) => {
    return await request.post<number>({ url: '/fms/closing/voucher/generate-scheme', data })
  },

  // 批量生成结转凭证
  generateClosingVoucherList: async (data: FmsClosingVoucherBatchGenerateReqVO) => {
    return await request.post<number[]>({ url: '/fms/closing/voucher/generate-list', data })
  }
}
