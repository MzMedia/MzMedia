import request from '@/config/axios'

// 录像文件
export interface CloudRecordFileVO {
  id: number
  app: string
  stream: string
  time: number
  startDate: string
  endDate: string
  filePath: string
  fileName: string
  fileSize: number
  mediaServerId: string
  callId?: string
}

// 录像计划
export interface RecordPlanVO {
  id?: number
  name: string
  planItemList?: RecordPlanItemVO[]
  channelCount?: number
  createTime?: string
}

export interface RecordPlanItemVO {
  dayOfWeek: number // 1-7
  start: string // HH:mm:ss
  stop: string // HH:mm:ss
}

// 报警记录
export interface MediaAlarmVO {
  id: number
  deviceId: string
  channelId: string
  alarmPriority: string
  alarmMethod: string
  alarmTime: string
  alarmDescription: string
  longitude?: number
  latitude?: number
  alarmType?: string
  snapUrl?: string
}

// ==================== 云端录像 ====================
// 查询有录像的日期列表
export const getRecordDateList = (app: string, stream: string) => {
  return request.get({ url: '/api/cloud/record/date/list', params: { app, stream } })
}

// 查询某时间段录像文件列表
export const getCloudRecordList = (params: any) => {
  return request.get({ url: '/api/cloud/record/list', params })
}

// 获取录像播放地址
export const getRecordPlayPath = (recordId: number) => {
  return request.get({ url: '/api/cloud/record/play/path', params: { recordId } })
}

// 删除录像
export const deleteCloudRecord = (recordId: number) => {
  return request.delete({ url: '/api/cloud/record/delete', params: { recordId } })
}

// ==================== 录像计划 ====================
// 查询录像计划列表
export const getRecordPlanList = (params: any) => {
  return request.get({ url: '/api/record/plan/query', params })
}

// 保存录像计划
export const addRecordPlan = (data: any) => {
  return request.post({ url: '/api/record/plan/add', data })
}

export const updateRecordPlan = (data: any) => {
  return request.post({ url: '/api/record/plan/update', data })
}

export const deleteRecordPlan = (id: number) => {
  return request.delete({ url: '/api/record/plan/delete', params: { id } })
}

// 关联计划与通道
export const linkRecordPlan = (planId: number, channelIds: number[]) => {
  return request.post({ url: '/api/record/plan/link', data: { planId, channelIds } })
}

// 获取计划关联的通道列表
export const getPlanChannels = (planId: number) => {
  return request.get({ url: '/api/record/plan/channel/list', params: { planId } })
}

// ==================== 报警管理 ====================
// 查询报警记录列表
export const getMediaAlarmList = (params: any) => {
  return request.get({ url: '/api/alarm/list', params })
}

// 删除单条报警记录
export const deleteMediaAlarm = (id: number) => {
  return request.delete({ url: '/api/alarm/delete', params: { id } })
}

// 清空报警记录
export const clearMediaAlarm = () => {
  return request.delete({ url: '/api/alarm/clear' })
}
