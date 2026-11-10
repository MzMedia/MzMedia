import request from '@/config/axios'

// 招聘面试 VO
export interface HrmRecruitInterviewVO {
  id?: number // 面试编号
  candidateId?: number // 候选人编号
  type?: number // 面试方式
  stageNumber?: number // 面试轮次
  interviewEmployeeId?: number // 主面试官员工编号
  interviewEmployeeName?: string // 主面试官姓名
  otherInterviewEmployeeIds?: number[] // 其他面试官员工编号数组
  otherInterviewEmployeeNames?: string[] // 其他面试官姓名数组
  interviewTime?: Date // 面试时间
  address?: string // 面试地址
  remark?: string // 备注
  result?: number // 面试结果
  evaluate?: string // 面试评价
  cancelReason?: string // 取消原因
  createTime?: Date // 创建时间
}

// 招聘面试结果修改 Request VO
export interface HrmRecruitInterviewResultReqVO {
  id: number // 面试编号
  result: number // 面试结果
  evaluate?: string // 面试评价
  cancelReason?: string // 取消原因
}

// 查询招聘面试详情
export const getRecruitInterview = async (id: number) => {
  return await request.get<HrmRecruitInterviewVO>({
    url: '/hrm/recruit/interview/get?id=' + id
  })
}

// 查询候选人的招聘面试列表
export const getRecruitInterviewListByCandidate = async (candidateId: number) => {
  return await request.get<HrmRecruitInterviewVO[]>({
    url: '/hrm/recruit/interview/list-by-candidate?candidateId=' + candidateId
  })
}

// 新增招聘面试
export const createRecruitInterview = async (data: HrmRecruitInterviewVO) => {
  return await request.post<number>({ url: '/hrm/recruit/interview/create', data })
}

// 修改招聘面试
export const updateRecruitInterview = async (data: HrmRecruitInterviewVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/interview/update', data })
}

// 修改招聘面试结果
export const updateRecruitInterviewResult = async (data: HrmRecruitInterviewResultReqVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/interview/update-result', data })
}

// 删除招聘面试
export const deleteRecruitInterview = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/recruit/interview/delete?id=' + id
  })
}
