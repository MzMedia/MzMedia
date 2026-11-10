/** ERP 审批状态 */
export const ErpAuditStatusEnum = {
  UNAUDITED: 10,
  AUDITED: 20,
} as const

/** ERP 业务类型 */
export const ErpBizType = {
  PURCHASE_ORDER: 10,
  PURCHASE_IN: 11,
  PURCHASE_RETURN: 12,
  SALE_ORDER: 20,
  SALE_OUT: 21,
  SALE_RETURN: 22,
} as const
