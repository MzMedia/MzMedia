import { http } from '@/http/http'

/** 用户端频道素材详情 */
export interface ImChannelMaterialRespVO {
  id: number
  channelId: number
  type: number
  title: string
  coverUrl?: string
  summary?: string
  content?: string
  url?: string
}

/** 获取频道素材详情 */
export function getChannelMaterial(id: number) {
  return http.get<ImChannelMaterialRespVO>('/im/channel/material/get', { id })
}
