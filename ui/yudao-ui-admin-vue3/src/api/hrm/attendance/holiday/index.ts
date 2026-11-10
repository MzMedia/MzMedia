import request from '@/config/axios'

// HRM 考勤节假日 VO
export interface HrmAttendanceHolidayVO {
  id?: number // 节假日编号
  date?: number // 日期
  type: number // 日期类型
  createTime?: Date // 创建时间
}

// 获得考勤节假日分页
export const getAttendanceHolidayPage = async (params: PageParam) => {
  return await request.get<PageResult<HrmAttendanceHolidayVO[]>>({
    url: '/hrm/attendance/holiday/page',
    params
  })
}

// 获得考勤节假日详情
export const getAttendanceHoliday = async (id: number) => {
  return await request.get<HrmAttendanceHolidayVO>({
    url: '/hrm/attendance/holiday/get?id=' + id
  })
}

// 创建考勤节假日
export const createAttendanceHoliday = async (data: HrmAttendanceHolidayVO) => {
  return await request.post<number>({ url: '/hrm/attendance/holiday/create', data })
}

// 修改考勤节假日
export const updateAttendanceHoliday = async (data: HrmAttendanceHolidayVO) => {
  return await request.put<boolean>({ url: '/hrm/attendance/holiday/update', data })
}

// 删除考勤节假日
export const deleteAttendanceHoliday = async (id: number) => {
  return await request.delete<boolean>({ url: '/hrm/attendance/holiday/delete?id=' + id })
}
