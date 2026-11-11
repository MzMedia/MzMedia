import request from '@/config/axios'

// ==================== ONVIF ====================
export interface OnvifDeviceVO {
  id?: number
  name: string
  ip: string
  port: number
  username?: string
  password?: string
  status?: number
  profiles?: string
  streamUri?: string
  createTime?: string
}

export const probeOnvifDevices = () => {
  return request.get({ url: '/admin-api/media/onvif/probe' })
}

export const getOnvifDeviceList = () => {
  return request.get({ url: '/admin-api/media/onvif/list' })
}

export const saveOnvifDevice = (data: OnvifDeviceVO) => {
  return request.post({ url: '/admin-api/media/onvif/save', data })
}

export const deleteOnvifDevice = (id: number) => {
  return request.delete({ url: `/admin-api/media/onvif/delete?id=${id}` })
}

export const syncOnvifDevice = (id: number) => {
  return request.post({ url: `/admin-api/media/onvif/sync?id=${id}` })
}

export const onvifPtzControl = (id: number, direction: string, speed: number) => {
  return request.post({
    url: '/admin-api/media/onvif/ptz',
    params: { id, direction, speed }
  })
}

// ==================== 海康 ISAPI ====================
export interface HikDeviceVO {
  id?: number
  name: string
  ip: string
  port: number
  username?: string
  password?: string
  channels?: number
  status?: number
  deviceInfo?: string
  createTime?: string
}

export const getHikDeviceList = () => {
  return request.get({ url: '/admin-api/media/hikvision/list' })
}

export const saveHikDevice = (data: HikDeviceVO) => {
  return request.post({ url: '/admin-api/media/hikvision/save', data })
}

export const deleteHikDevice = (id: number) => {
  return request.delete({ url: `/admin-api/media/hikvision/delete?id=${id}` })
}

export const syncHikDevice = (id: number) => {
  return request.post({ url: `/admin-api/media/hikvision/sync?id=${id}` })
}

export const hikPtzControl = (id: number, channel: number, command: string, speed: number) => {
  return request.post({
    url: '/admin-api/media/hikvision/ptz',
    params: { id, channel, command, speed }
  })
}

// ==================== 大华 HTTP RPC / CGI ====================
export interface DahuaDeviceVO {
  id?: number
  name: string
  ip: string
  port: number
  username?: string
  password?: string
  channels?: number
  status?: number
  deviceInfo?: string
  createTime?: string
}

export const getDahuaDeviceList = () => {
  return request.get({ url: '/admin-api/media/dahua/list' })
}

export const saveDahuaDevice = (data: DahuaDeviceVO) => {
  return request.post({ url: '/admin-api/media/dahua/save', data })
}

export const deleteDahuaDevice = (id: number) => {
  return request.delete({ url: `/admin-api/media/dahua/delete?id=${id}` })
}

export const syncDahuaDevice = (id: number) => {
  return request.post({ url: `/admin-api/media/dahua/sync?id=${id}` })
}

export const dahuaPtzControl = (id: number, channel: number, code: string, arg1: number, arg2: number) => {
  return request.post({
    url: '/admin-api/media/dahua/ptz',
    params: { id, channel, code, arg1, arg2 }
  })
}

// ==================== 萤石云 Open API ====================
export interface EzvizDeviceVO {
  id?: number
  name: string
  appKey: string
  appSecret: string
  deviceSerial: string
  channelNo: number
  validateCode?: string
  status?: number
  liveAddress?: string
  createTime?: string
}

export const getEzvizDeviceList = () => {
  return request.get({ url: '/admin-api/media/ezviz/list' })
}

export const saveEzvizDevice = (data: EzvizDeviceVO) => {
  return request.post({ url: '/admin-api/media/ezviz/save', data })
}

export const deleteEzvizDevice = (id: number) => {
  return request.delete({ url: `/admin-api/media/ezviz/delete?id=${id}` })
}

export const syncEzvizDevice = (id: number) => {
  return request.post({ url: `/admin-api/media/ezviz/sync?id=${id}` })
}

export const ezvizPtzControl = (id: number, direction: number, speed: number) => {
  return request.post({
    url: '/admin-api/media/ezviz/ptz',
    params: { id, direction, speed }
  })
}

// ==================== 部标 JT/T 1078 ====================
export interface JT1078DeviceVO {
  id?: number
  simNumber: string
  channel: number
  terminalId?: string
  plateNumber?: string
  status?: number
  mediaServerId?: string
  streamId?: string
  createTime?: string
}

export const getJT1078DeviceList = () => {
  return request.get({ url: '/admin-api/media/jt1078/list' })
}

export const saveJT1078Device = (data: JT1078DeviceVO) => {
  return request.post({ url: '/admin-api/media/jt1078/save', data })
}

export const deleteJT1078Device = (id: number) => {
  return request.delete({ url: `/admin-api/media/jt1078/delete?id=${id}` })
}

export const getJT1078Status = (simNumber: string, channel: number) => {
  return request.get({
    url: '/admin-api/media/jt1078/status',
    params: { simNumber, channel }
  })
}
