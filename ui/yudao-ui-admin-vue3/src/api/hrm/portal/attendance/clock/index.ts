import request from '@/config/axios'

// 员工端考勤记录 VO
export interface AttendanceRecordVO {
  id: number // 打卡记录编号
  employeeId: number // 打卡员工编号
  clockTime: Date // 打卡时间
  type?: number // 打卡类型
  attendanceTime?: Date // 应打卡时间
  sourceType?: number // 打卡来源
  status?: number // 打卡状态
  stage?: number // 打卡阶段
  address?: string // 打卡地址
  longitude?: number // 经度
  latitude?: number // 纬度
  ssid?: string // WiFi 名称
  mac?: string // WiFi MAC 地址
  remark?: string // 备注
}

// 获得我的考勤记录列表
export const getAttendanceRecordList = async (year?: number, month?: number) => {
  return await request.get<AttendanceRecordVO[]>({
    url: '/hrm/portal/attendance/clock/list',
    params: { year, month }
  })
}
