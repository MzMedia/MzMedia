/**
 * 公众号自动回复类型枚举
 */
export const MpAutoReplyTypeEnum = {
  FOLLOW: 1, // 关注时回复
  MESSAGE: 2, // 消息回复
  KEYWORD: 3, // 关键词回复
}

/**
 * 公众号自动回复关键词匹配模式枚举
 */
export const MpAutoReplyRequestMatchEnum = {
  ALL: 1, // 完全匹配
  HALF: 2, // 半匹配
}
