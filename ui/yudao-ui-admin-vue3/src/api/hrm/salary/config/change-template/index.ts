import request from '@/config/axios'

// HRM 调薪项 VO
export interface HrmSalaryChangeOptionVO {
  name: string // 薪资项名称
  code: number // 薪资项编码
}

// HRM 调薪模板 VO
export interface HrmSalaryChangeTemplateVO {
  id?: number // 调薪模板编号
  name: string // 模板名称
  defaultStatus: boolean // 是否默认模板
  options: HrmSalaryChangeOptionVO[] // 调薪项配置
  createTime?: Date // 创建时间
}

// 获得调薪模板列表
export const getSalaryChangeTemplateList = async () => {
  return await request.get<HrmSalaryChangeTemplateVO[]>({
    url: '/hrm/salary/change-template/list'
  })
}

// 获得调薪模板
export const getSalaryChangeTemplate = async (id: number) => {
  return await request.get<HrmSalaryChangeTemplateVO>({
    url: '/hrm/salary/change-template/get?id=' + id
  })
}

// 创建调薪模板
export const createSalaryChangeTemplate = async (data: HrmSalaryChangeTemplateVO) => {
  return await request.post<number>({ url: '/hrm/salary/change-template/create', data })
}

// 修改调薪模板
export const updateSalaryChangeTemplate = async (data: HrmSalaryChangeTemplateVO) => {
  return await request.put<boolean>({ url: '/hrm/salary/change-template/update', data })
}

// 删除调薪模板
export const deleteSalaryChangeTemplate = async (id: number) => {
  return await request.delete<boolean>({
    url: '/hrm/salary/change-template/delete?id=' + id
  })
}
