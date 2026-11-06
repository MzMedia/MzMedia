export interface SipConfigVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 使能开关
   */
  enabled: number;

  /**
   * 系统默认配置
   */
  isdefault: number;

  /**
   * 拓展sdp
   */
  seniorSdp: number;

  /**
   * 服务器域
   */
  domain: string;

  /**
   * 服务器sipid
   */
  sipid: string | number;

  /**
   * sip认证密码
   */
  password: string;

  /**
   * sip接入IP
   */
  ip: string;

  /**
   * sip接入端口号
   */
  port: number;

  /**
   * 备注
   */
  remark: string;

}

export interface SipConfigForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 使能开关
   */
  enabled?: number;

  /**
   * 系统默认配置
   */
  isdefault?: number;

  /**
   * 拓展sdp
   */
  seniorSdp?: number;

  /**
   * 服务器域
   */
  domain?: string;

  /**
   * 服务器sipid
   */
  sipid?: string | number;

  /**
   * sip认证密码
   */
  password?: string;

  /**
   * sip接入IP
   */
  ip?: string;

  /**
   * sip接入端口号
   */
  port?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface SipConfigQuery extends PageQuery {

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 使能开关
   */
  enabled?: number;

  /**
   * 系统默认配置
   */
  isdefault?: number;

  /**
   * 拓展sdp
   */
  seniorSdp?: number;

  /**
   * 服务器域
   */
  domain?: string;

  /**
   * 服务器sipid
   */
  sipid?: string | number;

  /**
   * sip认证密码
   */
  password?: string;

  /**
   * sip接入IP
   */
  ip?: string;

  /**
   * sip接入端口号
   */
  port?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



