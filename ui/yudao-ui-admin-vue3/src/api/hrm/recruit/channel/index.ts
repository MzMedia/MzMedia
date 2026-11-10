import request from '@/config/axios'

// 招聘渠道 VO
export interface HrmRecruitChannelVO {
  id?: number // 招聘渠道编号
  systemFlag?: boolean // 是否系统内置
  status?: number // 状态
  name: string // 渠道名称
  sort: number // 显示顺序
  remark?: string // 备注
  createTime?: Date // 创建时间
}

// 招聘渠道状态修改 VO
export interface HrmRecruitChannelStatusReqVO {
  id: number // 招聘渠道编号
  status: number // 状态
}

// 招聘渠道删除 VO
export interface HrmRecruitChannelDeleteReqVO {
  id: number // 待删除招聘渠道编号
  transferChannelId: number // 承接招聘渠道编号
}

// 查询招聘渠道分页
export const getRecruitChannelPage = async (params: PageParam) => {
  return await request.get<PageResult<HrmRecruitChannelVO[]>>({
    url: '/hrm/recruit/channel/page',
    params
  })
}

// 查询招聘渠道详情
export const getRecruitChannel = async (id: number) => {
  return await request.get<HrmRecruitChannelVO>({ url: '/hrm/recruit/channel/get?id=' + id })
}

// 查询招聘渠道精简列表
export const getRecruitChannelSimpleList = async () => {
  return await request.get<HrmRecruitChannelVO[]>({
    url: '/hrm/recruit/channel/simple-list'
  })
}

// 新增招聘渠道
export const createRecruitChannel = async (data: HrmRecruitChannelVO) => {
  return await request.post<number>({ url: '/hrm/recruit/channel/create', data })
}

// 修改招聘渠道
export const updateRecruitChannel = async (data: HrmRecruitChannelVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/channel/update', data })
}

// 修改招聘渠道状态
export const updateRecruitChannelStatus = async (data: HrmRecruitChannelStatusReqVO) => {
  return await request.put<boolean>({ url: '/hrm/recruit/channel/update-status', data })
}

// 删除招聘渠道
export const deleteRecruitChannel = async (data: HrmRecruitChannelDeleteReqVO) => {
  return await request.delete<boolean>({ url: '/hrm/recruit/channel/delete', data })
}
