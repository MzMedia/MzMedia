import { http } from '@/http/http'

/** 合同配置 */
export interface ContractConfig {
  notifyEnabled?: boolean
  notifyDays?: number
}

/** 获取合同配置 */
export function getContractConfig() {
  return http.get<ContractConfig>('/crm/contract-config/get')
}

/** 更新合同配置 */
export function saveContractConfig(data: ContractConfig) {
  return http.put<boolean>('/crm/contract-config/save', data)
}
