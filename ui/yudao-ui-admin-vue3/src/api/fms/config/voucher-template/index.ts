import request from '@/config/axios'
import type { FmsVoucherAuxiliaryItemVO } from '@/api/fms/voucher'

/** FMS 凭证模板分录信息 */
export interface FmsVoucherTemplateEntryVO {
  digest: string // 摘要内容
  subjectId: number // 科目编号
  quantity?: number // 数量
  unitPrice?: number // 单价
  debitAmount?: number // 借方金额
  creditAmount?: number // 贷方金额
  auxiliaries: FmsVoucherAuxiliaryItemVO[] // 辅助核算项目数组
}

/** FMS 凭证模板信息 */
export interface FmsVoucherTemplateVO {
  id?: number // 模板编号
  accountSetId: number // 账套编号
  name: string // 模板名称
  categoryId: number // 分类编号
  categoryName?: string // 分类名称
  entries: FmsVoucherTemplateEntryVO[] // 凭证模板分录数组
}

// FMS 凭证模板 API
export const FmsVoucherTemplateApi = {
  // 查询凭证模板列表
  getVoucherTemplateList: async (accountSetId: number) => {
    return await request.get<FmsVoucherTemplateVO[]>({
      url: '/fms/config/voucher-template/list',
      params: { accountSetId }
    })
  },

  // 查询凭证模板精简列表
  getVoucherTemplateSimpleList: async (accountSetId: number) => {
    return await request.get<FmsVoucherTemplateVO[]>({
      url: '/fms/config/voucher-template/simple-list',
      params: { accountSetId }
    })
  },

  // 新增凭证模板
  createVoucherTemplate: async (data: FmsVoucherTemplateVO) => {
    return await request.post<number>({ url: '/fms/config/voucher-template/create', data })
  },

  // 修改凭证模板
  updateVoucherTemplate: async (data: FmsVoucherTemplateVO) => {
    return await request.put({ url: '/fms/config/voucher-template/update', data })
  },

  // 删除凭证模板
  deleteVoucherTemplate: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/voucher-template/delete',
      params: { accountSetId, id }
    })
  }
}
