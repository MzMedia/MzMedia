import request from '@/config/axios'

export interface CrmStatisticsPerformanceTargetRespVO {
  month: number // 月份
  targetPrice: number // 目标金额
  currentPrice: number // 完成金额
  completionRate: number // 完成率
}

// 业绩目标完成情况 API
export const StatisticsPerformanceTargetApi = {
  // 获得业绩目标完成情况
  getPerformanceTargetSummary: (params: any) => {
    return request.get({
      url: '/crm/statistics-performance-target/get-performance-target-summary',
      params
    })
  }
}
