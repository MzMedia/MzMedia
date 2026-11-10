import request from '@/config/axios'
import type { SalaryOptionValueVO } from '../config/option'

// 员工薪资信息 VO
export interface SalaryEmployeeInfoVO {
  id?: number // 员工薪资信息编号
  employeeId?: number // 员工编号
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  mobile?: string // 手机号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 岗位名称
  entryStatus?: number // 入职状态
  status?: number // 员工状态
  entryTime?: Date // 入职时间
  regularTime?: Date // 转正时间
  changeReason?: number // 调薪原因
  effectTime?: number // 调薪生效时间
  changeType?: number // 调薪类型
  probationSalary?: number // 试用期工资
  regularSalary?: number // 转正工资
  remark?: string // 备注
  salaryOptions?: SalaryOptionValueVO[] // 转正工资项
  probationSalaryOptions?: SalaryOptionValueVO[] // 试用期工资项
  createTime?: Date // 创建时间
}

// 员工薪资信息修改 Request VO
export interface SalaryEmployeeInfoUpdateReqVO {
  id?: number // 调薪记录编号
  employeeId?: number // 员工编号
  recordType?: number // 记录类型
  changeReason?: number // 调薪原因
  effectTime?: number // 生效时间
  remark?: string // 备注
  salaryOptions?: SalaryOptionValueVO[] // 转正工资项
  probationSalaryOptions?: SalaryOptionValueVO[] // 试用期工资项
}

// 员工薪资信息批量更新 Request VO
export interface SalaryEmployeeInfoUpdateListReqVO {
  employeeIds: number[] // 员工编号数组
  deptIds: number[] // 部门编号数组
  type: number // 调整方式
  changeReason?: number // 调薪原因
  effectTime?: number // 生效时间
  remark?: string // 备注
  salaryOptions: SalaryOptionValueVO[] // 调整工资项
}

// 员工薪资信息批量更新响应 VO
export interface SalaryEmployeeInfoUpdateListRespVO {
  successEmployeeIds: number[] // 成功员工编号数组
  failureEmployeeReasons: Record<number, string> // 失败员工及原因
}

// 员工薪资导入结果 VO
export interface SalaryEmployeeInfoImportRespVO {
  successJobNumbers: string[] // 导入成功的工号
  failureJobNumbers: Record<string, string> // 导入失败的工号及原因
}

// 员工薪资导入响应
export interface SalaryEmployeeInfoImportResponse {
  code: number // 响应码
  msg: string // 响应消息
  data: SalaryEmployeeInfoImportRespVO // 导入结果
}

// 员工状态数量 VO
export interface SalaryEmployeeStatusCountVO {
  status: number // 状态页签
  count: number // 数量
}

// 获得员工薪资信息分页
export const getSalaryEmployeeInfoPage = async (params: PageParam) => {
  return await request.get<PageResult<SalaryEmployeeInfoVO[]>>({
    url: '/hrm/salary/employee-info/page',
    params
  })
}

// 获得员工薪资信息状态数量
export const getSalaryEmployeeInfoStatusCount = async (params: PageParam) => {
  return await request.get<SalaryEmployeeStatusCountVO[]>({
    url: '/hrm/salary/employee-info/status-count',
    params
  })
}

// 获得员工薪资信息
export const getSalaryEmployeeInfo = async (employeeId: number) => {
  return await request.get<SalaryEmployeeInfoVO>({
    url: '/hrm/salary/employee-info/get',
    params: { employeeId }
  })
}

// 获得最早调薪生效日期
export const getSalaryAdjustmentMinEffectDate = async () => {
  return await request.get<string>({
    url: '/hrm/salary/employee-info/get-adjustment-min-effect-date'
  })
}

// 修改员工薪资信息
export const updateSalaryEmployeeInfo = async (data: SalaryEmployeeInfoUpdateReqVO) => {
  return await request.put<number>({ url: '/hrm/salary/employee-info/update', data })
}

// 批量更新员工薪资信息
export const updateSalaryEmployeeInfoList = async (data: SalaryEmployeeInfoUpdateListReqVO) => {
  return await request.put<SalaryEmployeeInfoUpdateListRespVO>({
    url: '/hrm/salary/employee-info/update-list',
    data
  })
}

// 下载定薪导入模板
export const getFixSalaryImportTemplate = async () => {
  return await request.download({ url: '/hrm/salary/employee-info/get-fix-import-template' })
}

// 下载调薪导入模板
export const getChangeSalaryImportTemplate = async () => {
  return await request.download({ url: '/hrm/salary/employee-info/get-change-import-template' })
}
