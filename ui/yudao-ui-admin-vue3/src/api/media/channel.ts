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
  hasAudio?: boolean
  dataType?: number // 0-GB28181, 1-拉流代理, 2-推流, 4-ONVIF, 5-海康, 6-大华, 7-萤石, 200-JT1078
}

// 播放流信息
export interface StreamInfoVO {
  app: string
  stream: string
  ip: string
  port: number
  flv: string
  https_flv: string
  ws_flv: string
  wss_flv: string
  fmp4: string
  https_fmp4: string
  ws_fmp4: string
  wss_fmp4: string
  hls: string
  https_hls: string
  ws_hls: string
  wss_hls: string
  ts: string
  https_ts: string
  ws_ts: string
  wss_ts: string
  rtc: string
  rtcs: string
  rtsp: string
  rtmps: string
  rtmp: string
  mediaServerId: string
  tracks?: any[]
}

// 分页查询通道列表
export const getChannelPage = (params: any) => {
  return request.get({ url: '/api/common/channel/list', params })
}

// 查询单个通道详情
export const getChannelOne = (id: number) => {
  return request.get({ url: '/api/common/channel/one', params: { id } })
}

// 开始点播直播流
export const playChannel = (channelId: number) => {
  return request.get({ url: '/api/common/channel/play', params: { channelId } })
}

// 停止点播
export const stopPlayChannel = (channelId: number) => {
  return request.get({ url: '/api/common/channel/play/stop', params: { channelId } })
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
  return request.get({
    url: `/api/front-end/ptz/${deviceId}/${channelId}`,
    params: { cmdCode, horizonSpeed, verticalSpeed, zoomSpeed }
  })
}

// 光圈控制
export const irisControl = (deviceId: string, channelId: string, cmdCode: number, speed: number = 50) => {
  return request.get({
    url: `/api/front-end/fi/iris/${deviceId}/${channelId}`,
    params: { cmdCode, speed }
  })
}

// 聚焦控制
export const focusControl = (deviceId: string, channelId: string, cmdCode: number, speed: number = 50) => {
  return request.get({
    url: `/api/front-end/fi/focus/${deviceId}/${channelId}`,
    params: { cmdCode, speed }
  })
}

// 查询预置位列表
export const queryPresets = (deviceId: string, channelId: string) => {
  return request.get({ url: `/api/front-end/preset/query/${deviceId}/${channelId}` })
}

// 调用预置位
export const callPreset = (deviceId: string, channelId: string, presetId: number) => {
  return request.get({
    url: `/api/front-end/preset/call/${deviceId}/${channelId}`,
    params: { presetId }
  })
}

// 添加预置位
export const addPreset = (deviceId: string, channelId: string, presetName: string, presetId: number) => {
  return request.get({
    url: `/api/front-end/preset/add/${deviceId}/${channelId}`,
    params: { presetName, presetId }
  })
}

// 电子地图通道点位列表
export const getMapChannelList = () => {
  return request.get({ url: '/api/common/channel/map/list' })
}

// 开启语音对讲
export const startAudioTalk = (channelId: number) => {
  return request.get({ url: '/api/common/channel/talk/start', params: { channelId } })
}

// 停止语音对讲
export const stopAudioTalk = (channelId: number) => {
  return request.get({ url: '/api/common/channel/talk/stop', params: { channelId } })
}
