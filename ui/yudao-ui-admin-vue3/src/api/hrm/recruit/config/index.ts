import request from '@/config/axios'

// 保存招聘淘汰原因
export const saveRecruitEliminateReason = async (reasons: string[]) => {
  return await request.post<boolean>({
    url: '/hrm/recruit/config/eliminate-reason/save',
    data: { reasons }
  })
}

// 查询招聘淘汰原因列表
export const getRecruitEliminateReasonList = async () => {
  return await request.get<string[]>({ url: '/hrm/recruit/config/eliminate-reason/list' })
}
