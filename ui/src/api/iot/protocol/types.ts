export interface ProtocolVO {
  /**
   * ID
   */
  id: string | number;

  /**
   * 协议名称
   */
  name: string;

  /**
   * jar包文件
   */
  jarFile: string;

  /**
   * 编码脚本文件
   */
  codecFile: string;

  /**
   * 协议网络
   */
  network: string;

  /**
   * 协议配置
   */
  config: string;

  /**
   * 编解码器ID
   */
  codecId: string | number;

  /**
   * 编解码类型
   */
  codecType: string;

  /**
   * 脚本内容
   */
  script: string;

  /**
   * 脚本语言
   */
  scriptLang: string;

  /**
   * 协议运行状态
   */
  state: string;

  /**
   * 备注
   */
  remark: string;

}

export interface ProtocolForm extends BaseEntity {
  /**
   * ID
   */
  id?: string | number;

  /**
   * 协议名称
   */
  name?: string;

  /**
   * jar包文件
   */
  jarFile?: string;

  /**
   * 编码脚本文件
   */
  codecFile?: string;

  /**
   * 协议网络
   */
  network?: string;

  /**
   * 协议配置
   */
  config?: string;

  /**
   * 编解码器ID
   */
  codecId?: string | number;

  /**
   * 编解码类型
   */
  codecType?: string;

  /**
   * 脚本内容
   */
  script?: string;

  /**
   * 脚本语言
   */
  scriptLang?: string;

  /**
   * 协议运行状态
   */
  state?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface ProtocolQuery extends PageQuery {

  /**
   * 协议名称
   */
  name?: string;

  /**
   * jar包文件
   */
  jarFile?: string;

  /**
   * 编码脚本文件
   */
  codecFile?: string;

  /**
   * 协议网络
   */
  network?: string;

  /**
   * 协议配置
   */
  config?: string;

  /**
   * 编解码器ID
   */
  codecId?: string | number;

  /**
   * 编解码类型
   */
  codecType?: string;

  /**
   * 脚本内容
   */
  script?: string;

  /**
   * 脚本语言
   */
  scriptLang?: string;

  /**
   * 协议运行状态
   */
  state?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



