import request from '@/config/axios'

/** FMS 辅助核算类别信息 */
export interface FmsAuxiliaryTypeVO {
  id?: number // 类别编号
  accountSetId: number // 账套编号
  name: string // 类别名称
  type?: number // 辅助核算类型
  systemPreset?: boolean // 是否系统预置
}

export type FmsAuxiliaryTypeOptionVO = FmsAuxiliaryTypeVO & { id: number }

// FMS 辅助核算类别 API
export const FmsAuxiliaryTypeApi = {
  // 查询辅助核算类别列表
  getAuxiliaryTypeList: async (accountSetId: number) => {
    return await request.get<FmsAuxiliaryTypeVO[]>({
      url: '/fms/config/auxiliary-type/list',
      params: { accountSetId }
    })
  },

  // 查询辅助核算类别精简列表
  getAuxiliaryTypeSimpleList: async (accountSetId: number) => {
    return await request.get<FmsAuxiliaryTypeOptionVO[]>({
      url: '/fms/config/auxiliary-type/simple-list',
      params: { accountSetId }
    })
  },

  // 新增辅助核算类别
  createAuxiliaryType: async (data: FmsAuxiliaryTypeVO) => {
    return await request.post<number>({ url: '/fms/config/auxiliary-type/create', data })
  },

  // 修改辅助核算类别
  updateAuxiliaryType: async (data: FmsAuxiliaryTypeVO) => {
    return await request.put({ url: '/fms/config/auxiliary-type/update', data })
  },

  // 删除辅助核算类别
  deleteAuxiliaryType: async (accountSetId: number, id: number) => {
    return await request.delete({
      url: '/fms/config/auxiliary-type/delete',
      params: { accountSetId, id }
    })
  }
}
