import request from '@/config/axios'
import type { HrmHomeCalendarItemVO } from '@/api/hrm/home'

export type { HrmHomeCalendarItemVO } from '@/api/hrm/home'

// 获得员工端首页日历
export const getEmployeeHomeCalendar = async (params: { startDate: string; endDate: string }) => {
  return await request.get<HrmHomeCalendarItemVO[]>({
    url: '/hrm/portal/home/calendar',
    params
  })
}
