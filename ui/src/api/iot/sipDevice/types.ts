export interface SipDeviceVO {
  /**
   * 设备ID
   */
  id: string | number;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 设备SipID
   */
  deviceSipId: string | number;

  /**
   * 设备名称
   */
  deviceName: string;

  /**
   * 厂商名称
   */
  manufacturer: string;

  /**
   * 产品型号
   */
  model: string;

  /**
   * 固件版本
   */
  firmware: string;

  /**
   * 传输模式
   */
  transport: string;

  /**
   * 流模式
   */
  streamMode: string;

  /**
   * 在线状态
   */
  online: string;

  /**
   * 注册时间
   */
  registerTime: string;

  /**
   * 最后上线时间
   */
  lastConnectTime: string;

  /**
   * 激活时间
   */
  activeTime: string;

  /**
   * 设备入网IP
   */
  ip: string;

  /**
   * 设备接入端口号
   */
  port: number;

  /**
   * 设备地址
   */
  hostAddress: string;

  /**
   * 备注
   */
  remark: string;

}

export interface SipDeviceForm extends BaseEntity {
  /**
   * 设备ID
   */
  id?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 设备SipID
   */
  deviceSipId?: string | number;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 厂商名称
   */
  manufacturer?: string;

  /**
   * 产品型号
   */
  model?: string;

  /**
   * 固件版本
   */
  firmware?: string;

  /**
   * 传输模式
   */
  transport?: string;

  /**
   * 流模式
   */
  streamMode?: string;

  /**
   * 在线状态
   */
  online?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 最后上线时间
   */
  lastConnectTime?: string;

  /**
   * 激活时间
   */
  activeTime?: string;

  /**
   * 设备入网IP
   */
  ip?: string;

  /**
   * 设备接入端口号
   */
  port?: number;

  /**
   * 设备地址
   */
  hostAddress?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface SipDeviceQuery extends PageQuery {

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 设备SipID
   */
  deviceSipId?: string | number;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 厂商名称
   */
  manufacturer?: string;

  /**
   * 产品型号
   */
  model?: string;

  /**
   * 固件版本
   */
  firmware?: string;

  /**
   * 传输模式
   */
  transport?: string;

  /**
   * 流模式
   */
  streamMode?: string;

  /**
   * 在线状态
   */
  online?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 最后上线时间
   */
  lastConnectTime?: string;

  /**
   * 激活时间
   */
  activeTime?: string;

  /**
   * 设备入网IP
   */
  ip?: string;

  /**
   * 设备接入端口号
   */
  port?: number;

  /**
   * 设备地址
   */
  hostAddress?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



