import { http } from '@/http/http'

/** 流程定义 */
export interface ProcessDefinition {
  id: string
  key: string
  version?: number
  name: string
  description?: string
  icon?: string
  category: string
  formType?: number
  formId?: number
  formName?: string
  formConf?: string
  formFields?: string[]
  formCustomCreatePath?: string
  formCustomViewPath?: string
  bpmnXml?: string
  simpleModel?: string
  suspensionState: number
}

/** 获取流程定义列表 */
export function getProcessDefinitionList(params?: { suspensionState?: number }) {
  return http.get<ProcessDefinition[]>('/bpm/process-definition/list', params)
}

/** 获取流程定义详情 */
export function getProcessDefinition(id?: string, key?: string) {
  return http.get<ProcessDefinition>('/bpm/process-definition/get', { id, key })
}
