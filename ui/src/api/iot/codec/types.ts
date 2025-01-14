export interface CodecVO {
  /**
   * ID
   */
  id: string | number;

  /**
   * 协议编解码名称
   */
  name: string;

  /**
   * 协议编解码描述
   */
  describe: string;

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
   * 备注
   */
  remark: string;

}

export interface CodecForm extends BaseEntity {
  /**
   * ID
   */
  id?: string | number;

  /**
   * 协议编解码名称
   */
  name?: string;

  /**
   * 协议编解码描述
   */
  describe?: string;

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
   * 备注
   */
  remark?: string;

}

export interface CodecQuery extends PageQuery {

  /**
   * 协议编解码名称
   */
  name?: string;

  /**
   * 协议编解码描述
   */
  describe?: string;

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
     * 日期范围参数
     */
    params?: any;
}



