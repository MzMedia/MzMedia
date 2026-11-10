import request from '@/config/axios'
import type { HrmAttendanceLeaveVO } from '@/api/hrm/attendance/leave'

export type { HrmAttendanceLeaveVO } from '@/api/hrm/attendance/leave'

// HRM 员工请假申请创建 VO
export interface HrmAttendanceLeaveCreateVO {
  type?: string // 请假类型
  startTime?: number // 请假开始时间
  endTime?: number // 请假结束时间
  day?: number // 请假天数
  reason?: string // 请假事由
  remark?: string // 备注
}

// 获得我的请假申请列表
export const getMyAttendanceLeaveList = async () => {
  return await request.get<HrmAttendanceLeaveVO[]>({
    url: '/hrm/portal/attendance/leave/list'
  })
}

// 创建我的请假申请
export const createMyAttendanceLeave = async (data: HrmAttendanceLeaveCreateVO) => {
  return await request.post<number>({
    url: '/hrm/portal/attendance/leave/create',
    data
  })
}

// 取消我的请假申请
export const cancelMyAttendanceLeave = async (id: number, reason: string) => {
  return await request.put<boolean>({
    url: '/hrm/portal/attendance/leave/cancel',
    data: { id, reason }
  })
}
