import request from '@/config/axios'

/** CWMP (TR-069) 连接配置 VO */
export interface DeviceCwmpConfigVO {
  id?: number
  deviceId: number
  productId?: number
  acsUsername?: string
  acsPassword?: string
  connectionRequestUrl?: string
  connectionRequestUsername?: string
  connectionRequestPassword?: string
  periodicInformEnable?: boolean
  periodicInformInterval?: number
  lastInformTime?: string
  dataModelType?: string
  hardwareVersion?: string
  softwareVersion?: string
  provisioningCode?: string
}

/** CWMP 参数快照 VO */
export interface DeviceCwmpParamVO {
  id?: number
  path: string
  value: string
  type?: string
  writable?: boolean
  updateTime?: string
}

/** CWMP Wi-Fi 快速配置 VO */
export interface DeviceCwmpWifiVO {
  ssid?: string
  password?: string
  enable?: boolean
  securityMode?: string
  channel?: number
  band?: string
}

/** CWMP (TR-069) API */
export const DeviceCwmpApi = {
  /** 获取设备 CWMP 连接配置 */
  getCwmpConfig: async (deviceId: number) => {
    return await request.get<DeviceCwmpConfigVO>({
      url: `/iot/device-cwmp/config/get`,
      params: { deviceId }
    })
  },

  /** 保存设备 CWMP 连接配置 */
  saveCwmpConfig: async (data: DeviceCwmpConfigVO) => {
    return await request.post({ url: `/iot/device-cwmp/config/save`, data })
  },

  /** 获取设备 CWMP 参数快照列表 */
  getCwmpParams: async (deviceId: number, pathPrefix?: string) => {
    return await request.get<DeviceCwmpParamVO[]>({
      url: `/iot/device-cwmp/params`,
      params: { deviceId, pathPrefix }
    })
  },

  /** 下发 GetParameterNames 发现参数树 */
  refreshParameterNames: async (deviceId: number, path?: string, nextLevel?: boolean) => {
    return await request.post({
      url: `/iot/device-cwmp/params/refresh-names`,
      params: { deviceId, path, nextLevel }
    })
  },

  /** 下发 GetParameterValues 读取最新参数值 */
  refreshParameterValues: async (deviceId: number, paths: string[]) => {
    return await request.post({
      url: `/iot/device-cwmp/params/refresh-values?deviceId=${deviceId}`,
      data: paths
    })
  },

  /** 下发 SetParameterValues 修改参数 */
  setParameterValues: async (deviceId: number, params: Record<string, string>) => {
    return await request.post({
      url: `/iot/device-cwmp/params/set`,
      data: { deviceId, params }
    })
  },

  /** 远程重启设备 */
  reboot: async (deviceId: number) => {
    return await request.post({
      url: `/iot/device-cwmp/reboot`,
      params: { deviceId }
    })
  },

  /** 恢复出厂设置 */
  factoryReset: async (deviceId: number) => {
    return await request.post({
      url: `/iot/device-cwmp/factory-reset`,
      params: { deviceId }
    })
  },

  /** 主动唤醒设备 (Connection Request) */
  triggerConnectionRequest: async (deviceId: number) => {
    return await request.post<boolean>({
      url: `/iot/device-cwmp/connection-request`,
      params: { deviceId }
    })
  },

  /** 获取 Wi-Fi 配置 */
  getWifiConfig: async (deviceId: number) => {
    return await request.get<DeviceCwmpWifiVO>({
      url: `/iot/device-cwmp/wifi`,
      params: { deviceId }
    })
  },

  /** 保存 Wi-Fi 配置 */
  setWifiConfig: async (deviceId: number, data: DeviceCwmpWifiVO) => {
    return await request.post({
      url: `/iot/device-cwmp/wifi/set?deviceId=${deviceId}`,
      data
    })
  },

  /** 执行 Ping 诊断 */
  runPingDiagnostics: async (deviceId: number, host: string, count?: number) => {
    return await request.post({
      url: `/iot/device-cwmp/diagnostics/ping`,
      data: { deviceId, host, count }
    })
  }
}
