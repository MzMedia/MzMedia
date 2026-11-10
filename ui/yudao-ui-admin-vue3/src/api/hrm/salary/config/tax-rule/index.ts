import request from '@/config/axios'

// 计税规则 VO
export interface SalaryTaxRuleVO {
  id?: number // 计税规则编号
  name: string // 计税规则名称
  type?: number // 计税类型
  taxEnabled?: boolean // 是否计税
  threshold?: number // 起征阈值
  decimalScale?: number // 小数位数
  cycleType?: number // 计税周期类型
  usedGroupCount?: number // 使用该规则的薪资组数量
  createTime?: Date // 创建时间
}

// 创建计税规则
export const createSalaryTaxRule = async (data: SalaryTaxRuleVO) => {
  return await request.post<number>({ url: '/hrm/salary/tax-rule/create', data })
}

// 修改计税规则
export const updateSalaryTaxRule = async (data: SalaryTaxRuleVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/tax-rule/update', data })
}

// 删除计税规则
export const deleteSalaryTaxRule = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/salary/tax-rule/delete?id=' + id })
}

// 获得计税规则
export const getSalaryTaxRule = async (id: number) => {
  return await request.get<SalaryTaxRuleVO>({ url: '/hrm/salary/tax-rule/get?id=' + id })
}

// 获得计税规则列表
export const getSalaryTaxRuleList = async () => {
  return await request.get<SalaryTaxRuleVO[]>({ url: '/hrm/salary/tax-rule/list' })
}
