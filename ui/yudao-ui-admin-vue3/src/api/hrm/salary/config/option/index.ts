import request from '@/config/axios'

// 工资表薪资项 VO
export interface SalaryOptionVO {
  id: number // 薪资项编号
  code: number // 薪资项编码
  parentCode: number // 父薪资项编码
  name: string // 薪资项名称
  remark?: string // 备注
  systemFlag: boolean // 是否系统默认项
  type: number // 薪资项类型
  taxEnabled: boolean // 是否计税
  visible: boolean // 是否显示
  calculateEnabled: boolean // 是否参与计算
  enabled: boolean // 是否启用
  templateId?: number // 标准薪资项目录编号
  children?: SalaryOptionVO[] // 子薪资项
  createTime: Date // 创建时间
}

// 工资表薪资项新增 VO
export interface SalaryOptionSaveReqVO {
  parentCode?: number // 父薪资项编码
  name: string // 薪资项名称
  remark?: string // 备注
}

// 薪资项值 VO
export interface SalaryOptionValueVO {
  code?: number // 薪资项编码
  name?: string // 薪资项名称
  value?: number // 薪资项金额
}

// 新增工资表薪资项
export const createSalaryOption = async (data: SalaryOptionSaveReqVO) => {
  return await request.post<number>({ url: '/hrm/salary/option/create', data })
}

// 更新工资表薪资项启用状态
export const updateSalaryOptionEnabled = async (id: number, enabled: boolean) => {
  return await request.put<boolean>({
    url: '/hrm/salary/option/update-enabled',
    data: { id, enabled }
  })
}

// 更新工资表薪资项显示状态
export const updateSalaryOptionVisible = async (id: number, visible: boolean) => {
  return await request.put<boolean>({
    url: '/hrm/salary/option/update-visible',
    data: { id, visible }
  })
}

// 删除工资表薪资项
export const deleteSalaryOption = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/salary/option/delete?id=' + id })
}

// 同步标准工资表薪资项
export const syncSalaryOption = async () => {
  return await request.put<boolean>({ url: '/hrm/salary/option/sync' })
}

// 获得工资表薪资项列表
export const getSalaryOptionList = async () => {
  return await request.get<SalaryOptionVO[]>({ url: '/hrm/salary/option/list' })
}

// 获得工资表薪资项精简列表
export const getSalaryOptionSimpleList = async (adjustable?: boolean) => {
  return await request.get<SalaryOptionVO[]>({
    url: '/hrm/salary/option/simple-list',
    params: { adjustable }
  })
}
