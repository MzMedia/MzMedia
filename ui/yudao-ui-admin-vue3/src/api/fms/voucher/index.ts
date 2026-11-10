import request from '@/config/axios'

/** FMS 凭证辅助核算项目信息 */
export interface FmsVoucherAuxiliaryItemVO {
  type?: number // 辅助核算类型
  typeId: number // 辅助核算类别编号
  itemId: number // 辅助核算项目编号
  name?: string // 辅助核算项目名称
}

/** FMS 凭证分录信息 */
export interface FmsVoucherEntryVO {
  id?: number // 分录编号
  digest: string // 摘要内容
  subjectId: number // 科目编号
  quantity?: number // 数量
  unitPrice?: number // 单价
  debitAmount?: number // 借方金额
  creditAmount?: number // 贷方金额
  auxiliaries: FmsVoucherAuxiliaryItemVO[] // 辅助核算项目数组
  subjectCode?: string // 科目编码
  subjectName?: string // 科目名称
  sort?: number // 显示顺序
  assistCombinationId?: number // 辅助核算组合编号
}

/** FMS 凭证信息 */
export interface FmsVoucherVO {
  id: number // 凭证编号
  accountSetId: number // 账套编号
  voucherWordId: number // 凭证字编号
  voucherNumber: number // 凭证号
  voucherTime: number // 凭证日期时间戳
  attachmentUrls: string[] // 附件地址数组
  entries: FmsVoucherEntryVO[] // 凭证分录数组
  voucherWordName?: string // 凭证字
  attachmentCount: number // 附单据张数
  debitAmount: number // 借方金额
  creditAmount: number // 贷方金额
  total: number // 合计金额
  status: number // 审核状态
  closingGenerated: boolean // 是否为结账生成凭证
  creatorUserId?: number // 制单人后台用户编号
  creatorUserName?: string // 制单人名称
  reviewerUserId?: number // 审核人后台用户编号
  reviewerUserName?: string // 审核人名称
  createTime: Date // 创建时间
}

/** FMS 凭证保存参数 */
export interface FmsVoucherSaveReqVO {
  id?: number // 凭证编号
  accountSetId: number // 账套编号
  voucherWordId: number // 凭证字编号
  voucherNumber: number // 凭证号
  voucherTime: number // 凭证日期时间戳
  attachmentCount: number // 附单据张数
  entries: FmsVoucherEntryVO[] // 凭证分录数组
}

/** FMS 凭证科目余额信息 */
export interface FmsVoucherSubjectBalanceVO {
  subjectId: number // 科目编号
  balanceDirection?: string // 余额方向
  balance: number // 余额
}

/** FMS 凭证附件修改参数 */
export interface FmsVoucherAttachmentUpdateReqVO {
  id: number // 凭证编号
  accountSetId: number // 账套编号
  attachmentUrls: string[] // 附件地址数组
}

/** FMS 凭证分页查询参数 */
export interface FmsVoucherPageReqVO extends PageParam {
  accountSetId: number // 账套编号
  ids?: number[] // 凭证编号数组
  voucherTime?: string[] // 凭证日期范围
  voucherWordId?: number // 凭证字编号
  voucherNumber?: number // 凭证号
  digest?: string // 摘要关键词
  subjectId?: number // 科目编号
  minAmount?: number // 最小金额
  maxAmount?: number // 最大金额
  creatorUserId?: number // 制单人后台用户编号
  status?: number // 审核状态
}

/** FMS 凭证整理参数 */
export interface FmsVoucherTidyReqVO {
  accountSetId: number // 账套编号
  month: string // 整理月份
  voucherWordId?: number // 凭证字编号
  startNumber: number // 起始编号
  type: number // 整理方式
}

/** FMS 凭证移动参数 */
export interface FmsVoucherMoveReqVO {
  accountSetId: number // 账套编号
  month: string // 凭证月份
  voucherWordId?: number // 凭证字编号
  sourceNumber?: number // 原凭证号
  targetNumber?: number // 移动到的凭证号
}

/** FMS 凭证导入结果 */
export interface FmsVoucherImportRespVO {
  totalRowCount: number // 总分录数
  successRowCount: number // 成功分录数
  failureRowCount: number // 失败分录数
  totalVoucherCount: number // 总凭证数
  successVoucherCount: number // 成功凭证数
  failureVoucherCount: number // 失败凭证数
  errorFileUrl?: string // 错误数据文件地址
}

// FMS 凭证 API
export const FmsVoucherApi = {
  // 查询凭证分页
  getVoucherPage: async (params: FmsVoucherPageReqVO) => {
    return await request.get<PageResult<FmsVoucherVO[]>>({ url: '/fms/voucher/page', params })
  },

  // 查询待打印凭证列表
  getVoucherPrintList: async (params: FmsVoucherPageReqVO) => {
    return await request.get<FmsVoucherVO[]>({ url: '/fms/voucher/print-list', params })
  },

  // 导出凭证 Excel
  exportVoucher: async (params: FmsVoucherPageReqVO) => {
    return await request.download({ url: '/fms/voucher/export-excel', params })
  },

  // 下载凭证导入模板
  getVoucherImportTemplate: async (accountSetId: number) => {
    return await request.download({
      url: '/fms/voucher/get-import-template',
      params: { accountSetId }
    })
  },

  // 导入凭证
  importVoucher: async (accountSetId: number, file: File) => {
    const data = new FormData()
    data.append('accountSetId', String(accountSetId))
    data.append('file', file)
    return await request.post<FmsVoucherImportRespVO>({
      url: '/fms/voucher/import',
      data,
      headersType: 'multipart/form-data'
    })
  },

  // 查询凭证详情
  getVoucher: async (accountSetId: number, id: number) => {
    return await request.get<FmsVoucherVO>({
      url: '/fms/voucher/get',
      params: { accountSetId, id }
    })
  },

  // 查询凭证科目余额列表
  getVoucherSubjectBalanceList: async (accountSetId: number, month: string) => {
    return await request.get<FmsVoucherSubjectBalanceVO[]>({
      url: '/fms/voucher/subject-balance-list',
      params: { accountSetId, month }
    })
  },

  // 查询凭证辅助核算组合余额
  getVoucherAuxiliaryBalance: async (
    accountSetId: number,
    month: string,
    subjectId: number,
    auxiliaryItemIds: number[]
  ) => {
    return await request.get<FmsVoucherSubjectBalanceVO>({
      url: '/fms/voucher/auxiliary-balance',
      params: { accountSetId, month, subjectId, auxiliaryItemIds: auxiliaryItemIds.join(',') }
    })
  },

  // 查询下一凭证号
  getNextVoucherNumber: async (
    accountSetId: number,
    voucherWordId: number,
    voucherTime: string
  ) => {
    return await request.get<number>({
      url: '/fms/voucher/next-number',
      params: { accountSetId, voucherWordId, voucherTime }
    })
  },

  // 新增凭证
  createVoucher: async (data: FmsVoucherSaveReqVO) => {
    return await request.post<number>({ url: '/fms/voucher/create', data })
  },

  // 修改凭证
  updateVoucher: async (data: FmsVoucherSaveReqVO) => {
    return await request.put({ url: '/fms/voucher/update', data })
  },

  // 修改凭证附件
  updateVoucherAttachments: async (data: FmsVoucherAttachmentUpdateReqVO) => {
    return await request.put({ url: '/fms/voucher/update-attachments', data })
  },

  // 批量删除凭证
  deleteVoucherList: async (accountSetId: number, ids: number[]) => {
    return await request.delete({
      url: '/fms/voucher/delete-list',
      params: { accountSetId, ids: ids.join(',') }
    })
  },

  // 审核或反审核凭证
  updateVoucherReviewStatus: async (accountSetId: number, ids: number[], status: number) => {
    return await request.put({
      url: '/fms/voucher/update-review-status',
      data: { accountSetId, ids, status }
    })
  },

  // 整理凭证
  tidyVoucher: async (data: FmsVoucherTidyReqVO) => {
    return await request.put({ url: '/fms/voucher/tidy', data })
  },

  // 移动凭证
  moveVoucher: async (data: FmsVoucherMoveReqVO) => {
    return await request.put({ url: '/fms/voucher/move', data })
  }
}

export * from './statistics'
