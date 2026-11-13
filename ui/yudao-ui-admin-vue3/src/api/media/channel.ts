import request from '@/config/axios'

// 媒体通道对象
export interface MediaChannelVO {
  id: number
  channelId: string
  deviceId: string
  name: string
  manufacture?: string
  model?: string
  owner?: string
  civilCode?: string
  block?: string
  address?: string
  parental?: number
  parentId?: string
  safetyWay?: number
  registerWay?: number
  certNum?: string
  certifiable?: number
  errCode?: number
  endTime?: string
  secrecy?: string
  ipAddress?: string
  port?: number
  password?: string
  status: boolean | number
  longitude?: number
  latitude?: number
  ptzType?: number
  customName?: string
  subCount?: number
  streamId?: string
  hasAudio?: boolean | number
  dataType?: number // 0-GB28181, 1-拉流代理, 2-推流, 4-ONVIF, 5-海康, 6-大华, 7-萤石, 200-JT1078
}

// 播放流信息
export interface StreamInfoVO {
  app: string
  stream: string
  ip?: string
  port?: number
  flv: string
  https_flv?: string
  ws_flv: string
  wss_flv?: string
  fmp4?: string
  https_fmp4?: string
  ws_fmp4?: string
  wss_fmp4?: string
  hls: string
  https_hls?: string
  ws_hls?: string
  wss_hls?: string
  ts?: string
  https_ts?: string
  ws_ts?: string
  wss_ts?: string
  rtc: string
  rtcs?: string
  rtsp: string
  rtmps?: string
  rtmp: string
  mediaServerId: string
  tracks?: any[]
}

// 分页查询通道列表
export const getChannelPage = (params: any) => {
  return request.get({
    url: '/video/channel/page',
    params: {
      pageNo: params?.pageNo || params?.page || 1,
      pageSize: params?.pageSize || params?.count || 20,
      name: params?.search || params?.name,
      channelId: params?.channelId,
      deviceId: params?.deviceId,
      status: params?.status
    }
  })
}

// 查询单个通道详情
export const getChannelOne = (id: number) => {
  return request.get({ url: '/video/channel/get', params: { id } })
}

// 开始点播直播流
export const playChannel = (channelId: number) => {
  return request.post({
    url: '/video/play/start',
    data: { channelId }
  })
}

// 停止点播
export const stopPlayChannel = (channelId: number) => {
  return request.post({
    url: '/video/play/stop',
    data: { channelId }
  })
}

// 云台方向与变倍控制 (PTZ)
// cmdCode: 0-停止, 1-向右, 2-向左, 3-向下, 4-向上, 5-右下, 6-左下, 7-右上, 8-左上, 9-放大, 10-缩小
export const ptzControl = (
  deviceId: string,
  channelId: string,
  cmdCode: number,
  horizonSpeed: number = 50,
  verticalSpeed: number = 50,
  zoomSpeed: number = 50
) => {
  return request.post({
    url: '/video/ptz/control',
    params: { deviceId, channelId, cmdCode, horizonSpeed, verticalSpeed, zoomSpeed }
  })
}

// 光圈控制
export const irisControl = (deviceId: string, channelId: string, cmdCode: number, speed: number = 50) => {
  return request.post({
    url: '/video/ptz/iris',
    params: { deviceId, channelId, cmdCode, speed }
  })
}

// 聚焦控制
export const focusControl = (deviceId: string, channelId: string, cmdCode: number, speed: number = 50) => {
  return request.post({
    url: '/video/ptz/focus',
    params: { deviceId, channelId, cmdCode, speed }
  })
}

// 查询预置位列表
export const queryPresets = (deviceId: string, channelId: string) => {
  return request.get({
    url: '/video/ptz/preset/query',
    params: { deviceId, channelId }
  })
}

// 调用预置位
export const callPreset = (deviceId: string, channelId: string, presetId: number) => {
  return request.post({
    url: '/video/ptz/preset/call',
    params: { deviceId, channelId, presetId }
  })
}

// 添加预置位
export const addPreset = (deviceId: string, channelId: string, presetName: string, presetId: number) => {
  return request.post({
    url: '/video/ptz/preset/add',
    params: { deviceId, channelId, presetName, presetId }
  })
}

// 电子地图通道点位列表
export const getMapChannelList = () => {
  return request.get({ url: '/video/channel/map-list' })
}

// 开启语音对讲
export const startAudioTalk = (channelId: number) => {
  return request.post({
    url: '/video/play/start',
    data: { channelId }
  })
}

// 停止语音对讲
export const stopAudioTalk = (channelId: number) => {
  return request.post({
    url: '/video/play/stop',
    data: { channelId }
  })
}
