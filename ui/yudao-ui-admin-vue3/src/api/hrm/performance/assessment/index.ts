import request from '@/config/axios'

// 绩效归档计划精简 VO
export interface PerformanceArchivePlanVO {
  id: number // 绩效计划编号
  name: string // 绩效计划名称
}

// 员工绩效考核指标评分 VO
export interface PerformanceAssessmentQuotaScoreVO {
  id?: number // 评分编号
  assessmentStageId?: number // 考核阶段编号
  assessmentQuotaId?: number // 考核指标编号
  score?: number // 分数
  comment?: string // 评分说明
}

// 员工绩效考核阶段 VO
export interface PerformanceAssessmentStageVO {
  id?: number // 考核阶段编号
  assessmentId?: number // 员工绩效考核编号
  type?: number // 业务阶段类型
  handlerEmployeeId?: number // 处理员工编号
  handlerName?: string // 处理人姓名
  name?: string // 阶段名称
  raterType?: number // 评分人类型
  weight?: number // 阶段权重
  scoringType?: number // 评分方式
  visibleContent?: number // 可见内容
  requiredSetting?: boolean // 评语是否必填
  rejectAuthority?: boolean // 是否允许驳回
  sort?: number // 排序
  status?: number // 阶段状态
  score?: number // 阶段得分
  resultLevel?: string // 阶段结果等级
  comment?: string // 评分说明
  rejectReason?: string // 驳回原因
  submitTime?: Date // 提交时间
  deadlineTime?: Date // 截止时间
  canHandle?: boolean // 是否可处理
  canScore?: boolean // 是否可评分
  quotaScoreList?: PerformanceAssessmentQuotaScoreVO[] // 指标评分列表
}

// 员工绩效考核指标 VO
export interface PerformanceAssessmentQuotaVO {
  id?: number // 考核指标编号
  assessmentId?: number // 员工绩效考核编号
  dimensionId?: number // 考核维度编号
  allowEdit?: boolean // 是否允许员工编辑指标
  preset?: boolean // 是否预设指标
  dimensionName?: string // 维度名称
  name?: string // 指标名称
  description?: string // 指标说明
  standard?: string // 评分标准
  dimensionWeight?: number // 维度权重
  weight?: number // 指标权重
  scoreType?: number // 评分类型
  targetValue?: string // 目标值
  actualValue?: string // 实际值
  selfScore?: number // 自评分
  reviewerScore?: number // 他评分
  finalScore?: number // 最终分
  comment?: string // 备注
  sort?: number // 排序
}

// 员工绩效考核维度 VO
export interface PerformanceAssessmentDimensionVO {
  id?: number // 考核维度编号
  assessmentId?: number // 员工绩效考核编号
  name?: string // 维度名称
  quotaType?: number // 指标类型
  weight?: number // 维度权重
  remark?: string // 备注
  allowEdit?: boolean // 是否允许员工编辑指标
  sort?: number // 排序
}

// 员工绩效考核 VO
export interface PerformanceAssessmentVO {
  id?: number // 员工绩效考核编号
  planId?: number // 绩效计划编号
  name?: string // 绩效计划名称
  cycleType?: number // 考核周期类型
  cycle?: string // 考核周期
  startTime?: Date // 考核开始时间
  endTime?: Date // 考核结束时间
  upperLimitScore?: number // 单项评分上限
  employeeId?: number // 员工编号
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  mobile?: string // 手机号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  employeeType?: number // 聘用形式
  employeeStatus?: number // 员工状态
  currentHandlerName?: string // 当前处理人
  status?: number // 考核状态
  processStatus?: number // 流程状态
  stageType?: number // 当前阶段
  stageSort?: number // 当前阶段排序
  score?: number // 绩效得分
  resultLevel?: string // 结果等级
  coefficient?: number // 绩效系数
  targetConfirmationEmployeeName?: string // 目标确认员工姓名
  targetConfirmationResult?: number // 目标确认结果
  targetConfirmationComment?: string // 目标确认意见
  targetConfirmationTime?: Date // 目标确认时间
  canConfirmTarget?: boolean // 是否可确认目标
  selfComment?: string // 自评说明
  reviewerComment?: string // 评分说明
  resultComment?: string // 结果说明
  resultConfirmationTime?: Date // 结果确认时间
  resultAuditStatus?: number // 结果审核状态
  resultAuditTime?: Date // 结果审核时间
  resultAuditReason?: string // 结果审核意见
  appealReason?: string // 申诉原因
  appealFileUrls?: string[] // 申诉附件地址列表
  appealReviewStageIds?: number[] // 申诉退回评分阶段编号列表
  appealSubmitTime?: Date // 申诉提交时间
  appealStatus?: number // 申诉状态
  appealTime?: Date // 申诉完成时间
  appealComment?: string // 申诉审批意见
  archiveTime?: Date // 归档时间
  dimensions?: PerformanceAssessmentDimensionVO[] // 指标维度列表
  quotas?: PerformanceAssessmentQuotaVO[] // 指标列表
  reviewStages?: PerformanceAssessmentStageVO[] // 评分阶段列表
  currentReviewStage?: PerformanceAssessmentStageVO // 当前评分阶段
  stages?: PerformanceAssessmentStageVO[] // 运行阶段列表
  currentStage?: PerformanceAssessmentStageVO // 当前待处理阶段
  createTime?: Date // 创建时间
}

// 员工绩效档案 VO
export interface PerformanceArchiveEmployeeVO {
  employeeId: number // 员工编号
  employeeName: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  mobile?: string // 手机号码
  employeeStatus?: number // 员工状态
  employeeType?: number // 聘用形式
  latestAssessmentId?: number // 最近考核编号
  latestPlanName?: string // 最近考核计划
  latestScore?: number // 最近绩效评分
  latestResultLevel?: string // 最近绩效等级
  assessmentCount: number // 考核次数
}

// 绩效阶段统计 VO
export interface PerformanceStageCountVO {
  stageType?: number // 阶段状态
  count?: number // 员工数量
}

// 绩效等级统计 VO
export interface PerformanceLevelCountVO {
  levelName?: string // 结果等级
  count?: number // 员工数量
}

// 绩效评分预览 VO
export interface PerformanceScorePreviewVO {
  stageScore?: number // 当前阶段得分
  stageResultLevel?: string // 当前阶段结果等级
  cumulativeScore?: number // 当前累计得分
  cumulativeResultLevel?: string // 当前累计结果等级，全部评分阶段完成时返回
}

// 绩效评分驳回 Request VO
export interface PerformanceReviewRejectReqVO {
  assessmentId: number // 员工绩效考核编号
  reviewStageId: number // 评分阶段编号
  reason: string // 驳回原因
}

// 绩效流程记录 VO
export interface PerformanceProcessRecordVO {
  title?: string // 记录标题
  content?: string // 记录内容
  source?: 'ACTION' | 'BUSINESS' | 'STAGE' // 记录来源
  status?: number // 流程状态
  operatorName?: string // 操作人姓名
  operateTime?: Date // 操作时间
  fileUrls?: string[] // 附件地址列表
}

// 添加员工绩效考核
export const addPerformancePlanEmployees = async (data: {
  planId?: number
  employeeIds?: number[]
}) => {
  return await request.post<boolean>({ url: '/hrm/performance/assessment/create-list', data })
}

// 移除员工绩效考核
export const removePerformancePlanEmployees = async (data: {
  planId?: number
  employeeIds?: number[]
}) => {
  return await request.delete<boolean>({ url: '/hrm/performance/assessment/delete-list', data })
}

// 获得员工绩效考核分页
export const getPerformanceAssessmentPage = async (params: PageParam) => {
  return await request.get<PageResult<PerformanceAssessmentVO[]>>({
    url: '/hrm/performance/assessment/page',
    params
  })
}

// 获得未加入指定绩效计划的员工编号列表
export const getPerformancePlanUnassignedEmployeeIdList = async (planId: number) => {
  return await request.get<number[]>({
    url: '/hrm/performance/assessment/unassigned-employee-id-list',
    params: { planId }
  })
}

// 获得员工绩效考核详情
export const getPerformanceAssessment = async (id: number) => {
  return await request.get<PerformanceAssessmentVO>({
    url: '/hrm/performance/assessment/get?id=' + id
  })
}

// 获得绩效流程记录列表
export const getPerformanceAssessmentProcessRecordList = async (id: number) => {
  return await request.get<PerformanceProcessRecordVO[]>({
    url: '/hrm/performance/assessment/process-record-list',
    params: { id }
  })
}

// 获得绩效归档分页
export const getPerformanceAssessmentArchivePage = async (params: PageParam) => {
  return await request.get<PageResult<PerformanceAssessmentVO[]>>({
    url: '/hrm/performance/assessment/archive-page',
    params
  })
}

// 获得员工绩效档案分页
export const getPerformanceArchiveEmployeePage = async (
  params: PageParam & { search?: string }
) => {
  return await request.get<PageResult<PerformanceArchiveEmployeeVO[]>>({
    url: '/hrm/performance/assessment/archive-employee-page',
    params
  })
}

// 获得绩效归档详情
export const getPerformanceAssessmentArchive = async (id: number) => {
  return await request.get<PerformanceAssessmentVO>({
    url: '/hrm/performance/assessment/archive-get?id=' + id
  })
}

// 获得绩效归档流程记录列表
export const getPerformanceAssessmentArchiveProcessRecordList = async (id: number) => {
  return await request.get<PerformanceProcessRecordVO[]>({
    url: '/hrm/performance/assessment/archive-process-record-list',
    params: { id }
  })
}

// 获得绩效归档计划精简列表
export const getPerformanceArchivePlanSimpleList = async () => {
  return await request.get<PerformanceArchivePlanVO[]>({
    url: '/hrm/performance/assessment/archive-plan-simple-list'
  })
}

// 删除绩效归档记录
export const deletePerformanceArchiveRecords = async (ids: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/assessment/archive-delete',
    params: { ids: ids.join(',') }
  })
}

// 删除员工的全部绩效档案
export const deletePerformanceArchiveEmployeeRecords = async (employeeIds: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/performance/assessment/archive-employee-delete',
    params: { employeeIds: employeeIds.join(',') }
  })
}
