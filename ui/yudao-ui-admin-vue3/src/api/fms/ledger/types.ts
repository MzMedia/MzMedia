/** FMS 账簿查询参数 */
export interface FmsLedgerListReqVO {
  accountSetId: number // 账套编号
  startMonth: string // 开始会计期间
  endMonth: string // 结束会计期间
  subjectId?: number // 科目编号
  startSubjectId?: number // 起始科目编号
  endSubjectId?: number // 结束科目编号
  minLevel?: number // 最小科目级次
  maxLevel?: number // 最大科目级次
}

/** FMS 辅助核算账簿查询参数 */
export interface FmsLedgerAuxiliaryListReqVO {
  accountSetId: number // 账套编号
  startMonth: string // 开始会计期间
  endMonth: string // 结束会计期间
  auxiliaryTypeId: number // 辅助核算类别编号
  subjectId?: number // 科目编号
  auxiliaryItemId?: number // 辅助核算项目编号
}

/** FMS 账簿明细信息 */
export interface FmsLedgerDetailVO {
  rowType: number // 行类型
  entryId?: number // 分录编号
  entrySubjectId?: number // 分录科目编号
  subjectId: number // 科目编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  period: string // 会计期间
  accountDate: string // 日期
  voucherId?: number // 凭证编号
  voucherNumber?: string // 凭证字号
  digest: string // 摘要
  debitAmount: number // 借方金额
  creditAmount: number // 贷方金额
  balanceDirection: string // 余额方向
  balance: number // 余额
  debitQuantity: number // 借方数量
  creditQuantity: number // 贷方数量
  balanceQuantity: number // 结存数量
  unitPrice?: number // 单价
  quantityUnit?: string // 计量单位
  columnAmounts?: Record<number, number> // 多栏账科目金额 Map
}

/** FMS 总账信息 */
export interface FmsLedgerGeneralVO {
  rowType: number // 行类型
  subjectId: number // 科目编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  period: string // 会计期间
  digest: string // 摘要
  debitAmount: number // 借方金额
  creditAmount: number // 贷方金额
  balanceDirection: string // 余额方向
  balance: number // 余额
}

/** FMS 科目余额信息 */
export interface FmsSubjectBalanceVO {
  nodeKey: string // 节点唯一键
  nodeType: number // 节点类型：1 科目、2 辅助核算组合
  subjectId: number // 科目编号
  assistCombinationId?: number // 辅助核算组合编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  level: number // 科目级次
  quantityAccounting: boolean // 是否启用数量核算
  quantityUnit?: string // 计量单位
  openingDebitAmount: number // 期初借方余额
  openingCreditAmount: number // 期初贷方余额
  openingBalanceDirection: string // 期初余额方向
  openingQuantity: number // 期初数量
  openingUnitPrice: number // 期初单价
  periodDebitAmount: number // 本期借方发生额
  periodCreditAmount: number // 本期贷方发生额
  periodDebitQuantity: number // 本期借方数量
  periodCreditQuantity: number // 本期贷方数量
  yearDebitAmount: number // 本年累计借方发生额
  yearCreditAmount: number // 本年累计贷方发生额
  yearDebitQuantity: number // 本年累计借方数量
  yearCreditQuantity: number // 本年累计贷方数量
  endingDebitAmount: number // 期末借方余额
  endingCreditAmount: number // 期末贷方余额
  endingBalanceDirection: string // 期末余额方向
  endingQuantity: number // 期末数量
  endingUnitPrice: number // 期末单价
  children: FmsSubjectBalanceVO[] // 下级科目或辅助核算组合数组
}
