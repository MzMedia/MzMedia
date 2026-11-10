import request from '@/config/axios'
import type { AssessmentConfigVO } from '@/api/hrm/performance/config/assessment-template'
import type { ResultLevelVO } from '@/api/hrm/performance/config/result-template'
import type {
  PerformanceLevelCountVO,
  PerformanceStageCountVO
} from '@/api/hrm/performance/assessment'

// 绩效计划处理节点 VO
export interface PerformanceHandlerStageVO {
  type?: number // 处理人类型
  level?: number // 上级或部门层级
  employeeId?: number // 指定处理员工编号
}

// 绩效计划评分阶段 VO
export interface PerformanceReviewStageVO {
  name?: string // 阶段名称
  rater?: PerformanceHandlerStageVO // 评分人
  weight?: number // 阶段权重
  scoringType?: number // 评分方式
  visibleContent?: number // 可见内容
  requiredSetting?: boolean // 评语是否必填
  rejectAuthority?: boolean // 是否允许驳回
}

// 绩效计划考评范围 VO
export interface PerformanceScopeVO {
  type?: number // 范围类型
  employeeIds?: number[] // 员工编号列表
  deptIds?: number[] // 部门编号列表
  employeeType?: number // 聘用形式
  employeeStatuses?: number[] // 员工状态列表
}

// 绩效计划结果配置快照 VO
export interface PerformanceResultConfigVO {
  name: string // 结果模板名称
  levels: ResultLevelVO[] // 结果等级列表
}

// 绩效计划 VO
export interface PerformancePlanVO {
  id?: number // 绩效计划编号
  name: string // 计划名称
  cycleType?: number // 考核周期类型
  cycle?: string // 考核周期
  quarter?: number // 季度
  startTime?: number // 开始时间
  endTime?: number // 结束时间
  description?: string // 考核说明
  scopes?: PerformanceScopeVO[] // 考评范围列表
  assessmentTemplateId?: number // 考核模板编号
  assessmentConfig?: AssessmentConfigVO // 考核配置快照
  resultTemplateId?: number // 结果模板编号
  resultConfig?: PerformanceResultConfigVO // 结果配置快照
  quotaSettingType?: number // 指标制定方式
  targetConfirmation?: boolean // 是否开启目标确认
  targetConfirmationStage?: PerformanceHandlerStageVO // 目标确认节点
  reviewStages?: PerformanceReviewStageVO[] // 评分阶段列表
  resultAudit?: boolean // 是否开启结果审核
  resultAuditStages?: PerformanceHandlerStageVO[] // 结果审核节点列表
  resultConfirmation?: boolean // 是否开启结果确认
  appealStages?: PerformanceHandlerStageVO[] // 申诉确认节点列表
  appealTimeoutDays?: number // 申诉超期天数
  appealTimeoutAction?: number // 申诉超期处理动作
  syncToSalary?: boolean // 是否同步薪资
  paidForMonth?: string // 计薪月份
  assessmentTemplateName?: string // 考核模板名称
  resultTemplateName?: string // 结果模板名称
  stageType?: number // 当前阶段
  status?: number // 计划状态
  operationType?: number // 可操作阶段
  terminateTime?: Date // 终止时间
  employeeCount?: number // 参评员工数量
  finishedCount?: number // 已完成人数
  scoringReady?: boolean // 是否可开启评分
  interviewReady?: boolean // 是否可发起面谈
  archiveReady?: boolean // 是否可归档
  stageCountMap?: Record<number, number> // 各阶段员工数量
  createTime?: Date // 创建时间
}

// 创建绩效计划
export const createPerformancePlan = async (data: PerformancePlanVO) => {
  return await request.post<number>({ url: '/hrm/performance/plan/create', data })
}

// 修改绩效计划
export const updatePerformancePlan = async (data: PerformancePlanVO) => {
  return await request.put<boolean>({ url: '/hrm/performance/plan/update', data })
}

// 删除绩效计划
export const deletePerformancePlan = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/performance/plan/delete?id=' + id })
}

// 获得绩效计划详情
export const getPerformancePlan = async (id: number) => {
  return await request.get<PerformancePlanVO>({ url: '/hrm/performance/plan/get?id=' + id })
}

// 获得绩效计划分页
export const getPerformancePlanPage = async (params: PageParam) => {
  return await request.get<PageResult<PerformancePlanVO[]>>({
    url: '/hrm/performance/plan/page',
    params
  })
}

// 启动绩效计划
export const startPerformancePlan = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/performance/plan/start?id=' + id })
}

// 开启绩效评分
export const openPerformancePlanScoring = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/performance/plan/open-scoring?id=' + id })
}

// 发起绩效面谈
export const startPerformancePlanInterview = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/performance/plan/start-interview?id=' + id })
}

// 归档绩效计划
export const archivePerformancePlan = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/performance/plan/archive?id=' + id })
}

// 终止绩效计划
export const terminatePerformancePlan = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/performance/plan/terminate?id=' + id })
}

// 获得绩效计划状态统计
export const getPerformancePlanStatusCount = async (params: PageParam) => {
  return await request.get<Record<number, number>>({
    url: '/hrm/performance/plan/status-count',
    params
  })
}

// 获得绩效计划阶段统计
export const getPerformancePlanStageCount = async (planId: number) => {
  return await request.get<PerformanceStageCountVO[]>({
    url: '/hrm/performance/plan/stage-count?planId=' + planId
  })
}

// 获得绩效计划等级统计
export const getPerformancePlanLevelCount = async (planId: number) => {
  return await request.get<PerformanceLevelCountVO[]>({
    url: '/hrm/performance/plan/level-count?planId=' + planId
  })
}
