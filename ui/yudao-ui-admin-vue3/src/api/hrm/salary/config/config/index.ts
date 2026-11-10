import request from '@/config/axios'

// 计薪配置创建 Request VO
export interface SalaryConfigCreateReqVO {
  cycleStartDay: number // 计薪周期开始日
  socialSecurityMonthType: number // 社保对应月份类型
  startYear: number // 工资开始年份
  startMonth: number // 工资开始月份
}

// 计薪配置更新 Request VO
export interface SalaryConfigUpdateReqVO {
  socialSecurityMonthType: number // 社保对应月份类型
}

// 计薪配置 VO
export interface SalaryConfigVO {
  id: number // 配置编号
  cycleStartDay?: number // 计薪周期开始日
  cycleEndDay?: number // 计薪周期结束日
  socialSecurityMonthType?: number // 社保对应月份类型
  startYear?: number // 工资开始年份
  startMonth?: number // 工资开始月份
  createTime?: Date // 创建时间
}

// 创建计薪配置
export const createSalaryConfig = async (data: SalaryConfigCreateReqVO) => {
  return await request.post<number>({ url: '/hrm/salary/config/create', data })
}

// 更新对应社保自然月
export const updateSalaryConfig = async (data: SalaryConfigUpdateReqVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/config/update', data })
}

// 获得计薪配置
export const getSalaryConfig = async () => {
  return await request.get<SalaryConfigVO>({ url: '/hrm/salary/config/get' })
}
