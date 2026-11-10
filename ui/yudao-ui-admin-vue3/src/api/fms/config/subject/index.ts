import request from '@/config/axios'

/** FMS 科目信息 */
export interface FmsSubjectVO {
  id: number // 科目编号
  accountSetId: number // 账套编号
  code: string // 科目编码
  name: string // 科目名称
  parentId: number // 上级科目编号
  type: number // 科目类型
  category: number // 科目类别
  balanceDirection: number // 余额方向
  auxiliaryTypeIds: number[] // 辅助核算类别编号数组
  auxiliaryTypeNames: string[] // 辅助核算类别名称数组
  currencyIds: number[] // 外币核算币别编号数组
  quantityAccounting: boolean // 是否启用数量核算
  quantityUnit?: string // 数量单位
  cash: boolean // 是否现金及现金等价物
  migrateParentData?: boolean // 是否迁移上级科目历史数据
  auxiliaryMappings?: FmsSubjectAuxiliaryMappingVO[] // 辅助核算历史数据迁移项目数组
  status?: number // 状态
  level?: number // 层级
  children?: FmsSubjectVO[] // 子级科目数组，由前端按需生成
  createTime: Date // 创建时间
}

/** FMS 科目辅助核算历史数据迁移项目 */
export interface FmsSubjectAuxiliaryMappingVO {
  typeId: number // 辅助核算类别编号
  itemId?: number // 辅助核算项目编号
}

/** FMS 科目状态修改参数 */
export interface FmsSubjectStatusReqVO {
  accountSetId: number // 账套编号
  ids: number[] // 科目编号数组
  status: number // 状态
}

/** FMS 科目使用情况 */
export interface FmsSubjectUsageVO {
  childCount: number // 下级科目数量
  voucherEntryCount: number // 凭证分录数量
  initialBalanceCount: number // 初始余额数量
  auxiliaryCombinationCount: number // 辅助核算组合数量
  quantityDataCount: number // 包含数量数据的记录数量
  used: boolean // 是否已被业务使用
}

/** FMS 科目导入结果 */
export interface FmsSubjectImportRespVO {
  totalCount: number // 总数量
  successSubjectCodes: string[] // 成功科目编码数组
  failureReasons: Record<string, string> // 失败原因 Map
}

// 查询科目列表
export const getSubjectList = (accountSetId: number, type?: number) => {
  return request.get<FmsSubjectVO[]>({
    url: '/fms/config/subject/list',
    params: { accountSetId, type }
  })
}

// 查询科目精简列表
export const getSubjectSimpleList = (accountSetId: number, type?: number) => {
  return request.get<FmsSubjectVO[]>({
    url: '/fms/config/subject/simple-list',
    params: { accountSetId, type }
  })
}

// 查询指定期间有发生额的科目精简列表
export const getDetailSubjectList = (params: {
  accountSetId: number
  startMonth: string
  endMonth: string
}) => {
  return request.get<FmsSubjectVO[]>({ url: '/fms/ledger/detail/subject-list', params })
}

// 查询科目详情
export const getSubject = (accountSetId: number, id: number) => {
  return request.get<FmsSubjectVO>({
    url: '/fms/config/subject/get',
    params: { accountSetId, id }
  })
}

// 查询科目使用情况
export const getSubjectUsage = (accountSetId: number, id: number) => {
  return request.get<FmsSubjectUsageVO>({
    url: '/fms/config/subject/get-usage',
    params: { accountSetId, id }
  })
}

// 新增科目
export const createSubject = (data: FmsSubjectVO) => {
  return request.post<number>({ url: '/fms/config/subject/create', data })
}

// 修改科目
export const updateSubject = (data: FmsSubjectVO) => {
  return request.put({ url: '/fms/config/subject/update', data })
}

// 批量删除科目
export const deleteSubjectList = (accountSetId: number, ids: number[]) => {
  return request.delete({
    url: '/fms/config/subject/delete-list',
    data: { accountSetId, ids }
  })
}

// 修改科目状态
export const updateSubjectStatus = (data: FmsSubjectStatusReqVO) => {
  return request.put({ url: '/fms/config/subject/update-status', data })
}

// 导出科目 Excel
export const exportSubject = (accountSetId: number, type?: number) => {
  return request.download({
    url: '/fms/config/subject/export-excel',
    params: { accountSetId, type }
  })
}

// 下载科目导入模板
export const getSubjectImportTemplate = () => {
  return request.download({ url: '/fms/config/subject/get-import-template' })
}

// 导入科目
export const importSubject = (accountSetId: number, file: File) => {
  const data = new FormData()
  data.append('accountSetId', String(accountSetId))
  data.append('file', file)
  return request.post<FmsSubjectImportRespVO>({
    url: '/fms/config/subject/import',
    data,
    headersType: 'multipart/form-data'
  })
}
