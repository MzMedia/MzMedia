import request from '@/config/axios'

/** FMS 常用摘要信息 */
export interface FmsDigestVO {
  id?: number // 摘要编号
  accountSetId: number // 账套编号
  content: string // 摘要内容
  createTime?: Date // 创建时间
}

// FMS 常用摘要 API
export const FmsDigestApi = {
  // 查询常用摘要列表
  getDigestList: async (accountSetId: number) => {
    return await request.get<FmsDigestVO[]>({
      url: '/fms/config/digest/list',
      params: { accountSetId }
    })
  },

  // 查询常用摘要精简列表
  getDigestSimpleList: async (accountSetId: number) => {
    return await request.get<FmsDigestVO[]>({
      url: '/fms/config/digest/simple-list',
      params: { accountSetId }
    })
  },

  // 新增常用摘要
  createDigest: async (data: FmsDigestVO) => {
    return await request.post<number>({ url: '/fms/config/digest/create', data })
  },

  // 修改常用摘要
  updateDigest: async (data: FmsDigestVO) => {
    return await request.put({ url: '/fms/config/digest/update', data })
  },

  // 删除常用摘要
  deleteDigest: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/digest/delete',
      params: { accountSetId, id }
    })
  }
}
