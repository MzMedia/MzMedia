export interface DeviceChannelVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 产品名称
   */
  productName: string;

  /**
   * 设备SipID
   */
  deviceSipId: string | number;

  /**
   * 通道SipID
   */
  channelSipId: string | number;

  /**
   * 通道名称
   */
  channelName: string;

  /**
   * 注册时间
   */
  registerTime: string;

  /**
   * 设备类型
   */
  deviceType: string;

  /**
   * 通道类型
   */
  channelType: string;

  /**
   * 城市编码
   */
  cityCode: string;

  /**
   * 行政区域
   */
  civilCode: string;

  /**
   * 厂商名称
   */
  manufacture: string;

  /**
   * 产品型号
   */
  model: string;

  /**
   * 设备归属
   */
  owner: string;

  /**
   * 警区
   */
  block: string;

  /**
   * 安装地址
   */
  address: string;

  /**
   * 父级id
   */
  parentId: string | number;

  /**
   * 设备入网IP
   */
  ipAddress: string;

  /**
   * 设备接入端口号
   */
  port: number;

  /**
   * 密码
   */
  password: string;

  /**
   * PTZ类型
   */
  PTZType: number;

  /**
   * PTZ类型描述字符串
   */
  PTZTypeText: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status: number;

  /**
   * 设备经度
   */
  longitude: number;

  /**
   * 设备纬度
   */
  latitude: number;

  /**
   * 流媒体ID
   */
  streamId: string | number;

  /**
   * 子设备数
   */
  subCount: number;

  /**
   * 是否有子设备（1-有, 0-没有）
   */
  parental: number;

  /**
   * 是否含有音频（1-有, 0-没有）
   */
  hasAudio: number;

  /**
   * 备注
   */
  remark: string;

}

export interface DeviceChannelForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 产品名称
   */
  productName?: string;

  /**
   * 设备SipID
   */
  deviceSipId?: string | number;

  /**
   * 通道SipID
   */
  channelSipId?: string | number;

  /**
   * 通道名称
   */
  channelName?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * 通道类型
   */
  channelType?: string;

  /**
   * 城市编码
   */
  cityCode?: string;

  /**
   * 行政区域
   */
  civilCode?: string;

  /**
   * 厂商名称
   */
  manufacture?: string;

  /**
   * 产品型号
   */
  model?: string;

  /**
   * 设备归属
   */
  owner?: string;

  /**
   * 警区
   */
  block?: string;

  /**
   * 安装地址
   */
  address?: string;

  /**
   * 父级id
   */
  parentId?: string | number;

  /**
   * 设备入网IP
   */
  ipAddress?: string;

  /**
   * 设备接入端口号
   */
  port?: number;

  /**
   * 密码
   */
  password?: string;

  /**
   * PTZ类型
   */
  PTZType?: number;

  /**
   * PTZ类型描述字符串
   */
  PTZTypeText?: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status?: number;

  /**
   * 设备经度
   */
  longitude?: number;

  /**
   * 设备纬度
   */
  latitude?: number;

  /**
   * 流媒体ID
   */
  streamId?: string | number;

  /**
   * 子设备数
   */
  subCount?: number;

  /**
   * 是否有子设备（1-有, 0-没有）
   */
  parental?: number;

  /**
   * 是否含有音频（1-有, 0-没有）
   */
  hasAudio?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface DeviceChannelQuery extends PageQuery {

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 产品名称
   */
  productName?: string;

  /**
   * 通道SipID
   */
  channelSipId?: string | number;

  /**
   * 通道名称
   */
  channelName?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * 通道类型
   */
  channelType?: string;

  /**
   * 城市编码
   */
  cityCode?: string;

  /**
   * 行政区域
   */
  civilCode?: string;

  /**
   * 厂商名称
   */
  manufacture?: string;

  /**
   * 产品型号
   */
  model?: string;

  /**
   * 设备归属
   */
  owner?: string;

  /**
   * 警区
   */
  block?: string;

  /**
   * 安装地址
   */
  address?: string;

  /**
   * 父级id
   */
  parentId?: string | number;

  /**
   * 设备入网IP
   */
  ipAddress?: string;

  /**
   * 设备接入端口号
   */
  port?: number;

  /**
   * 密码
   */
  password?: string;

  /**
   * PTZ类型
   */
  PTZType?: number;

  /**
   * PTZ类型描述字符串
   */
  PTZTypeText?: string;

  /**
   * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
   */
  status?: number;

  /**
   * 设备经度
   */
  longitude?: number;

  /**
   * 设备纬度
   */
  latitude?: number;

  /**
   * 流媒体ID
   */
  streamId?: string | number;

  /**
   * 子设备数
   */
  subCount?: number;

  /**
   * 是否有子设备（1-有, 0-没有）
   */
  parental?: number;

  /**
   * 是否含有音频（1-有, 0-没有）
   */
  hasAudio?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



