import request from '@/config/axios'

/** FMS 凭证字信息 */
export interface FmsVoucherWordVO {
  id: number // 凭证字编号
  accountSetId: number // 账套编号
  name: string // 凭证字
  printTitle?: string // 打印标题
  defaultStatus: boolean // 是否默认凭证字
  sort?: number // 显示顺序
  createTime?: Date // 创建时间
}

// FMS 凭证字 API
export const FmsVoucherWordApi = {
  // 查询凭证字列表
  getVoucherWordList: async (accountSetId: number) => {
    return await request.get<FmsVoucherWordVO[]>({
      url: '/fms/config/voucher-word/list',
      params: { accountSetId }
    })
  },

  // 查询凭证字精简列表
  getVoucherWordSimpleList: async (accountSetId: number) => {
    return await request.get<FmsVoucherWordVO[]>({
      url: '/fms/config/voucher-word/simple-list',
      params: { accountSetId }
    })
  },

  // 新增凭证字
  createVoucherWord: async (data: FmsVoucherWordVO) => {
    return await request.post<number>({ url: '/fms/config/voucher-word/create', data })
  },

  // 修改凭证字
  updateVoucherWord: async (data: FmsVoucherWordVO) => {
    return await request.put({ url: '/fms/config/voucher-word/update', data })
  },

  // 删除凭证字
  deleteVoucherWord: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/voucher-word/delete',
      params: { accountSetId, id }
    })
  }
}
