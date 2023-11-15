export interface RuleVO {
  /**
   * 告警ID
   */
  alertId: string | number;

  /**
   * 告警名称
   */
  alertName: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel: number;

  /**
   * 所属场景ID
   */
  sceneId: string | number;

  /**
   * 备注
   */
  remark: string;

}

export interface RuleForm extends BaseEntity {
  /**
   * 告警ID
   */
  alertId?: string | number;

  /**
   * 告警名称
   */
  alertName?: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel?: number;

  /**
   * 所属场景ID
   */
  sceneId?: string | number;

  /**
   * 备注
   */
  remark?: string;

}

export interface RuleQuery extends PageQuery {

  /**
   * 告警名称
   */
  alertName?: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel?: number;

  /**
   * 所属场景ID
   */
  sceneId?: string | number;

    /**
     * 日期范围参数
     */
    params?: any;
}



