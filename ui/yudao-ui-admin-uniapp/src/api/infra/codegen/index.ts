import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 代码生成表定义 */
export interface CodegenTable {
  id: number
  tableId: number
  dataSourceConfigId: number
  scene: number // 生成场景
  tableName: string // 表名
  tableComment: string // 表描述
  remark: string // 备注
  moduleName: string // 模块名
  businessName: string // 业务名
  className: string // 类名
  classComment: string // 类描述
  author: string // 作者
  templateType: number // 模板类型
  frontType: number // 前端类型
  parentMenuId: number // 上级菜单
  createTime: number // 创建时间
  updateTime: number // 更新时间
}

/** 代码生成字段定义 */
export interface CodegenColumn {
  id: number
  tableId: number
  columnName: string // 字段名
  dataType: string // 数据库字段类型
  columnComment: string // 字段描述
  nullable: boolean // 是否允许为空
  primaryKey: boolean // 是否主键
  ordinalPosition: number // 排序
  javaType: string // Java 类型
  javaField: string // Java 属性名
  dictType: string // 字典类型
  example: string // 示例值
  createOperation: boolean // 是否为新增操作字段
  updateOperation: boolean // 是否为修改操作字段
  listOperation: boolean // 是否为查询操作字段
  listOperationCondition: string // 查询操作条件类型
  listOperationResult: boolean // 是否为查询返回字段
  htmlType: string // 显示类型
}

/** 代码生成表 + 字段详情 */
export interface CodegenDetail {
  table: CodegenTable // 表定义
  columns: CodegenColumn[] // 字段定义
}

/** 代码预览文件 */
export interface CodegenPreview {
  filePath: string // 文件路径
  code: string // 代码内容
}

/** 数据库自带的表（导入时可选） */
export interface CodegenDbTable {
  name: string // 表名
  comment: string // 表描述
}

/** 获取代码生成表定义分页 */
export function getCodegenTablePage(params: PageParam) {
  return http.get<PageResult<CodegenTable>>('/infra/codegen/table/page', params)
}

/** 获取代码生成表 + 字段详情 */
export function getCodegenDetail(tableId: number) {
  return http.get<CodegenDetail>(`/infra/codegen/detail?tableId=${tableId}`)
}

/** 基于数据库表结构，同步表和字段定义 */
export function syncCodegenFromDB(tableId: number) {
  return http.put<boolean>(`/infra/codegen/sync-from-db?tableId=${tableId}`)
}

/** 预览生成代码 */
export function previewCodegen(tableId: number) {
  return http.get<CodegenPreview[]>(`/infra/codegen/preview?tableId=${tableId}`)
}

/** 删除代码生成表定义 */
export function deleteCodegenTable(tableId: number) {
  return http.delete<boolean>(`/infra/codegen/delete?tableId=${tableId}`)
}

/** 更新代码生成表 + 字段定义 */
export function updateCodegenTable(data: CodegenDetail) {
  return http.put<boolean>('/infra/codegen/update', data)
}

/** 获取数据库自带的表列表（已导入的会被后端过滤） */
export function getCodegenDbTableList(params: { dataSourceConfigId: number, name?: string, comment?: string }) {
  return http.get<CodegenDbTable[]>('/infra/codegen/db/table/list', params)
}

/** 基于数据库表结构，批量创建代码生成表定义 */
export function createCodegenList(data: { dataSourceConfigId: number, tableNames: string[] }) {
  return http.post<number[]>('/infra/codegen/create-list', data)
}
