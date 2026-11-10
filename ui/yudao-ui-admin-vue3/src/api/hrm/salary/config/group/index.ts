import request from '@/config/axios'

// 薪资组 VO
export interface SalaryGroupVO {
  id?: number // 薪资组编号
  name: string // 薪资组名称
  salaryStandard?: number // 月计薪标准
  changeRule?: string // 转正、调薪月规则
  taxRuleId?: number // 计税规则编号
  taxRuleName?: string // 计税规则名称
  deptIds?: number[] // 适用部门编号列表
  deptNames?: string[] // 适用部门名称列表
  employeeIds?: number[] // 适用员工编号列表
  employeeNames?: string[] // 适用员工名称列表
  createTime?: Date // 创建时间
}

// 新增薪资组
export const createSalaryGroup = async (data: SalaryGroupVO) => {
  return await request.post<number>({ url: '/hrm/salary/group/create', data })
}

// 修改薪资组
export const updateSalaryGroup = async (data: SalaryGroupVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/group/update', data })
}

// 删除薪资组
export const deleteSalaryGroup = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/salary/group/delete?id=' + id })
}

// 获得薪资组详情
export const getSalaryGroup = async (id: number) => {
  return await request.get<SalaryGroupVO>({ url: '/hrm/salary/group/get?id=' + id })
}

// 获得薪资组分页
export const getSalaryGroupPage = async (params: PageParam) => {
  return await request.get<PageResult<SalaryGroupVO[]>>({
    url: '/hrm/salary/group/page',
    params
  })
}

// 获得薪资组精简列表
export const getSalaryGroupSimpleList = async () => {
  return await request.get<SalaryGroupVO[]>({ url: '/hrm/salary/group/simple-list' })
}
