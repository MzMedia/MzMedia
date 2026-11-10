import request from '@/config/axios'

// HRM 员工个人备忘 VO
export interface HrmEmployeePersonalNoteVO {
  content: string // 备忘内容
  reminderTime: number // 提醒时间
}

// 创建员工个人备忘
export const createEmployeePersonalNote = async (data: HrmEmployeePersonalNoteVO) => {
  return await request.post<number>({ url: '/hrm/employee/personal-note/create', data })
}

// 删除员工个人备忘
export const deleteEmployeePersonalNote = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/employee/personal-note/delete',
    params: { id }
  })
}
