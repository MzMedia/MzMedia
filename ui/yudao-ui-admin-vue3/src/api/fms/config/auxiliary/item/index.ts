import request from '@/config/axios'

/** FMS 辅助核算项目信息 */
export interface FmsAuxiliaryItemVO {
  id?: number // 项目编号
  accountSetId: number // 账套编号
  auxiliaryTypeId: number // 辅助核算类别编号
  code: string // 项目编码
  name: string // 项目名称
  status?: number // 状态
  remark?: string // 备注
  specification?: string // 规格
  unit?: string // 单位
  createTime?: Date // 创建时间
}

/** FMS 辅助核算项目下拉选项 */
export type FmsAuxiliaryItemOptionVO = FmsAuxiliaryItemVO & { id: number }

/** FMS 辅助核算项目分页查询参数 */
export interface FmsAuxiliaryItemPageReqVO extends PageParam {
  accountSetId: number // 账套编号
  auxiliaryTypeId: number // 辅助核算类别编号
  search?: string // 关键词
}

/** FMS 辅助核算项目导入结果 */
export interface FmsAuxiliaryItemImportRespVO {
  totalCount: number // 总数量
  successItemCodes: string[] // 成功项目编码数组
  failureReasons: Record<string, string> // 失败原因 Map
}

// FMS 辅助核算项目 API
export const FmsAuxiliaryItemApi = {
  // 查询辅助核算项目分页
  getAuxiliaryItemPage: async (params: FmsAuxiliaryItemPageReqVO) => {
    return await request.get<PageResult<FmsAuxiliaryItemVO[]>>({
      url: '/fms/config/auxiliary-item/page',
      params
    })
  },

  // 查询辅助核算项目精简列表
  getAuxiliaryItemSimpleList: async (accountSetId: number, auxiliaryTypeId: number) => {
    return await request.get<FmsAuxiliaryItemOptionVO[]>({
      url: '/fms/config/auxiliary-item/simple-list',
      params: { accountSetId, auxiliaryTypeId }
    })
  },

  // 新增辅助核算项目
  createAuxiliaryItem: async (data: FmsAuxiliaryItemVO) => {
    return await request.post<number>({ url: '/fms/config/auxiliary-item/create', data })
  },

  // 修改辅助核算项目
  updateAuxiliaryItem: async (data: FmsAuxiliaryItemVO) => {
    return await request.put({ url: '/fms/config/auxiliary-item/update', data })
  },

  // 批量删除辅助核算项目
  deleteAuxiliaryItemList: async (accountSetId: number, ids: number[]) => {
    return await request.delete({
      url: '/fms/config/auxiliary-item/delete-list',
      params: { accountSetId, ids: ids.join(',') }
    })
  },

  // 修改辅助核算项目状态
  updateAuxiliaryItemStatus: async (accountSetId: number, id: number, status: number) => {
    return await request.put({
      url: '/fms/config/auxiliary-item/update-status',
      data: { accountSetId, id, status }
    })
  },

  // 导出辅助核算项目 Excel
  exportAuxiliaryItem: async (params: FmsAuxiliaryItemPageReqVO) => {
    return await request.download({ url: '/fms/config/auxiliary-item/export-excel', params })
  },

  // 下载辅助核算项目导入模板
  getAuxiliaryItemImportTemplate: async (type: number) => {
    return await request.download({
      url: '/fms/config/auxiliary-item/get-import-template',
      params: { type }
    })
  },

  // 导入辅助核算项目
  importAuxiliaryItem: async (accountSetId: number, auxiliaryTypeId: number, file: File) => {
    const data = new FormData()
    data.append('accountSetId', String(accountSetId))
    data.append('auxiliaryTypeId', String(auxiliaryTypeId))
    data.append('file', file)
    return await request.post<FmsAuxiliaryItemImportRespVO>({
      url: '/fms/config/auxiliary-item/import',
      data,
      headersType: 'multipart/form-data'
    })
  }
}
