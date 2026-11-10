import request from '@/config/axios'

// HRM 考勤地点配置
export interface HrmAttendancePoint {
  name: string // 地点名称
  address?: string // 定位地址
  latitude?: number // 纬度
  longitude?: number // 经度
  radius?: number // 有效打卡半径（米）
}

// HRM 考勤 WiFi 配置
export interface HrmAttendanceWifi {
  ssid: string // WiFi 名称
  mac?: string // MAC 地址
}

// HRM 考勤班次配置
export interface HrmAttendanceShift {
  weeks: number[] // 工作日数组
  startTime: string // 上班时间
  endTime: string // 下班时间
  clockInStartTime: string // 上班打卡开始时间
  clockInEndTime: string // 上班打卡结束时间
  clockOutStartTime: string // 下班打卡开始时间
  clockOutEndTime: string // 下班打卡结束时间
  restStartTime: string // 休息开始时间
  restEndTime: string // 休息结束时间
  excludeRestTime: boolean // 休息时间是否不计入工作时长
}

// HRM 考勤特殊日期配置
export interface HrmAttendanceSpecialDate {
  type?: number // 日期类型
  date?: Date // 特殊日期
}

// HRM 考勤扣款规则
export interface HrmAttendanceDeductRule {
  lateMethod: number // 迟到扣款方式
  lateDeductMoney: number // 迟到扣款金额
  earlyMethod: number // 早退扣款方式
  earlyDeductMoney: number // 早退扣款金额
  absenteeismMethod: number // 旷工扣款方式
  absenteeismDeductMoney: number // 旷工扣款金额
  misscardMethod: number // 缺卡扣款方式
  misscardDeductMoney: number // 缺卡扣款金额
}

// HRM 考勤组 VO
export interface HrmAttendanceGroupVO {
  id?: number // 考勤组编号
  name: string // 考勤组名称
  openWifiCard?: boolean // 是否启用 WiFi 打卡
  openPointCard?: boolean // 是否启用定位打卡
  rest?: boolean // 是否法定节假日休息
  defaultStatus?: boolean // 是否默认考勤组
  specialDates?: HrmAttendanceSpecialDate[] // 特殊日期配置数组
  deptIds?: number[] // 适用部门编号数组
  deptNames?: string[] // 适用部门名称数组
  employeeIds?: number[] // 适用员工编号数组
  employeeNames?: string[] // 适用员工名称数组
  shifts?: HrmAttendanceShift[] // 班次配置
  points?: HrmAttendancePoint[] // 打卡地点数组
  wifis?: HrmAttendanceWifi[] // 打卡 WiFi 数组
  deductRule?: HrmAttendanceDeductRule // 扣款规则
  createTime?: Date // 创建时间
}

// 获得考勤组分页
export const getAttendanceGroupPage = async (params: PageParam) => {
  return await request.get<PageResult<HrmAttendanceGroupVO[]>>({
    url: '/hrm/attendance/group/page',
    params
  })
}

// 获得考勤组详情
export const getAttendanceGroup = async (id: number) => {
  return await request.get<HrmAttendanceGroupVO>({
    url: '/hrm/attendance/group/get?id=' + id
  })
}

// 获得员工所在考勤组
export const getMyAttendanceGroup = async (employeeId: number) => {
  return await request.get<HrmAttendanceGroupVO>({
    url: '/hrm/attendance/group/my?employeeId=' + employeeId
  })
}

// 创建考勤组
export const createAttendanceGroup = async (data: HrmAttendanceGroupVO) => {
  return await request.post<number>({ url: '/hrm/attendance/group/create', data })
}

// 修改考勤组
export const updateAttendanceGroup = async (data: HrmAttendanceGroupVO) => {
  return await request.put<boolean>({ url: '/hrm/attendance/group/update', data })
}

// 删除考勤组
export const deleteAttendanceGroup = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/attendance/group/delete?id=' + id })
}
