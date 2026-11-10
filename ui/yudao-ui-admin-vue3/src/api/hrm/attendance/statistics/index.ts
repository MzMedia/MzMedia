import request from '@/config/axios'
import type { HrmAttendanceClockVO } from '@/api/hrm/attendance/clock'
import type { HrmAttendanceLeaveVO } from '@/api/hrm/attendance/leave'

// HRM 月度考勤汇总 VO
export interface HrmAttendanceMonthRecordVO {
  employeeId: number // 员工编号
  employeeName: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  attendanceGroupName?: string // 考勤组名称
  entryTime?: Date // 入职时间
  employeeStatus?: number // 员工状态
  workCity?: string // 工作城市
  year: number // 年份
  month: number // 月份
  attendDays: number // 应出勤天数
  actualDays: number // 实际出勤天数
  lateMinute: number // 迟到分钟数
  lateCount: number // 迟到次数
  earlyMinute: number // 早退分钟数
  earlyCount: number // 早退次数
  misscardCount: number // 缺卡次数
  absenteeismDays: number // 旷工天数
  absenteeismMinutes: number // 旷工分钟数
  leaveDays: number // 请假天数
  leaveMinutes: number // 请假分钟数
  lateDeductAmount: number // 迟到扣款
  earlyDeductAmount: number // 早退扣款
  misscardDeductAmount: number // 缺卡扣款
  absenteeismDeductAmount: number // 旷工扣款
  attendanceDeductAmount: number // 考勤扣款合计
  fullAttendance: boolean // 是否全勤
}

// HRM 每日打卡概况 VO
export interface HrmAttendanceDailyOverviewVO {
  clocks: HrmAttendanceClockVO[] // 打卡记录
  attendanceResult?: string // 考勤结果
  overviews: HrmAttendanceDailyOverviewItemVO[] // 打卡概况展示项
}

// HRM 每日打卡概况展示项 VO
export interface HrmAttendanceDailyOverviewItemVO {
  type?: string // 打卡类型
  time?: string // 打卡时间
  status?: string // 打卡状态
  text?: string // 考勤结果
}

// HRM 月度每日考勤概览 VO
export interface HrmAttendanceMonthDailyOverviewVO {
  employeeId: number // 员工编号
  employeeName: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  year: number // 年份
  month: number // 月份
  dailyClockMap: Record<string, HrmAttendanceDailyOverviewVO> // 每日打卡概况
}

// HRM 每日考勤明细 VO
export interface HrmAttendanceDailyDetailVO {
  employeeId: number // 员工编号
  employeeName?: string // 员工姓名
  jobNumber?: string // 工号
  deptId?: number // 部门编号
  deptName?: string // 部门名称
  postName?: string // 职位名称
  attendanceTime: Date // 考勤时间
  shiftName?: string // 班次名称
  scheduled?: boolean // 是否排班
  requiredClockCount?: number // 应打卡次数
  scheduledMinutes?: number // 应出勤分钟数
  misscardCount?: number // 缺卡次数
  absenteeism?: boolean // 是否旷工
  absenteeismMinutes?: number // 旷工分钟数
  absenteeismDays?: number // 旷工天数
  leaveStatus?: boolean // 是否请假
  leaveMinutes?: number // 请假分钟数
  leaveDays?: number // 请假天数
  attendanceResult?: string // 考勤结果
  lateCount: number // 迟到次数
  lateMinutes?: number // 迟到分钟数
  earlyCount: number // 早退次数
  earlyMinutes?: number // 早退分钟数
  clockList: HrmAttendanceClockVO[] // 打卡记录
}

// HRM 月度考勤详情 VO
export interface HrmAttendanceMonthDetailVO {
  summary: HrmAttendanceMonthRecordVO // 月度汇总
  dailyDetails: HrmAttendanceDailyDetailVO[] // 每日明细
  leaves: HrmAttendanceLeaveVO[] // 请假记录
}

// 获得月度考勤汇总分页
export const getAttendanceMonthRecordPage = async (
  params: PageParam & { year: number; month: number }
) => {
  return await request.get<PageResult<HrmAttendanceMonthRecordVO[]>>({
    url: '/hrm/attendance/statistics/month-record-page',
    params
  })
}

// 获得月度打卡概况分页
export const getAttendanceMonthDailyOverviewPage = async (
  params: PageParam & { year: number; month: number }
) => {
  return await request.get<PageResult<HrmAttendanceMonthDailyOverviewVO[]>>({
    url: '/hrm/attendance/statistics/month-daily-page',
    params
  })
}

// 获得月度考勤详情
export const getAttendanceMonthDetail = async (params: {
  employeeId: number
  year: number
  month: number
}) => {
  return await request.get<HrmAttendanceMonthDetailVO>({
    url: '/hrm/attendance/statistics/month-detail',
    params
  })
}

// 获得每日考勤明细
export const getAttendanceDailyDetail = async (params: {
  employeeId: number
  attendanceTime: string
}) => {
  return await request.get<HrmAttendanceDailyDetailVO>({
    url: '/hrm/attendance/statistics/daily-detail',
    params
  })
}

// 导出月度考勤汇总
export const exportAttendanceMonthRecord = async (
  params: PageParam & { year: number; month: number }
) => {
  return await request.download({
    url: '/hrm/attendance/statistics/month-record-export-excel',
    params
  })
}

// 导出月度打卡概况
export const exportAttendanceMonthDailyOverview = async (
  params: PageParam & { year: number; month: number }
) => {
  return await request.download({
    url: '/hrm/attendance/statistics/month-daily-export-excel',
    params
  })
}
