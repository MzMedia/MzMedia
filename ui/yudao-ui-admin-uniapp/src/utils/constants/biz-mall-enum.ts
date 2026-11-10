/**
 * 商品 SPU 状态枚举
 */
export const ProductSpuStatusEnum = {
  RECYCLE: -1, // 回收站
  DISABLE: 0, // 仓库中（下架）
  ENABLE: 1, // 出售中（上架）
}

/**
 * 交易订单状态枚举
 */
export const TradeOrderStatusEnum = {
  UNPAID: 0, // 待付款
  UNDELIVERED: 10, // 待发货
  DELIVERED: 20, // 待收货
  COMPLETED: 30, // 已完成
  CANCELED: 40, // 已取消
}

/**
 * 配送方式枚举
 */
export const DeliveryTypeEnum = {
  EXPRESS: 1, // 快递发货
  PICK_UP: 2, // 到店自提
}

/**
 * 交易售后状态枚举
 */
export const TradeAfterSaleStatusEnum = {
  APPLY: 10, // 申请中
  WAIT_BUYER_RETURN: 20, // 商品待退货
  WAIT_RETURN: 30, // 待买家退货 / 卖家待收货
  WAIT_REFUND: 40, // 待退款
  COMPLETE: 50, // 已完成
  BUYER_CANCEL: 61, // 买家取消
  SELLER_DISAGREE: 62, // 商家拒绝
  SELLER_REFUSE_RECEIVE: 63, // 商家拒收货
}

/**
 * 佣金提现状态枚举
 */
export const BrokerageWithdrawStatusEnum = {
  AUDITING: 0, // 审核中
  AUDIT_SUCCESS: 10, // 审核通过
  WITHDRAW_SUCCESS: 11, // 提现成功
  AUDIT_FAIL: 20, // 审核不通过
  WITHDRAW_FAIL: 21, // 提现失败
}

/**
 * 快递运费模板计费方式枚举
 */
export const DeliveryExpressChargeModeEnum = {
  COUNT: 1, // 按件
  WEIGHT: 2, // 按重量
  VOLUME: 3, // 按体积
}

/**
 * 营销优惠类型枚举
 */
export const PromotionDiscountTypeEnum = {
  PRICE: 1, // 满减（具体金额）
  PERCENT: 2, // 折扣（百分比）
}

/**
 * 营销商品范围枚举
 */
export const PromotionProductScopeEnum = {
  ALL: 1, // 通用券（全部商品）
  SPU: 2, // 指定商品
  CATEGORY: 3, // 指定分类
}

/**
 * 营销满减送条件类型枚举
 */
export const PromotionConditionTypeEnum = {
  PRICE: 10, // 满 N 元
  COUNT: 20, // 满 N 件
}

/**
 * 优惠券模板有效期类型枚举
 */
export const CouponTemplateValidityTypeEnum = {
  DATE: 1, // 固定日期
  TERM: 2, // 领取之后
}

/**
 * 优惠券模板领取方式枚举
 */
export const CouponTemplateTakeTypeEnum = {
  USER: 1, // 直接领取
  ADMIN: 2, // 指定发放
  REGISTER: 3, // 新人券
}

/**
 * 客服消息内容类型枚举
 */
export const KeFuMessageContentTypeEnum = {
  TEXT: 1, // 文本消息
  IMAGE: 2, // 图片消息
  VOICE: 3, // 语音消息
  VIDEO: 4, // 视频消息
  SYSTEM: 5, // 系统消息
  PRODUCT: 10, // 商品消息
  ORDER: 11, // 订单消息
}
