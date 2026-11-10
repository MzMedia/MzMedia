import request from '@/config/axios'

// HRM 考勤打卡 VO
export interface HrmAttendanceClockVO {
  id?: number // 打卡记录编号
  employeeId?: number // 打卡员工编号
  clockTime?: Date | number // 打卡时间
  type: number // 打卡类型
  attendanceTime?: Date | number // 应打卡时间
  sourceType?: number // 打卡来源
  status?: number // 打卡状态
  stage?: number // 打卡阶段
  address?: string // 打卡地址
  longitude?: number // 经度
  latitude?: number // 纬度
  ssid?: string // WiFi 名称
  mac?: string // WiFi MAC 地址
  remark?: string // 备注
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  createTime?: Date // 创建时间
}

// HRM 员工实际班次 VO
export interface HrmAttendanceClockShiftVO {
  startTime: Date // 上班时间
  endTime: Date // 下班时间
  clockInStartTime: Date // 上班打卡开始时间
  clockInEndTime: Date // 上班打卡结束时间
  clockOutStartTime: Date // 下班打卡开始时间
  clockOutEndTime: Date // 下班打卡结束时间
}

// 获得考勤打卡分页
export const getAttendanceClockPage = async (params: PageParam) => {
  return await request.get<PageResult<HrmAttendanceClockVO[]>>({
    url: '/hrm/attendance/clock/page',
    params
  })
}

// 获得考勤打卡详情
export const getAttendanceClock = async (id: number) => {
  return await request.get<HrmAttendanceClockVO>({
    url: '/hrm/attendance/clock/get?id=' + id
  })
}

// 获得员工实际班次和允许打卡时间
export const getAttendanceClockShift = async (params: {
  employeeId: number
  attendanceTime: string
}) => {
  return await request.get<HrmAttendanceClockShiftVO | undefined>({
    url: '/hrm/attendance/clock/get-shift',
    params
  })
}

// 导出考勤打卡
export const exportAttendanceClock = async (params: PageParam) => {
  return await request.download({ url: '/hrm/attendance/clock/export-excel', params })
}

// 新增考勤打卡
export const createAttendanceClock = async (data: HrmAttendanceClockVO) => {
  return await request.post<number>({ url: '/hrm/attendance/clock/create', data })
}

// 修改考勤打卡
export const updateAttendanceClock = async (data: HrmAttendanceClockVO) => {
  return await request.put<boolean>({ url: '/hrm/attendance/clock/update', data })
}

// 删除考勤打卡
export const deleteAttendanceClock = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/attendance/clock/delete?id=' + id })
}

// 批量删除考勤打卡
export const deleteAttendanceClockList = async (ids: number[]) => {
  return await request.delete<boolean>({
    url: '/hrm/attendance/clock/delete-list',
    params: { ids: ids.join(',') }
  })
}
