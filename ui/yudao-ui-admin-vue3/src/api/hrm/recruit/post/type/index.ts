import request from '@/config/axios'

// 招聘职位类型 VO
export interface HrmRecruitPostTypeVO {
  id: number // 职位类型编号
  name: string // 类型名称
  parentId: number // 父类型编号
  sort?: number // 排序
  status?: number // 状态
  createTime?: Date // 创建时间
}

// 查询招聘职位类型列表
export const getRecruitPostTypeList = async (params?: { status?: number }) => {
  return await request.get<HrmRecruitPostTypeVO[]>({ url: '/hrm/recruit/post-type/list', params })
}
