import request from '@/config/axios'
import type { SalaryOptionValueVO } from '../../config/option'

// 员工月度工资 VO
export interface SalaryMonthEmployeeRecordVO {
  id?: number // 员工月度工资记录编号
  monthRecordId?: number // 月度工资表编号
  employeeId?: number // 员工编号
  year?: number // 年份
  month?: number // 月份
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门
  postName?: string // 职位名称
  actualWorkDay?: number // 实际出勤天数
  needWorkDay?: number // 应出勤天数
  expectedPaySalary?: number // 应发工资
  taxableSalary?: number // 应税工资
  personalTax?: number // 个人所得税
  realPaySalary?: number // 实发工资
  performanceCoefficient?: number // 绩效系数
  optionValues?: SalaryOptionValueVO[] // 工资项值
}

// 绩效系数查询 Request VO
export interface PerformanceCoefficientReqVO {
  year: number // 年份
  month: number // 月份
  employeeIds?: number[] // 员工编号数组
}

// 批量修改员工月度工资
export const updateSalaryMonthEmployeeRecordList = async (data: SalaryMonthEmployeeRecordVO[]) => {
  return await request.put<boolean>({
    url: '/hrm/salary/month-employee-record/update-list',
    data
  })
}

// 获得员工月度工资分页
export const getSalaryMonthEmployeeRecordPage = async (params: PageParam) => {
  return await request.get<PageResult<SalaryMonthEmployeeRecordVO[]>>({
    url: '/hrm/salary/month-employee-record/page',
    params
  })
}

// 获得指定员工的月度工资分页
export const getSalaryEmployeeMonthRecordPage = async (params: PageParam) => {
  return await request.get<PageResult<SalaryMonthEmployeeRecordVO[]>>({
    url: '/hrm/salary/month-employee-record/employee-page',
    params
  })
}

// 获得员工月度工资列表
export const getSalaryMonthEmployeeRecordList = async (params: {
  monthRecordId: number
  employeeId?: number
  employeeIds?: number[]
  employeeName?: string
  jobNumber?: string
  deptId?: number
  employeeChangeType?: number
  salarySlipSent?: boolean
}) => {
  return await request.get<SalaryMonthEmployeeRecordVO[]>({
    url: '/hrm/salary/month-employee-record/list',
    params
  })
}

// 获得月度工资员工变动数量
export const getSalaryMonthEmployeeChangeCount = async (params: PageParam) => {
  return await request.get<Record<number, number>>({
    url: '/hrm/salary/month-employee-record/change-count',
    params
  })
}

// 获得绩效系数列表
export const getSalaryPerformanceCoefficients = async (data: PerformanceCoefficientReqVO) => {
  return await request.post<Record<number, number>>({
    url: '/hrm/salary/month-employee-record/performance-coefficients',
    data
  })
}
