import { http } from '@/http/http'

/** 用户端表情包项 */
export interface ImFacePackUserItemVO {
  id: number
  url: string
  name?: string
  width: number
  height: number
}

/** 用户端表情包 */
export interface ImFacePackUserVO {
  id: number
  name: string
  icon?: string
  items: ImFacePackUserItemVO[]
}

/** 拉取所有启用的系统表情包 */
export function getFacePackList() {
  return http.get<ImFacePackUserVO[]>('/im/face-pack/list')
}
