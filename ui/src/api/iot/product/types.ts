export interface ProductVO {
  /**
   * 产品ID
   */
  id: string | number;

  /**
   * 产品分类ID
   */
  categoryId: string | number;

  /**
   * 产品名称
   */
  productName: string;

  /**
   * 协议编号
   */
  protocolCode: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys: number;

  /**
   * 是否启用授权码（0-否，1-是）
   */
  isAuthorize: number;

  /**
   * mqtt账号
   */
  mqttAccount: string;

  /**
   * mqtt密码
   */
  mqttPassword: string;

  /**
   * 产品秘钥
   */
  mqttSecret: string;

  /**
   * 状态（1-未发布，2-已发布）
   */
  status: number;

  /**
   * 物模型JSON（属性、功能、事件）
   */
  thingsModelsJson: string;

  /**
   * 设备类型（1-直连设备、2-网关设备、3-监控设备）
   */
  deviceType: number;

  /**
   * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
   */
  networkMethod: number;

  /**
   * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
   */
  vertificateMethod: number;

  /**
   * 图片地址
   */
  imgUrl: string;

  /**
   * 备注
   */
  remark: string;

}

export interface ProductForm extends BaseEntity {
  /**
   * 产品ID
   */
  id?: string | number;

  /**
   * 产品分类ID
   */
  categoryId?: string | number;

  /**
   * 产品名称
   */
  productName?: string;

  /**
   * 协议编号
   */
  protocolCode?: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys?: number;

  /**
   * 是否启用授权码（0-否，1-是）
   */
  isAuthorize?: number;

  /**
   * mqtt账号
   */
  mqttAccount?: string;

  /**
   * mqtt密码
   */
  mqttPassword?: string;

  /**
   * 产品秘钥
   */
  mqttSecret?: string;

  /**
   * 状态（1-未发布，2-已发布）
   */
  status?: number;

  /**
   * 物模型JSON（属性、功能、事件）
   */
  thingsModelsJson?: string;

  /**
   * 设备类型（1-直连设备、2-网关设备、3-监控设备）
   */
  deviceType?: number;

  /**
   * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
   */
  networkMethod?: number;

  /**
   * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
   */
  vertificateMethod?: number;

  /**
   * 图片地址
   */
  imgUrl?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface ProductQuery extends PageQuery {

  /**
   * 产品分类ID
   */
  categoryId?: string | number;

  /**
   * 产品名称
   */
  productName?: string;

  /**
   * 协议编号
   */
  protocolCode?: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys?: number;

  /**
   * 是否启用授权码（0-否，1-是）
   */
  isAuthorize?: number;

  /**
   * mqtt账号
   */
  mqttAccount?: string;

  /**
   * mqtt密码
   */
  mqttPassword?: string;

  /**
   * 产品秘钥
   */
  mqttSecret?: string;

  /**
   * 状态（1-未发布，2-已发布）
   */
  status?: number;

  /**
   * 物模型JSON（属性、功能、事件）
   */
  thingsModelsJson?: string;

  /**
   * 设备类型（1-直连设备、2-网关设备、3-监控设备）
   */
  deviceType?: number;

  /**
   * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
   */
  networkMethod?: number;

  /**
   * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
   */
  vertificateMethod?: number;

  /**
   * 图片地址
   */
  imgUrl?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



