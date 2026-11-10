import request from '@/config/axios'

// 招聘职位 VO
export interface HrmRecruitPostVO {
  id?: number // 招聘职位编号
  postName: string // 职位名称
  deptId?: number // 用人部门编号
  deptName?: string // 用人部门名称
  jobNature?: number // 工作性质
  areaId?: number // 工作城市地区编号
  areaName?: string // 工作城市地区名称
  recruitNum?: number // 招聘人数
  reason?: string // 招聘原因
  workTime?: number // 工作经验要求
  educationRequire?: number // 学历要求
  minSalary?: number // 最低薪资，-1 表示面议
  maxSalary?: number // 最高薪资，-1 表示面议
  salaryUnit?: number // 薪资单位，-1 表示面议
  minAge?: number // 最小年龄，-1 表示不限
  maxAge?: number // 最大年龄，-1 表示不限
  latestEntryTime?: Date // 最迟到岗时间
  ownerEmployeeId?: number // 招聘负责人员工编号
  ownerEmployeeName?: string // 招聘负责人姓名
  interviewEmployeeIds?: number[] // 面试官员工编号数组
  interviewEmployeeNames?: string[] // 面试官姓名数组
  description?: string // 职位描述
  emergencyLevel?: number // 紧急程度
  postTypeId?: number // 职位类型编号
  postTypeName?: string // 职位类型名称
  status?: number // 职位状态
  stopReason?: string // 停止原因
  hasEntryNum?: number // 已入职人数
  recruitSchedule?: number // 招聘进度百分比
  createTime?: Date // 创建时间
}

// 招聘职位状态统计 VO
export interface HrmRecruitPostStatusCountVO {
  status: number // 职位状态
  count: number // 职位数量
}

// 招聘职位状态修改 VO
export interface HrmRecruitPostStatusReqVO {
  id: number // 招聘职位编号
  status: number // 职位状态
  stopReason?: string // 停止原因
}

// 查询招聘职位分页
export const getRecruitPostPage = async (params: PageParam) => {
  return await request.get<PageResult<HrmRecruitPostVO[]>>({
    url: '/hrm/recruit/post/page',
    params
  })
}

// 查询招聘职位详情
export const getRecruitPost = async (id: number) => {
  return await request.get<HrmRecruitPostVO>({ url: '/hrm/recruit/post/get?id=' + id })
}

// 获得招聘职位精简列表
export const getRecruitPostSimpleList = async () => {
  return await request.get<HrmRecruitPostVO[]>({ url: '/hrm/recruit/post/simple-list' })
}

// 获得招聘职位状态统计
export const getRecruitPostStatusCount = async (params: PageParam) => {
  return await request.get<HrmRecruitPostStatusCountVO[]>({
    url: '/hrm/recruit/post/status-count',
    params
  })
}

// 新增招聘职位
export const createRecruitPost = async (data: HrmRecruitPostVO) => {
  return await request.post<number>({ url: '/hrm/recruit/post/create', data })
}

// 修改招聘职位
export const updateRecruitPost = async (data: HrmRecruitPostVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/post/update', data })
}

// 修改招聘职位状态
export const updateRecruitPostStatus = async (data: HrmRecruitPostStatusReqVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/post/update-status', data })
}
