import request from '@/config/axios'

// 绩效结果等级 VO
export interface ResultLevelVO {
  name: string // 等级名称
  minScore: number // 最低分数
  maxScore: number // 最高分数
  coefficient: number // 绩效系数
}

// 绩效结果模板 VO
export interface ResultTemplateVO {
  id?: number // 结果模板编号
  name: string // 结果模板名称
  levels: ResultLevelVO[] // 结果等级列表
  status?: number // 状态
  creator?: string // 创建人
  creatorName?: string // 创建人名称
  createTime?: Date // 创建时间
  updateTime?: Date // 更新时间
}

// 创建绩效结果模板
export const createPerformanceResultTemplate = async (data: ResultTemplateVO) => {
  return await request.post<number>({ url: '/hrm/performance/result-template/create', data })
}

// 修改绩效结果模板
export const updatePerformanceResultTemplate = async (data: ResultTemplateVO) => {
  return await request.put<boolean>({ url: '/hrm/performance/result-template/update', data })
}

// 删除绩效结果模板
export const deletePerformanceResultTemplate = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/result-template/delete?id=' + id
  })
}

// 批量删除绩效结果模板
export const deletePerformanceResultTemplateList = async (ids: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/result-template/delete-list',
    params: { ids: ids.join(',') }
  })
}

// 获得绩效结果模板详情
export const getPerformanceResultTemplate = async (id: number) => {
  return await request.get<ResultTemplateVO>({
    url: '/hrm/performance/result-template/get?id=' + id
  })
}

// 获得绩效结果模板分页
export const getPerformanceResultTemplatePage = async (params: PageParam) => {
  return await request.get<PageResult<ResultTemplateVO[]>>({
    url: '/hrm/performance/result-template/page',
    params
  })
}

// 获得绩效结果模板精简列表
export const getPerformanceResultTemplateSimpleList = async (params?: { status?: number }) => {
  return await request.get<ResultTemplateVO[]>({
    url: '/hrm/performance/result-template/simple-list',
    params
  })
}
