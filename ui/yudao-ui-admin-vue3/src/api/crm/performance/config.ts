import request from '@/config/axios'

export interface CrmPerformanceConfigVO {
  id?: number
  objectId: number
  objectName?: string
  objectType: number
  year: number
  januaryTargetPrice?: number
  februaryTargetPrice?: number
  marchTargetPrice?: number
  aprilTargetPrice?: number
  mayTargetPrice?: number
  juneTargetPrice?: number
  julyTargetPrice?: number
  augustTargetPrice?: number
  septemberTargetPrice?: number
  octoberTargetPrice?: number
  novemberTargetPrice?: number
  decemberTargetPrice?: number
  bizType: number
  yearTargetPrice?: number
  createTime?: Date
}

export enum PerformanceConfigObjectTypeEnum {
  DEPT = 2,
  USER = 3
}

// 业绩目标设置 API
export const PerformanceConfigApi = {
  // 查询业绩目标设置分页
  getPerformanceConfigPage: async (params: any) => {
    return await request.get({ url: '/crm/performance-config/page', params })
  },

  // 获得业绩目标设置详情
  getPerformanceConfig: async (id: number) => {
    return await request.get({ url: `/crm/performance-config/get?id=${id}` })
  },

  // 新增业绩目标设置
  createPerformanceConfig: async (data: CrmPerformanceConfigVO) => {
    return await request.post({ url: '/crm/performance-config/create', data })
  },

  // 修改业绩目标设置
  updatePerformanceConfig: async (data: CrmPerformanceConfigVO) => {
    return await request.put({ url: '/crm/performance-config/update', data })
  },

  // 删除业绩目标设置
  deletePerformanceConfig: async (id: number) => {
    return await request.delete({ url: `/crm/performance-config/delete?id=${id}` })
  }
}
