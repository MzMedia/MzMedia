import { KeFuMessageContentTypeEnum, UserTypeEnum } from '@/utils/constants'
import { fenToYuan } from '@/utils/format'

/** 渲染用消息结构（统一形状，避免联合类型在模板取值报错） */
export interface KefuParsedMessage {
  id?: number
  conversationId?: number
  createTime?: string | number
  fromAdmin: boolean // 是否客服（管理员）发送，决定气泡左右
  senderAvatar?: string // 发送者头像
  kind: 'system' | 'image' | 'text' | 'product' | 'order'
  text: string
  picUrl?: string
  title?: string
  price?: number
  orderNo?: string
  payPrice?: number
  productCount?: number
}

/** 解析消息 content（文本/图片/商品/订单均为 JSON 结构） */
export function parseKefuContent(content: any): Record<string, any> {
  if (typeof content !== 'string') {
    return content || {}
  }
  const text = content.trim()
  if (!text.startsWith('{')) {
    return { text }
  }
  try {
    return JSON.parse(text)
  } catch {
    return { text }
  }
}

/** 解析单条消息为渲染结构 */
export function parseKefuMessage(item: Record<string, any>): KefuParsedMessage {
  const payload = parseKefuContent(item.content)
  const isSystem = item.contentType === KeFuMessageContentTypeEnum.SYSTEM
  const isImage = item.contentType === KeFuMessageContentTypeEnum.IMAGE || (!isSystem && !!payload.picUrl)
  const base = {
    id: item.id,
    conversationId: item.conversationId,
    createTime: item.createTime,
    fromAdmin: item.senderType === UserTypeEnum.ADMIN,
    senderAvatar: item.senderAvatar,
    text: '',
    picUrl: payload.picUrl,
  }
  // 商品消息：spuName/picUrl/price(分)
  if (item.contentType === KeFuMessageContentTypeEnum.PRODUCT) {
    return { ...base, kind: 'product', title: payload.spuName, price: fenToYuan(payload.price) }
  }
  // 订单消息：no/payPrice(分)/productCount
  if (item.contentType === KeFuMessageContentTypeEnum.ORDER) {
    return { ...base, kind: 'order', orderNo: payload.no, payPrice: fenToYuan(payload.payPrice), productCount: payload.productCount }
  }
  return {
    ...base,
    kind: isSystem ? 'system' : (isImage ? 'image' : 'text'),
    text: payload.text || (typeof item.content === 'string' ? item.content : ''),
  }
}

/** 会话列表「最后一条消息」预览文案 */
export function kefuLastMessagePreview(content: any, contentType?: number): string {
  switch (contentType) {
    case KeFuMessageContentTypeEnum.IMAGE: return '[图片]'
    case KeFuMessageContentTypeEnum.VOICE: return '[语音]'
    case KeFuMessageContentTypeEnum.VIDEO: return '[视频]'
    case KeFuMessageContentTypeEnum.PRODUCT: return '[商品]'
    case KeFuMessageContentTypeEnum.ORDER: return '[订单]'
    default: {
      const payload = parseKefuContent(content)
      return payload.text || (typeof content === 'string' ? content : '') || '暂无消息'
    }
  }
}

export const KEFU_EMOJIS = '😀 😁 😂 🤣 😊 😍 😘 😎 🤔 😅 😭 😡 😴 🥰 😉 😜 👍 👎 👌 🙏 💪 👏 🙌 🤝 ❤️ 💔 🔥 ⭐ 🎉 🎁 💰 ✅'.split(' ') // 客服聊天常用表情（空格分隔，split 保留 ❤️ 等多码点表情）
