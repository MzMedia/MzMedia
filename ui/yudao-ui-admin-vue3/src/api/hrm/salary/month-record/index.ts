import request from '@/config/axios'
import type { SalaryOptionValueVO, SalaryOptionVO } from '../config/option'

// 月度工资表 VO
export interface SalaryMonthRecordVO {
  id?: number // 月度工资表编号
  title?: string // 标题
  year?: number // 年份
  month?: number // 月份
  employeeCount?: number // 计薪人数
  startTime?: string // 计薪开始时间
  endTime?: string // 计薪结束时间
  expectedPaySalary?: number // 应发工资
  personalInsuranceAmount?: number // 个人社保
  personalProvidentFundAmount?: number // 个人公积金
  personalTax?: number // 个人所得税
  realPaySalary?: number // 实发工资
  corporateInsuranceAmount?: number // 公司社保
  corporateProvidentFundAmount?: number // 公司公积金
  status?: number // 工资表状态
  optionHeaders?: SalaryOptionVO[] // 工资项表头
  createTime?: Date // 创建时间
}

// 薪资核算就绪员工 VO
export interface SalaryPayrollReadinessEmployeeVO {
  employeeId?: number // 员工编号
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  entryStatus?: number // 入职状态
  status?: number // 员工状态
  entryTime?: Date // 入职时间
}

// 薪资核算就绪状态 VO
export interface SalaryPayrollReadinessVO {
  monthRecordId?: number // 月度工资表编号
  title?: string // 工资表标题
  year?: number // 年份
  month?: number // 月份
  startTime?: string // 计薪开始时间
  endTime?: string // 计薪结束时间
  socialSecurityYearMonth?: string // 社保年月
  payrollEmployeeCount?: number // 计薪人数
  salaryEmployeeCount?: number // 已定薪人数
  noSalaryEmployeeCount?: number // 未定薪人数
  noSalaryGroupEmployeeCount?: number // 未分配薪资组人数
  changeEmployeeCount?: number // 异动人数
  changeTypeCountMap?: Record<number, number> // 异动类型数量
  noSalaryEmployees?: SalaryPayrollReadinessEmployeeVO[] // 未定薪员工
  noSalaryGroupEmployees?: SalaryPayrollReadinessEmployeeVO[] // 未分配薪资组员工
}

// 创建下月工资表
export const createNextSalaryMonthRecord = async () => {
  return await request.post<number>({ url: '/hrm/salary/month-record/create-next' })
}

// 核算月度工资表
export const computeSalaryMonthRecord = async (id: number) => {
  return await request.post<boolean>({ url: '/hrm/salary/month-record/compute?id=' + id })
}

// 导入并核算月度工资表
export const computeSalaryMonthRecordWithImport = async (data: FormData) => {
  return await request.upload<boolean>({ url: '/hrm/salary/month-record/compute-import', data })
}

// 删除月度工资表
export const deleteSalaryMonthRecord = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/salary/month-record/delete?id=' + id })
}

// 获得月度工资表分页
export const getSalaryMonthRecordPage = async (params: PageParam) => {
  return await request.get<PageResult<SalaryMonthRecordVO[]>>({
    url: '/hrm/salary/month-record/page',
    params
  })
}

// 获得月度工资表详情
export const getSalaryMonthRecord = async (id: number) => {
  return await request.get<SalaryMonthRecordVO>({ url: '/hrm/salary/month-record/get?id=' + id })
}

// 获得最近月度工资表
export const getLastSalaryMonthRecord = async () => {
  return await request.get<SalaryMonthRecordVO>({ url: '/hrm/salary/month-record/last' })
}

// 获得薪资核算就绪状态
export const getSalaryPayrollReadiness = async (monthRecordId?: number) => {
  return await request.get<SalaryPayrollReadinessVO>({
    url: '/hrm/salary/month-record/payroll-readiness',
    params: { monthRecordId }
  })
}

// 导入薪资考勤数据
export const getSalaryAttendanceImportTemplate = async (monthRecordId?: number) => {
  return await request.download({
    url: '/hrm/salary/month-record/get-attendance-import-template',
    params: { monthRecordId }
  })
}

// 导入累计个税数据
export const getSalaryCumulativeTaxImportTemplate = async (monthRecordId?: number) => {
  return await request.download({
    url: '/hrm/salary/month-record/get-cumulative-tax-import-template',
    params: { monthRecordId }
  })
}

// 导入专项附加扣除数据
export const getSalaryAdditionalDeductionImportTemplate = async (monthRecordId?: number) => {
  return await request.download({
    url: '/hrm/salary/month-record/get-additional-deduction-import-template',
    params: { monthRecordId }
  })
}

// 获得月度工资薪资项汇总
export const getSalaryMonthOptionSummary = async (params: PageParam) => {
  return await request.get<SalaryOptionValueVO[]>({
    url: '/hrm/salary/month-record/option-summary',
    params
  })
}
