import request from '@/config/axios'
import type { HrmAttendanceMonthDetailVO } from '@/api/hrm/attendance/statistics'

// 员工端月度考勤详情 VO
export type AttendanceMonthDetailVO = HrmAttendanceMonthDetailVO

// 获得我的月度考勤详情
export const getAttendanceMonthDetail = async (year?: number, month?: number) => {
  return await request.get<AttendanceMonthDetailVO>({
    url: '/hrm/portal/attendance/statistics/month-detail',
    params: { year, month }
  })
}

// 导出我的月度考勤
export const exportAttendanceMonthDetail = async (year: number, month: number) => {
  return await request.download({
    url: '/hrm/portal/attendance/statistics/export-excel',
    params: { year, month }
  })
}
