export interface DeviceVO {
  /**
   * 设备ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 子用户ID
   */
  subUid: string | number;

  /**
   * 子设备ID
   */
  subDid: string | number;

  /**
   * 设备名称
   */
  deviceName: string;

  /**
   * 设备昵称
   */
  nickName: string;

  /**
   * 产品key
   */
  productKey: string;

  /**
   * 设备编号
   */
  serialNumber: string;

  /**
   * 产品密钥
   */
  secret: string;

  /**
   * 固件版本
   */
  firmwareVersion: string;

  /**
   * 设备位置
   */
  locate: string;

  /**
   * 设备标签
   */
  tag: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status: number;

  /**
   * 设备IP
   */
  ip: string;

  /**
   * 激活时间
   */
  activeTime: string;

  /**
   * 物模型值
   */
  thingsModel: string;

  /**
   * 设备摘要
   */
  summary: string;

  /**
   * 备注
   */
  remark: string;

}

export interface DeviceForm extends BaseEntity {
  /**
   * 设备ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 子用户ID
   */
  subUid?: string | number;

  /**
   * 子设备ID
   */
  subDid?: string | number;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 设备昵称
   */
  nickName?: string;

  /**
   * 产品key
   */
  productKey?: string;

  /**
   * 设备编号
   */
  serialNumber?: string;

  /**
   * 产品密钥
   */
  secret?: string;

  /**
   * 固件版本
   */
  firmwareVersion?: string;

  /**
   * 设备位置
   */
  locate?: string;

  /**
   * 设备标签
   */
  tag?: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status?: number;

  /**
   * 设备IP
   */
  ip?: string;

  /**
   * 激活时间
   */
  activeTime?: string;

  /**
   * 物模型值
   */
  thingsModel?: string;

  /**
   * 设备摘要
   */
  summary?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface DeviceQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 子用户ID
   */
  subUid?: string | number;

  /**
   * 子设备ID
   */
  subDid?: string | number;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 设备昵称
   */
  nickName?: string;

  /**
   * 产品key
   */
  productKey?: string;

  /**
   * 设备编号
   */
  serialNumber?: string;

  /**
   * 产品密钥
   */
  secret?: string;

  /**
   * 固件版本
   */
  firmwareVersion?: string;

  /**
   * 设备位置
   */
  locate?: string;

  /**
   * 设备标签
   */
  tag?: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status?: number;

  /**
   * 设备IP
   */
  ip?: string;

  /**
   * 激活时间
   */
  activeTime?: string;

  /**
   * 物模型值
   */
  thingsModel?: string;

  /**
   * 设备摘要
   */
  summary?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



