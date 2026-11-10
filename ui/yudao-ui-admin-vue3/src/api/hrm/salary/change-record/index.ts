import request from '@/config/axios'
import type { SalaryOptionValueVO } from '../config/option'

// 员工调薪记录 VO
export interface SalaryChangeRecordVO {
  id?: number // 调薪记录编号
  employeeId?: number // 员工编号
  recordType?: number // 记录类型
  changeReason?: number // 调薪原因
  effectTime?: number // 生效时间
  beforeTotal?: number // 调整前转正工资
  afterTotal?: number // 调整后转正工资
  probationBeforeTotal?: number // 调整前试用期工资
  probationAfterTotal?: number // 调整后试用期工资
  status?: number // 状态
  remark?: string // 备注
  salaryOptions?: SalaryOptionValueVO[] // 转正工资项
  probationSalaryOptions?: SalaryOptionValueVO[] // 试用期工资项
  createTime?: Date // 创建时间
}

// 获得员工调薪记录
export const getSalaryChangeRecord = async (id: number) => {
  return await request.get<SalaryChangeRecordVO>({
    url: '/hrm/salary/change-record/get',
    params: { id }
  })
}

// 获得员工调薪记录列表
export const getSalaryChangeRecordList = async (employeeId: number) => {
  return await request.get<SalaryChangeRecordVO[]>({
    url: '/hrm/salary/change-record/list',
    params: { employeeId }
  })
}

// 取消员工调薪记录
export const cancelSalaryChangeRecord = async (id: number) => {
  return await request.put<boolean>({
    url: '/hrm/salary/change-record/cancel',
    params: { id }
  })
}

// 删除员工调薪记录
export const deleteSalaryChangeRecord = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/salary/change-record/delete',
    params: { id }
  })
}
