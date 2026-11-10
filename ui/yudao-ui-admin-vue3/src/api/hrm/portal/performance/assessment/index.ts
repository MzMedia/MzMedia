import request from '@/config/axios'
import type {
  PerformanceAssessmentVO,
  PerformanceProcessRecordVO,
  PerformanceReviewRejectReqVO,
  PerformanceScorePreviewVO
} from '@/api/hrm/performance/assessment'

// 员工端绩效考核摘要 VO
export interface PortalPerformanceAssessmentSummaryVO {
  id: number // 员工绩效考核编号
  planId: number // 绩效计划编号
  name?: string // 绩效计划名称
  status?: number // 考核状态
  stageType?: number // 当前阶段
  score?: number // 考核得分
  resultLevel?: string // 结果等级
  coefficient?: number // 绩效系数
  resultAuditStatus?: number // 结果审核状态
  resultAuditTime?: Date // 结果审核时间
  resultAuditReason?: string // 结果审核原因
  appealReason?: string // 申诉原因
  appealStatus?: number // 申诉状态
  appealTime?: Date // 申诉时间
  appealComment?: string // 申诉说明
  startTime?: string // 考核开始时间
  endTime?: string // 考核结束时间
  archiveTime?: Date // 归档时间
}

// 员工端绩效任务数量 VO
export interface PortalPerformanceTaskCountVO {
  fillPendingCount: number // 待填写指标数量
  fillCompletedCount: number // 已填写指标数量
  targetPendingCount: number // 待确认目标数量
  targetCompletedCount: number // 已确认目标数量
  reviewPendingCount: number // 待评分数量
  reviewCompletedCount: number // 已评分数量
  resultAuditPendingCount: number // 待审核结果数量
  resultAuditCompletedCount: number // 已审核结果数量
  resultConfirmationPendingCount: number // 待确认结果数量
  resultConfirmationCompletedCount: number // 已确认结果数量
  resultConfirmationAppealedCount: number // 已申诉结果数量
  appealPendingCount: number // 待处理申诉数量
  appealCompletedCount: number // 已处理申诉数量
}

// 员工端员工绩效考核 VO
export type PortalPerformanceAssessmentVO = PerformanceAssessmentVO

// 员工端绩效流程记录 VO
export type PerformanceRecordVO = PerformanceProcessRecordVO

// 员工端绩效确认 Request VO
export interface PerformanceConfirmReqVO {
  assessmentId: number // 员工绩效考核编号
  pass: number // 是否通过
  comment?: string // 确认意见
}

// 员工端绩效申诉 Request VO
export interface PerformanceAppealReqVO {
  assessmentId: number // 员工绩效考核编号
  appealReason: string // 申诉原因
  appealFileUrls?: string[] // 申诉附件地址列表
  reviewStageIds: number[] // 退回评分节点编号列表
}

// 员工端绩效流程响应 VO
export interface PerformanceProcessRespVO {
  id: number // 业务编号
  nextStageId?: number // 下一运行阶段编号
}

// 员工端绩效运行阶段处理 Request VO
export interface PerformanceHandleStageReqVO {
  assessmentId: number // 员工绩效考核编号
  stageId: number // 运行阶段编号
  pass: number // 是否通过
  comment?: string // 处理意见
  reviewStageIds?: number[] // 退回评分阶段编号列表
}

// 员工端绩效指标保存 Request VO
export interface PerformanceAssessmentQuotaSaveVO {
  id?: number // 指标编号
  dimensionId?: number // 绩效维度编号
  name?: string // 指标名称
  description?: string // 指标说明
  standard?: string // 标准值
  weight?: number // 指标权重
  scoreType?: number // 分数类型
  targetValue?: string // 目标值
  actualValue?: string // 实际值
  selfScore?: number // 自评分数
  reviewerScore?: number // 评分人得分
  finalScore?: number // 最终得分
  comment?: string // 说明
  sort?: number // 排序
}

// 员工端绩效评分 Request VO
export interface PerformanceReviewScoreReqVO {
  assessmentId: number // 员工绩效考核编号
  reviewStageId: number // 评分阶段编号
  comment?: string // 评分说明
  selfComment?: string // 自评说明
  reviewerComment?: string // 评分人说明
  quotas: PerformanceAssessmentQuotaSaveVO[] // 指标列表
}

// 员工端绩效指标填写 Request VO
export interface PerformanceQuotaReqVO {
  assessmentId: number // 员工绩效考核编号
  quotas: PerformanceAssessmentQuotaSaveVO[] // 指标列表
}

// 获得我的绩效分页
export const getPerformanceAssessmentPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentSummaryVO[]>>({
    url: '/hrm/portal/performance/assessment/page',
    params
  })
}

// 获得我的绩效任务数量
export const getPerformanceAssessmentTaskCount = async (search?: string) => {
  return await request.get<PortalPerformanceTaskCountVO>({
    url: '/hrm/portal/performance/assessment/task-count',
    params: { search }
  })
}

// 获得我的绩效参评详情
export const getPerformanceAssessment = async (id: number, stageId?: number) => {
  return await request.get<PortalPerformanceAssessmentVO>({
    url: '/hrm/portal/performance/assessment/get',
    params: { id, stageId }
  })
}

// 获得我的绩效流程记录列表
export const getPerformanceAssessmentProcessRecordList = async (id: number, stageId?: number) => {
  return await request.get<PerformanceRecordVO[]>({
    url: '/hrm/portal/performance/assessment/process-record-list',
    params: { id, stageId }
  })
}

// 获得我的绩效指标填写任务分页
export const getPerformanceAssessmentFillQuotaTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/fill-quota-task-page',
    params
  })
}

// 获得我的绩效目标确认任务分页
export const getPerformanceAssessmentTargetConfirmationTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/target-confirmation-task-page',
    params
  })
}

// 获得我的绩效评分任务分页
export const getPerformanceAssessmentReviewTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/review-task-page',
    params
  })
}

// 获得我的绩效结果审核任务分页
export const getPerformanceAssessmentResultAuditTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/result-audit-task-page',
    params
  })
}

// 获得我的绩效结果确认任务分页
export const getPerformanceAssessmentResultConfirmationTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/result-confirmation-task-page',
    params
  })
}

// 获得我的绩效申诉处理任务分页
export const getPerformanceAssessmentAppealTaskPage = async (params: PageParam) => {
  return await request.get<PageResult<PortalPerformanceAssessmentVO[]>>({
    url: '/hrm/portal/performance/assessment/appeal-task-page',
    params
  })
}

// 填写绩效指标
export const fillPerformanceAssessmentQuota = async (data: PerformanceQuotaReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/fill-quota',
    data
  })
}

// 确认绩效目标
export const confirmPerformanceAssessmentTarget = async (data: PerformanceConfirmReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/confirm-target',
    data
  })
}

// 预览绩效评分结果
export const previewPerformanceAssessmentScore = async (data: PerformanceReviewScoreReqVO) => {
  return await request.post<PerformanceScorePreviewVO>({
    url: '/hrm/portal/performance/assessment/score-preview',
    data
  })
}

// 提交绩效评分
export const scorePerformanceAssessment = async (data: PerformanceReviewScoreReqVO) => {
  return await request.put<PerformanceProcessRespVO>({
    url: '/hrm/portal/performance/assessment/score',
    data
  })
}

// 驳回绩效评分阶段
export const rejectPerformanceAssessmentReviewStage = async (
  data: PerformanceReviewRejectReqVO
) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/reject-review-stage',
    data
  })
}

// 处理绩效结果审核
export const handlePerformanceAssessmentResultAudit = async (data: PerformanceHandleStageReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/handle-result-audit',
    data
  })
}

// 确认绩效结果
export const confirmPerformanceAssessmentResult = async (data: PerformanceConfirmReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/confirm-result',
    data
  })
}

// 提交绩效申诉
export const submitPerformanceAssessmentAppeal = async (data: PerformanceAppealReqVO) => {
  return await request.put<PerformanceProcessRespVO>({
    url: '/hrm/portal/performance/assessment/submit-appeal',
    data
  })
}

// 处理绩效申诉
export const handlePerformanceAssessmentAppeal = async (data: PerformanceHandleStageReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/portal/performance/assessment/handle-appeal',
    data
  })
}
