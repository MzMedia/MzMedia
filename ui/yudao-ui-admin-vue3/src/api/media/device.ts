import request from '@/config/axios'

// GB28181 设备对象
export interface GbDeviceVO {
  deviceId: string
  name: string
  manufacturer?: string
  model?: string
  firmware?: string
  transport?: string
  streamMode?: string
  ip?: string
  port?: number
  hostAddress?: string
  onLine?: boolean | number
  channelCount?: number
  registerTime?: string
  keepaliveTime?: string
  mediaServerId?: string
  charset?: string
  customName?: string
}

// 拉流代理对象
export interface StreamProxyVO {
  id?: number
  app: string
  stream: string
  name: string
  url: string
  srcUrl?: string
  enable: boolean
  enableAudio: boolean
  enableMp4: boolean
  mediaServerId: string
  status?: boolean
  createTime?: string
}

// 查询国标设备列表
export const getGbDeviceList = (params: any) => {
  return request.get({ url: '/api/device/query/devices', params })
}

// 同步国标设备通道
export const syncGbDeviceChannels = (deviceId: string) => {
  return request.get({ url: `/api/device/query/devices/${deviceId}/sync` })
}

// 删除国标设备
export const deleteGbDevice = (deviceId: string) => {
  return request.delete({ url: `/api/device/query/devices/${deviceId}/delete` })
}

// 获取国标设备下通道列表
export const getGbDeviceChannels = (deviceId: string, params: any) => {
  return request.get({ url: `/api/device/query/devices/${deviceId}/channels`, params })
}

// 查询拉流代理列表
export const getStreamProxyList = (params: any) => {
  return request.get({ url: '/api/proxy/list', params })
}

// 添加拉流代理
export const addStreamProxy = (data: StreamProxyVO) => {
  return request.post({ url: '/api/proxy/add', data })
}

// 修改拉流代理
export const updateStreamProxy = (data: StreamProxyVO) => {
  return request.post({ url: '/api/proxy/update', data })
}

// 删除拉流代理
export const deleteStreamProxy = (app: string, stream: string) => {
  return request.delete({ url: '/api/proxy/del', params: { app, stream } })
}

// 启用/停用拉流代理
export const startStreamProxy = (app: string, stream: string) => {
  return request.get({ url: '/api/proxy/start', params: { app, stream } })
}

export const stopStreamProxy = (app: string, stream: string) => {
  return request.get({ url: '/api/proxy/stop', params: { app, stream } })
}
