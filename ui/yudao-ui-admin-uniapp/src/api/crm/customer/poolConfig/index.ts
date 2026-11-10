import { http } from '@/http/http'

/** 客户公海规则配置 */
export interface CustomerPoolConfig {
  enabled?: boolean
  contactExpireDays?: number
  dealExpireDays?: number
  notifyEnabled?: boolean
  notifyDays?: number
}

/** 获取客户公海规则配置 */
export function getCustomerPoolConfig() {
  return http.get<CustomerPoolConfig>('/crm/customer-pool-config/get')
}

/** 更新客户公海规则配置 */
export function saveCustomerPoolConfig(data: CustomerPoolConfig) {
  return http.put<boolean>('/crm/customer-pool-config/save', data)
}
