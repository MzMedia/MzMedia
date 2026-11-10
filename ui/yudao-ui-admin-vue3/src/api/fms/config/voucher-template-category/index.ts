import request from '@/config/axios'

/** FMS 凭证模板分类信息 */
export interface FmsVoucherTemplateCategoryVO {
  id?: number // 分类编号
  accountSetId: number // 账套编号
  name: string // 分类名称
}

// FMS 凭证模板分类 API
export const FmsVoucherTemplateCategoryApi = {
  // 查询凭证模板分类列表
  getVoucherTemplateCategoryList: async (accountSetId: number) => {
    return await request.get<FmsVoucherTemplateCategoryVO[]>({
      url: '/fms/config/voucher-template-category/list',
      params: { accountSetId }
    })
  },

  // 查询凭证模板分类精简列表
  getVoucherTemplateCategorySimpleList: async (accountSetId: number) => {
    return await request.get<FmsVoucherTemplateCategoryVO[]>({
      url: '/fms/config/voucher-template-category/simple-list',
      params: { accountSetId }
    })
  },

  // 新增凭证模板分类
  createVoucherTemplateCategory: async (data: FmsVoucherTemplateCategoryVO) => {
    return await request.post<number>({
      url: '/fms/config/voucher-template-category/create',
      data
    })
  },

  // 修改凭证模板分类
  updateVoucherTemplateCategory: async (data: FmsVoucherTemplateCategoryVO) => {
    return await request.put({ url: '/fms/config/voucher-template-category/update', data })
  },

  // 删除凭证模板分类
  deleteVoucherTemplateCategory: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/voucher-template-category/delete',
      params: { accountSetId, id }
    })
  }
}
