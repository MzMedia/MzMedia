export interface AlertRuleVO {
  /**
   * 告警ID
   */
  id: string | number;

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

export interface AlertRuleForm extends BaseEntity {
  /**
   * 告警ID
   */
  id?: string | number;

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

export interface AlertRuleQuery extends PageQuery {

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



