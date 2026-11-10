import request from '@/config/axios'

// 工资条模板薪资项 VO
export interface SalarySlipTemplateOptionVO {
  name?: string // 工资项名称
  type?: number // 工资项类型
  code?: number // 工资项编码
  remark?: string // 备注
  parentCode?: number // 父工资项编码
  hidden?: boolean // 是否隐藏
  sort?: number // 排序
}

// 工资条模板 VO
export interface SalarySlipTemplateVO {
  id?: number // 工资条模板编号
  name: string // 模板名称
  hideEmpty?: boolean // 是否隐藏空工资项
  defaultStatus?: boolean // 是否默认模板
  options?: SalarySlipTemplateOptionVO[] // 模板工资项
  createTime?: Date // 创建时间
}

// 创建工资条模板
export const createSalarySlipTemplate = async (data: SalarySlipTemplateVO) => {
  return await request.post<number>({ url: '/hrm/salary/slip-template/create', data })
}

// 更新工资条模板
export const updateSalarySlipTemplate = async (data: SalarySlipTemplateVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/slip-template/update', data })
}

// 删除工资条模板
export const deleteSalarySlipTemplate = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/salary/slip-template/delete?id=' + id })
}

// 获得工资条模板详情
export const getSalarySlipTemplate = async (id: number) => {
  return await request.get<SalarySlipTemplateVO>({ url: '/hrm/salary/slip-template/get?id=' + id })
}

// 获得工资条模板列表
export const getSalarySlipTemplateList = async () => {
  return await request.get<SalarySlipTemplateVO[]>({ url: '/hrm/salary/slip-template/list' })
}
