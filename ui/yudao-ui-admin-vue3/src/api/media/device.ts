import request from '@/config/axios'

// GB28181 设备对象
export interface GbDeviceVO {
  id?: number
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
  enable: boolean | number
  enableAudio?: boolean
  enableMp4?: boolean
  mediaServerId?: string
  status?: boolean
  createTime?: string
}

// 查询国标设备列表
export const getGbDeviceList = (params: any) => {
  return request.get({
    url: '/video/device/page',
    params: {
      pageNo: params?.pageNo || params?.page || 1,
      pageSize: params?.pageSize || params?.count || 20,
      name: params?.search || params?.name,
      deviceId: params?.deviceId
    }
  })
}

// 同步国标设备通道
export const syncGbDeviceChannels = (deviceId: string) => {
  return request.post({
    url: '/video/device/sync-channels',
    params: { deviceId }
  })
}

// 删除国标设备
export const deleteGbDevice = (deviceId: string) => {
  return request.delete({
    url: '/video/device/delete',
    params: { deviceId }
  })
}

// 获取国标设备下通道列表
export const getGbDeviceChannels = (deviceId: string, _params?: any) => {
  return request.get({
    url: '/video/channel/list-by-device',
    params: { deviceId }
  })
}

// 查询拉流代理列表
export const getStreamProxyList = (params: any) => {
  return request.get({
    url: '/video/stream-proxy/page',
    params: {
      pageNo: params?.pageNo || params?.page || 1,
      pageSize: params?.pageSize || params?.count || 20,
      name: params?.search || params?.name,
      app: params?.app,
      stream: params?.stream
    }
  })
}

// 添加拉流代理
export const addStreamProxy = (data: StreamProxyVO) => {
  return request.post({ url: '/video/stream-proxy/create', data })
}

// 修改拉流代理
export const updateStreamProxy = (data: StreamProxyVO) => {
  return request.put({ url: '/video/stream-proxy/update', data })
}

// 删除拉流代理
export const deleteStreamProxy = (app: string, stream: string, id?: number) => {
  return request.delete({
    url: '/video/stream-proxy/delete',
    params: { id, app, stream }
  })
}

// 启用/停用拉流代理
export const startStreamProxy = (app: string, stream: string, id?: number) => {
  return request.post({
    url: '/video/stream-proxy/start',
    params: { id, app, stream }
  })
}

export const stopStreamProxy = (app: string, stream: string, id?: number) => {
  return request.post({
    url: '/video/stream-proxy/stop',
    params: { id, app, stream }
  })
}
