import request from '@/config/axios'

/** FMS 财务指标信息 */
export interface FmsFinanceIndicatorVO {
  id?: number // 指标编号
  accountSetId: number // 账套编号
  name: string // 指标名称
  code: string // 指标编码
  type: number // 取数报表类型
  formula: string // 指标公式
  sort: number // 展示顺序
  status: number // 状态
  createTime?: Date // 创建时间
}

/** FMS 财务指标 API */
export const FmsFinanceIndicatorApi = {
  // 查询财务指标详情
  getFinanceIndicator: async (accountSetId: number, id: number) => {
    return await request.get<FmsFinanceIndicatorVO>({
      url: '/fms/config/finance-indicator/get',
      params: { accountSetId, id }
    })
  },

  // 查询财务指标列表
  getFinanceIndicatorList: async (accountSetId: number) => {
    return await request.get<FmsFinanceIndicatorVO[]>({
      url: '/fms/config/finance-indicator/list',
      params: { accountSetId }
    })
  },

  // 新增财务指标
  createFinanceIndicator: async (data: FmsFinanceIndicatorVO) => {
    return await request.post<number>({ url: '/fms/config/finance-indicator/create', data })
  },

  // 修改财务指标
  updateFinanceIndicator: async (data: FmsFinanceIndicatorVO) => {
    return await request.put({ url: '/fms/config/finance-indicator/update', data })
  },

  // 删除财务指标
  deleteFinanceIndicator: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/finance-indicator/delete',
      params: { accountSetId, id }
    })
  }
}
