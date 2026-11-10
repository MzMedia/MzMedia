import request from '@/config/axios'

// HRM 员工档案 VO
export interface HrmEmployeeVO {
  id?: number // 员工编号
  name: string // 员工姓名
  jobNumber?: string // 工号
  userId?: number // 后台用户编号
  userNickname?: string // 后台用户昵称
  mobile?: string // 手机号
  country?: string // 国家或地区
  nation?: string // 民族
  idType?: number // 证件类型
  idNumber?: string // 证件号码
  sex?: number // 性别
  email?: string // 邮箱
  nativePlace?: string // 籍贯
  birthday?: number // 出生时间
  age?: number // 年龄
  address?: string // 户籍地址
  highestEducation?: number // 最高学历
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  leaderEmployeeId?: number // 直属上级员工编号
  leaderEmployeeName?: string // 直属上级员工姓名
  entryStatus?: number // 入职状态
  status?: number // 员工状态
  type?: number // 聘用形式
  entryTime?: number // 入职时间
  probation?: number // 试用期，单位月
  regularTime?: number // 转正时间
  leaveTime?: number // 离职时间
  postName?: string // 职位名称
  postLevel?: string // 岗位职级
  workCity?: string // 工作城市
  workAddress?: string // 工作地点
  workDetailAddress?: string // 工作详细地址
  channelId?: number // 招聘渠道编号
  channelName?: string // 招聘渠道名称
  companyAgeStartTime?: number // 司龄开始时间
  companyAge?: number // 司龄，单位年
  candidateId?: number // 招聘候选人编号
  salaryCardNumber?: string // 银行卡号
  salaryCardAreaId?: number // 开户地区编号
  salaryCardAreaName?: string // 开户地区名称
  salaryCardBankName?: string // 银行名称
  salaryCardBankBranchName?: string // 开户支行名称
  socialSecurityNumber?: string // 个人社保账号
  accumulationFundNumber?: string // 个人公积金账号
  remark?: string // 备注
  createTime?: Date // 创建时间
}

// HRM 员工状态数量 VO
export interface HrmEmployeeStatusCountVO {
  status: number // 状态页签
  count: number // 数量
}

// HRM 员工部门统计 VO
export interface HrmEmployeeDeptStatisticsVO {
  deptId: number // 部门编号
  activeCount: number // 在职员工人数
  fullTimeCount: number // 全职员工人数
  nonFullTimeCount: number // 非全职员工人数
}

// HRM 员工再入职 Request VO
export interface HrmEmployeeRehireReqVO extends HrmEmployeeVO {
  employeeId?: number // 员工编号
}

// HRM 员工转正 Request VO
export interface HrmEmployeeRegularReqVO {
  employeeId?: number // 员工编号
  reason?: number // 异动原因
  newDeptId?: number // 新部门编号；未填写表示不变
  newPostName?: string // 新岗位名称；未填写表示不变
  newPostLevel?: string // 新职级；未填写表示不变
  newWorkAddress?: string // 新工作地点；未填写表示不变
  newLeaderEmployeeId?: number // 新直属上级员工编号；未填写表示不变
  effectTime?: number // 生效时间
  remark?: string // 备注
}

// HRM 员工调岗 Request VO
export interface HrmEmployeeTransferReqVO {
  employeeId?: number // 员工编号
  reason?: number // 异动原因
  newDeptId?: number // 新部门编号
  newPostName?: string // 新职位
  newPostLevel?: string // 新岗位职级
  newWorkAddress?: string // 新工作地点
  newLeaderEmployeeId?: number // 新直属上级员工编号
  effectTime?: number // 生效时间
  remark?: string // 备注
}

// HRM 员工晋升 Request VO
export interface HrmEmployeePromoteReqVO {
  employeeId?: number // 员工编号
  reason?: number // 异动原因
  newDeptId?: number // 新部门编号
  newPostName?: string // 新职位
  newPostLevel?: string // 新岗位职级
  newWorkAddress?: string // 新工作地点
  newLeaderEmployeeId?: number // 新直属上级员工编号
  effectTime?: number // 生效时间
  remark?: string // 备注
}

// HRM 员工降级 Request VO
export interface HrmEmployeeDemoteReqVO {
  employeeId?: number // 员工编号
  reason?: number // 异动原因
  newDeptId?: number // 新部门编号
  newPostName?: string // 新职位
  newPostLevel?: string // 新岗位职级
  newWorkAddress?: string // 新工作地点
  newLeaderEmployeeId?: number // 新直属上级员工编号
  effectTime?: number // 生效时间
  remark?: string // 备注
}

// HRM 员工转为全职 Request VO
export interface HrmEmployeeConvertToFullTimeReqVO {
  employeeId?: number // 员工编号
  reason?: number // 异动原因
  probation?: number // 试用期，单位月
  newDeptId?: number // 新部门编号；未填写表示不变
  newPostName?: string // 新岗位名称；未填写表示不变
  newPostLevel?: string // 新职级；未填写表示不变
  newWorkAddress?: string // 新工作地点；未填写表示不变
  newLeaderEmployeeId?: number // 新直属上级员工编号；未填写表示不变
  effectTime?: number // 生效时间
  remark?: string // 备注
}

// HRM 从后台用户批量创建员工 Request VO
export interface HrmEmployeeCreateFromUserReqVO {
  userId: number // 后台用户编号
  jobNumber: string // 工号
  mobile: string // 员工手机号
  deptId?: number // 部门编号
  leaderEmployeeId?: number // 直属上级员工编号
  type: number // 聘用形式
  status?: number // 非正式员工状态
  entryTime: number // 入职时间
  probation?: number // 试用期，单位月
  postName?: string // 职位名称
  postLevel?: string // 岗位职级
  workCity?: string // 工作城市
  workAddress?: string // 工作地点
  remark?: string // 备注
}

// HRM 员工通知发送结果 Response VO
export interface HrmEmployeeNotifyRespVO {
  successCount: number // 发送成功数量
  skippedCount: number // 无后台账号跳过数量
  failureCount: number // 发送失败数量
}

// HRM 员工导入结果 VO
export interface HrmEmployeeImportRespVO {
  createJobNumbers: string[] // 新增成功的工号
  updateJobNumbers: string[] // 更新成功的工号
  skipJobNumbers: string[] // 跳过的工号
  failureJobNumbers: Record<string, string> // 导入失败的工号及原因
}

// HRM 员工导入响应
export interface HrmEmployeeImportResponse {
  code: number // 响应码
  msg: string // 响应消息
  data: HrmEmployeeImportRespVO // 导入结果
}

// HRM 员工离职 Request VO
export interface HrmEmployeeQuitReqVO {
  employeeId?: number // 员工编号
  planQuitTime?: number // 计划离职时间
  applyQuitTime?: number // 申请离职时间
  salarySettlementTime?: number // 薪资结算时间
  type?: number // 离职类型
  reason?: number // 离职原因
  remark?: string // 备注
}

// HRM 员工取消离职 Request VO
export interface HrmEmployeeCancelQuitReqVO {
  employeeId: number // 员工编号
  reason: string // 取消原因
}

// 查询员工档案分页
export const getEmployeePage = async (params: PageParam) => {
  return await request.get<PageResult<HrmEmployeeVO[]>>({ url: '/hrm/employee/page', params })
}

// 查询员工档案详情
export const getEmployee = async (id: number) => {
  return await request.get<HrmEmployeeVO>({ url: '/hrm/employee/get?id=' + id })
}

// 查询指定员工列表
export const getEmployeeList = async (ids: number[]) => {
  return await request.get<HrmEmployeeVO[]>({
    url: '/hrm/employee/list',
    params: { ids: ids.join(',') }
  })
}

// 查询员工精简分页
export const getEmployeeSimplePage = async (params: PageParam) => {
  return await request.get<PageResult<HrmEmployeeVO[]>>({
    url: '/hrm/employee/simple-page',
    params
  })
}

// 查询指定员工精简列表
export const getEmployeeSimpleList = async (ids: number[]) => {
  return await request.get<HrmEmployeeVO[]>({
    url: '/hrm/employee/simple-list',
    params: { ids: ids.join(',') }
  })
}

// 查询员工状态数量
export const getEmployeeStatusCount = async (params: PageParam) => {
  return await request.get<HrmEmployeeStatusCountVO[]>({
    url: '/hrm/employee/status-count',
    params
  })
}

// 查询员工部门统计
export const getEmployeeDeptStatistics = async () => {
  return await request.get<HrmEmployeeDeptStatisticsVO[]>({
    url: '/hrm/employee/dept-statistics'
  })
}

// 新增员工档案
export const createEmployee = async (data: HrmEmployeeVO) => {
  return await request.post<number>({ url: '/hrm/employee/create', data })
}

// 从未建档后台用户批量创建员工档案
export const createEmployeeList = async (data: HrmEmployeeCreateFromUserReqVO[]) => {
  return await request.post<number[]>({ url: '/hrm/employee/create-list', data })
}

// 查询已经建立员工档案的后台用户编号
export const getBoundUserIdList = async () => {
  return await request.get<number[]>({ url: '/hrm/employee/bound-user-id-list' })
}

// 发送填写员工档案通知
export const sendEmployeeProfileFillMessage = async (employeeIds: number[]) => {
  return await request.post<HrmEmployeeNotifyRespVO>({
    url: '/hrm/employee/send-profile-fill-message',
    params: { ids: employeeIds.join(',') }
  })
}

// 修改员工档案
export const updateEmployee = async (data: HrmEmployeeVO) => {
  return await request.put<boolean>({ url: '/hrm/employee/update', data })
}

// 确认员工入职
export const confirmEmployeeEntry = async (data: HrmEmployeeVO) => {
  return await request.put<boolean>({
    url: '/hrm/employee/confirm-entry',
    data
  })
}

// 办理员工再入职
export const rehireEmployee = async (data: HrmEmployeeRehireReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/rehire', data })
}

// 办理员工转正
export const regularEmployee = async (data: HrmEmployeeRegularReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/regular', data })
}

// 办理员工调岗
export const transferEmployee = async (data: HrmEmployeeTransferReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/transfer', data })
}

// 办理员工晋升
export const promoteEmployee = async (data: HrmEmployeePromoteReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/promote', data })
}

// 办理员工降级
export const demoteEmployee = async (data: HrmEmployeeDemoteReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/demote', data })
}

// 办理员工转为全职
export const convertEmployeeToFullTime = async (data: HrmEmployeeConvertToFullTimeReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/convert-to-full-time', data })
}

// 办理员工离职
export const quitEmployee = async (data: HrmEmployeeQuitReqVO) => {
  return await request.post<boolean>({ url: '/hrm/employee/quit', data })
}

// 取消员工离职
export const cancelEmployeeQuit = async (data: HrmEmployeeCancelQuitReqVO) => {
  return await request.put<boolean>({
    url: '/hrm/employee/cancel-quit',
    data
  })
}

// 删除员工档案
export const deleteEmployee = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/employee/delete?id=' + id })
}

// 批量删除员工档案
export const deleteEmployeeList = async (ids: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/delete-list',
    params: { ids: ids.join(',') }
  })
}

// 导出员工档案
export const exportEmployee = async (params: PageParam) => {
  return await request.download({ url: '/hrm/employee/export-excel', params })
}

// 下载员工档案导入模板
export const importEmployeeTemplate = async () => {
  return await request.download({ url: '/hrm/employee/get-import-template' })
}
