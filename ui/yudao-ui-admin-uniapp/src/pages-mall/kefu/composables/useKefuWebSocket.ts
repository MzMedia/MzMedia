// 客服实时链路：复用 yudao 内置 /infra/ws 通道
// 处理两类帧：kefu_message_type（新消息）、kefu_message_read_status_change（管理员已读回执）
// 收到后广播 mall:kefu:message / mall:kefu:read 给客服页，由页面更新消息与会话列表

import { useTokenStore } from '@/store/token'
import { getEnvBaseUrlRoot } from '@/utils'

/** 客服新消息帧 type */
const KEFU_MESSAGE_TYPE = 'kefu_message_type'
/** 客服已读回执帧 type */
const KEFU_MESSAGE_READ = 'kefu_message_read_status_change'

let socketTask: UniApp.SocketTask | null = null
let connected = false
let manualClosed = false
let reconnectAttempts = 0
let heartbeatTimer: ReturnType<typeof setInterval> | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null

/** 拼接 ws 地址 */
function buildUrl(): string {
  const wsBase = getEnvBaseUrlRoot().replace('http', 'ws')
  const tokenStore = useTokenStore()
  const tokenInfo = tokenStore.tokenInfo as any
  const token = tokenInfo?.refreshToken || tokenStore.updateNowTime().validToken
  return `${wsBase}/infra/ws?token=${token}`
}

/** 启动心跳 */
function startHeartbeat() {
  stopHeartbeat()
  heartbeatTimer = setInterval(() => {
    if (socketTask && connected) {
      socketTask.send({ data: 'ping', fail: () => {} })
    }
  }, 30000)
}

/** 停止心跳 */
function stopHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
}

/** 断线重连（指数退避，封顶 30s） */
function scheduleReconnect() {
  if (manualClosed || reconnectTimer) {
    return
  }
  reconnectAttempts++
  const delay = Math.min(1000 * 2 ** Math.min(reconnectAttempts, 5), 30000)
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null
    connectKefuWebSocket()
  }, delay)
}

/** 处理收到的帧 */
function handleFrame(data: string) {
  if (!data || data === 'pong') {
    return
  }
  let frame: { type?: string, content?: string }
  try {
    frame = JSON.parse(data)
  } catch {
    return
  }
  if (!frame.type || !frame.content) {
    return
  }
  // 仅处理客服相关帧，其余（im-notification 等）忽略
  if (frame.type === KEFU_MESSAGE_TYPE) {
    try {
      uni.$emit('mall:kefu:message', JSON.parse(frame.content))
    } catch {}
  } else if (frame.type === KEFU_MESSAGE_READ) {
    try {
      uni.$emit('mall:kefu:read', JSON.parse(frame.content))
    } catch {}
  }
}

/** 建立连接（幂等：已连接或连接中则不重复建连） */
export function connectKefuWebSocket() {
  // socketTask 在 connectSocket 后同步赋值，故「连接中」也会拦住重复建连
  if (socketTask) {
    return
  }
  manualClosed = false
  const url = buildUrl()
  const task = uni.connectSocket({
    url,
    fail: () => {
      if (socketTask === task) {
        socketTask = null
      }
      scheduleReconnect()
    },
  })
  if (!task) {
    scheduleReconnect()
    return
  }
  socketTask = task
  task.onOpen(() => {
    if (socketTask !== task) {
      return
    }
    connected = true
    reconnectAttempts = 0
    startHeartbeat()
  })
  task.onMessage((res) => {
    if (socketTask === task) {
      handleFrame(res.data as string)
    }
  })
  task.onClose(() => {
    if (socketTask !== task) {
      return
    }
    connected = false
    socketTask = null
    stopHeartbeat()
    scheduleReconnect()
  })
  task.onError(() => {
    if (socketTask !== task) {
      return
    }
    connected = false
    task.close({})
  })
}

/** 主动断开（离开客服页时调用） */
export function disconnectKefuWebSocket() {
  manualClosed = true
  stopHeartbeat()
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  socketTask?.close({})
  socketTask = null
  connected = false
}
