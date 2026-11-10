import request from '@/config/axios'

// 工资条薪资项 VO
export interface SalarySlipOptionVO {
  name?: string // 工资项名称
  type?: number // 工资项类型
  code?: number // 工资项编码
  value?: number // 工资项金额
  remark?: string // 备注
  sort?: number // 排序
  children?: SalarySlipOptionVO[] // 子工资条项
}

// 工资条 VO
export interface SalarySlipVO {
  id?: number // 工资条编号
  sendRecordId?: number // 工资条发放记录编号
  monthEmployeeRecordId?: number // 员工月度工资记录编号
  employeeId?: number // 员工编号
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  mobile?: string // 手机号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 岗位名称
  year?: number // 年份
  month?: number // 月份
  readStatus?: number // 查看状态
  realPaySalary?: number // 实发工资
  remark?: string // 备注
  options?: SalarySlipOptionVO[] // 工资条项
  createTime?: Date // 创建时间
}

// 工资条备注修改 Request VO
export interface SalarySlipRemarkReqVO {
  id: number // 工资条编号
  remark?: string // 备注
}

// 获得工资条分页
export const getSalarySlipPage = async (params: PageParam) => {
  return await request.get<PageResult<SalarySlipVO[]>>({ url: '/hrm/salary/slip/page', params })
}

// 获得工资条详情
export const getSalarySlip = async (id: number) => {
  return await request.get<SalarySlipVO>({ url: '/hrm/salary/slip/get?id=' + id })
}

// 修改工资条备注
export const updateSalarySlipRemark = async (data: SalarySlipRemarkReqVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/slip/remark', data })
}
