import request from '@/config/axios'

// 绩效考核指标 VO
export interface AssessmentQuotaVO {
  name?: string // 指标名称
  illustrate?: string // 指标说明
  standard?: string // 评分标准
  weight?: number // 指标权重
  scoreType?: number // 评分类型
}

// 绩效考核维度 VO
export interface AssessmentDimensionVO {
  name?: string // 维度名称
  quotaType?: number // 指标配置类型
  weight?: number // 维度权重
  remark?: string // 备注
  allowEdit?: boolean // 是否允许员工编辑
  quotas?: AssessmentQuotaVO[] // 考核指标列表
}

// 绩效考核配置 VO
export interface AssessmentConfigVO {
  name: string // 模板名称
  scoreCalculation: number // 计分方式
  upperLimitType: number // 分数上限类型
  upperLimitScore: number // 分数上限
  dimensions?: AssessmentDimensionVO[] // 考核维度列表
}

// 绩效考核模板 VO
export interface AssessmentTemplateVO extends AssessmentConfigVO {
  id?: number // 模板编号
  illustrate?: string // 模板说明
  dimensionCount?: number // 维度数量
  quotaCount?: number // 指标数量
  creator?: string // 创建人
  creatorName?: string // 创建人名称
  createTime?: Date // 创建时间
  updateTime?: Date // 更新时间
}

// 创建绩效考核模板
export const createPerformanceAssessmentTemplate = async (data: AssessmentTemplateVO) => {
  return await request.post<number>({
    url: '/hrm/performance/assessment-template/create',
    data
  })
}

// 修改绩效考核模板
export const updatePerformanceAssessmentTemplate = async (data: AssessmentTemplateVO) => {
  return await request.put<boolean>({
    url: '/hrm/performance/assessment-template/update',
    data
  })
}

// 删除绩效考核模板
export const deletePerformanceAssessmentTemplate = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/assessment-template/delete?id=' + id
  })
}

// 批量删除绩效考核模板
export const deletePerformanceAssessmentTemplateList = async (ids: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/assessment-template/delete-list',
    params: { ids: ids.join(',') }
  })
}

// 获得绩效考核模板详情
export const getPerformanceAssessmentTemplate = async (id: number) => {
  return await request.get<AssessmentTemplateVO>({
    url: '/hrm/performance/assessment-template/get?id=' + id
  })
}

// 获得绩效考核模板分页
export const getPerformanceAssessmentTemplatePage = async (params: PageParam) => {
  return await request.get<PageResult<AssessmentTemplateVO[]>>({
    url: '/hrm/performance/assessment-template/page',
    params
  })
}

// 获得绩效考核模板精简列表
export const getPerformanceAssessmentTemplateSimpleList = async () => {
  return await request.get<AssessmentTemplateVO[]>({
    url: '/hrm/performance/assessment-template/simple-list'
  })
}
