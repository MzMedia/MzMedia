import request from '@/config/axios'

/** FMS 币别信息 */
export interface FmsCurrencyVO {
  id?: number // 币别编号
  accountSetId: number // 账套编号
  code: string // 币别编码
  name: string // 币别名称
  exchangeRate: number // 汇率
  standard?: boolean // 是否本位币
  createTime?: Date // 创建时间
}

// FMS 币别 API
export const FmsCurrencyApi = {
  // 查询币别列表
  getCurrencyList: async (accountSetId: number) => {
    return await request.get<FmsCurrencyVO[]>({
      url: '/fms/config/currency/list',
      params: { accountSetId }
    })
  },

  // 查询币别精简列表
  getCurrencySimpleList: async (accountSetId: number) => {
    return await request.get<FmsCurrencyVO[]>({
      url: '/fms/config/currency/simple-list',
      params: { accountSetId }
    })
  },

  // 新增币别
  createCurrency: async (data: FmsCurrencyVO) => {
    return await request.post<number>({ url: '/fms/config/currency/create', data })
  },

  // 修改币别
  updateCurrency: async (data: FmsCurrencyVO) => {
    return await request.put({ url: '/fms/config/currency/update', data })
  },

  // 删除币别
  deleteCurrency: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/currency/delete',
      params: { accountSetId, id }
    })
  }
}
