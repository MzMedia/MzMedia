import request from '@/config/axios'

/** FMS 首页指标信息 */
export interface FmsHomeMetricVO {
  key: string // 指标标识
  name: string // 指标名称
  amount: number // 指标金额
}

/** FMS 首页指标趋势信息 */
export interface FmsHomeTrendVO {
  month: string // 会计期间
  metrics: FmsHomeMetricVO[] // 动态财务指标
  income: number // 收入
  operatingCost: number // 成本
  profit: number // 利润
  expense: number // 费用
  other: number // 其他
}

/** FMS 首页信息 */
export interface FmsHomeVO {
  currentMonth: string // 当前会计期间
  metrics: FmsHomeMetricVO[] // 当期财务指标数组
  trends: FmsHomeTrendVO[] // 财务指标趋势数组
}

/** FMS 首页指标趋势明细信息 */
export interface FmsHomeMetricTrendVO {
  month: string // 会计期间
  amount: number // 指标金额
}

/** FMS 首页指标科目构成信息 */
export interface FmsHomeMetricStructureVO {
  subjectId: number // 科目编号
  subjectCode: string // 科目编码
  subjectName: string // 科目名称
  amount: number // 科目金额
}

/** FMS 首页指标明细信息 */
export interface FmsHomeMetricDetailVO {
  key: string // 指标标识
  name: string // 指标名称
  trends: FmsHomeMetricTrendVO[] // 财务指标趋势数组
  structure: FmsHomeMetricStructureVO[] // 当期科目构成数组
}

/** FMS 首页 API */
export const FmsHomeApi = {
  // 查询首页数据
  getHome: async (accountSetId: number) => {
    return await request.get<FmsHomeVO>({
      url: '/fms/home/get',
      params: { accountSetId }
    })
  },

  // 查询首页财务指标明细
  getHomeMetricDetail: async (accountSetId: number, metricKey: string) => {
    return await request.get<FmsHomeMetricDetailVO>({
      url: '/fms/home/metric-detail',
      params: { accountSetId, metricKey }
    })
  }
}
