import { http } from '@/http/http'

/** Redis 命令统计 */
export interface RedisCommandStats {
  command: string // 命令
  calls: number // 调用次数
  usec: number // 消耗时长（微秒）
}

/** Redis 监控信息 */
export interface RedisMonitorInfo {
  info: Record<string, string> // Redis info 指标集（字段繁多，按需取用）
  dbSize: number // Key 数量
  commandStats: RedisCommandStats[] // 命令统计
}

/** 获取 Redis 监控信息 */
export function getRedisMonitorInfo() {
  return http.get<RedisMonitorInfo>('/infra/redis/get-monitor-info')
}
