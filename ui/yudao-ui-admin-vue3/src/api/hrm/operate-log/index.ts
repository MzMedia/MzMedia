import request from '@/config/axios'

export interface OperateLogVO extends PageParam {
  bizType: number // 业务类型
  bizId: number // 业务编号
}

// 获得操作日志
export const getOperateLogPage = async (params: OperateLogVO) => {
  return await request.get({ url: '/hrm/operate-log/page', params })
}
