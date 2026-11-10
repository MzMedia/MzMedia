import request from '@/config/axios'

// HRM 员工异动记录 VO
export interface HrmEmployeeChangeRecordVO {
  id?: number // 异动记录编号
  employeeId?: number // 员工编号
  type?: number // 异动类型
  reason?: number // 异动原因
  oldDeptId?: number // 原部门编号
  oldDeptName?: string // 原部门名称
  newDeptId?: number // 新部门编号
  newDeptName?: string // 新部门名称
  oldPostName?: string // 原职位
  newPostName?: string // 新职位
  oldPostLevel?: string // 原岗位职级
  newPostLevel?: string // 新岗位职级
  oldWorkAddress?: string // 原工作地点
  newWorkAddress?: string // 新工作地点
  oldLeaderEmployeeId?: number // 原直属上级员工编号
  oldLeaderEmployeeName?: string // 原直属上级员工姓名
  newLeaderEmployeeId?: number // 新直属上级员工编号
  newLeaderEmployeeName?: string // 新直属上级员工姓名
  probation?: number // 试用期，单位月
  effectTime?: number // 生效日期
  remark?: string // 备注
  createTime?: Date // 创建时间
}

// 查询员工异动记录列表
export const getEmployeeChangeRecordList = async (employeeId: number) => {
  return await request.get<HrmEmployeeChangeRecordVO[]>({
    url: '/hrm/employee/change-record/list',
    params: { employeeId }
  })
}
