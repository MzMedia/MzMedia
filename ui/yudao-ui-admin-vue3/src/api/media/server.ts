import request from '@/config/axios'

// 流媒体服务器节点 VO
export interface MediaServerVO {
  id: string
  ip: string
  httpPort: number
  httpSSlPort?: number
  rtmpPort?: number
  rtmpSSlPort?: number
  rtpProxyPort?: number
  rtspPort?: number
  rtspSSLPort?: number
  autoConfig?: boolean
  secret?: string
  status?: boolean
  recordAssistPort?: number
  defaultServer?: boolean
  hookAdmin(): boolean
  currentCpu?: number
  currentMemory?: number
  netBandwidth?: number
  streamCount?: number
}

// 查询流媒体服务器列表
export const getMediaServerList = () => {
  return request.get({ url: '/api/server/media_server/list' })
}

// 查询在线流媒体服务器列表
export const getOnlineMediaServerList = () => {
  return request.get({ url: '/api/server/media_server/online/list' })
}

// 查询服务器系统状态信息
export const getSystemInfo = () => {
  return request.get({ url: '/api/server/system/info' })
}

// 查询流媒体负载
export const getMediaServerLoad = () => {
  return request.get({ url: '/api/server/media_server/load' })
}

// 保存流媒体服务配置
export const saveMediaServer = (data: any) => {
  return request.post({ url: '/api/server/media_server/save', data })
}

// 删除流媒体服务
export const deleteMediaServer = (id: string) => {
  return request.delete({ url: '/api/server/media_server/delete', params: { id } })
}

// 测试流媒体服务器连接
export const checkMediaServer = (ip: string, port: number, secret: string) => {
  return request.get({ url: '/api/server/media_server/check', params: { ip, port, secret } })
}
