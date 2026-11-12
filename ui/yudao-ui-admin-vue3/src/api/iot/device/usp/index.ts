import request from '@/config/axios'

/** USP (TR-369) 连接配置 VO */
export interface DeviceUspConfigVO {
  id?: number
  deviceId: number
  productId?: number
  agentEndpointId: string
  controllerEndpointId?: string
  mtpType: string
  mqttTopicAgent?: string
  mqttTopicController?: string
  protocolVersion?: string
  lastContactTime?: string
}

/** USP 参数快照 VO */
export interface DeviceUspParamVO {
  id?: number
  path: string
  value: string
  type?: string
  writable?: boolean
  updateTime?: string
}

/** USP (TR-369) API */
export const DeviceUspApi = {
  /** 获取设备 USP 连接配置 */
  getUspConfig: async (deviceId: number) => {
    return await request.get<DeviceUspConfigVO>({
      url: `/iot/device-usp/config/get`,
      params: { deviceId }
    })
  },

  /** 保存设备 USP 连接配置 */
  saveUspConfig: async (data: DeviceUspConfigVO) => {
    return await request.post({ url: `/iot/device-usp/config/save`, data })
  },

  /** 获取设备 USP 参数快照列表 */
  getUspParams: async (deviceId: number, pathPrefix?: string) => {
    return await request.get<DeviceUspParamVO[]>({
      url: `/iot/device-usp/params`,
      params: { deviceId, pathPrefix }
    })
  },

  /** 下发 USP GET 请求获取最新参数值 */
  uspGet: async (deviceId: number, paths: string[]) => {
    return await request.post({
      url: `/iot/device-usp/get?deviceId=${deviceId}`,
      data: paths
    })
  },

  /** 下发 USP SET 请求设置参数 */
  uspSet: async (deviceId: number, params: Record<string, string>) => {
    return await request.post({
      url: `/iot/device-usp/set?deviceId=${deviceId}`,
      data: params
    })
  },

  /** 下发 USP OPERATE 执行操作命令 */
  uspOperate: async (data: { deviceId: number; command: string; inputArgs?: Record<string, string> }) => {
    return await request.post({
      url: `/iot/device-usp/operate`,
      data
    })
  },

  /** 远程重启设备 (Device.Reboot) */
  reboot: async (deviceId: number) => {
    return await request.post({
      url: `/iot/device-usp/reboot`,
      params: { deviceId }
    })
  },

  /** 恢复出厂设置 (Device.FactoryReset) */
  factoryReset: async (deviceId: number) => {
    return await request.post({
      url: `/iot/device-usp/factory-reset`,
      params: { deviceId }
    })
  }
}
