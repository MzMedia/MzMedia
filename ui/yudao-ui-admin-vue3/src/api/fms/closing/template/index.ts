import request from '@/config/axios'

/** FMS 结账模板科目规则信息 */
export interface FmsClosingTemplateSubjectRuleVO {
  subjectId?: number // 科目编号
  subjectCode?: string // 科目编码快照
  digest: string // 摘要
  direction: number // 借贷方向
  amountRatio: number // 金额比例
}

/** FMS 结账模板信息 */
export interface FmsClosingTemplateVO {
  id?: number // 模板编号
  accountSetId: number // 账套编号
  presetCode?: string // 系统预置编码
  name: string // 模板名称
  category: number // 模板分类
  periodEnd: boolean // 是否期末结转
  subjectId?: number // 来源科目编号
  formulaRule?: number // 取数规则
  timeType?: number // 取数时间类型
  subjects: FmsClosingTemplateSubjectRuleVO[] // 结转科目规则数组
  sort: number // 显示顺序
  createTime?: Date // 创建时间
}

// FMS 结账模板 API
export const FmsClosingTemplateApi = {
  // 查询结账模板列表
  getClosingTemplateList: async (accountSetId: number) => {
    return await request.get<FmsClosingTemplateVO[]>({
      url: '/fms/closing/template/list',
      params: { accountSetId }
    })
  },

  // 新增结账模板
  createClosingTemplate: async (data: FmsClosingTemplateVO) => {
    return await request.post<number>({ url: '/fms/closing/template/create', data })
  },

  // 修改结账模板
  updateClosingTemplate: async (data: FmsClosingTemplateVO) => {
    return await request.put({ url: '/fms/closing/template/update', data })
  },

  // 删除结账模板
  deleteClosingTemplate: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/closing/template/delete',
      params: { accountSetId, id }
    })
  }
}
