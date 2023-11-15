export interface LogVO {
  /**
   * 告警日志ID
   */
  alertLogId: string | number;

  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 告警名称
   */
  alertName: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel: number;

  /**
   * 告警详情（对应物模型）
   */
  detail: string;

  /**
   * 处理状态(1=不需要处理,2=未处理,3=已处理)
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface LogForm extends BaseEntity {
  /**
   * 告警日志ID
   */
  alertLogId?: string | number;

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 告警名称
   */
  alertName?: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel?: number;

  /**
   * 告警详情（对应物模型）
   */
  detail?: string;

  /**
   * 处理状态(1=不需要处理,2=未处理,3=已处理)
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface LogQuery extends PageQuery {

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 告警名称
   */
  alertName?: string;

  /**
   * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
   */
  alertLevel?: number;

  /**
   * 告警详情（对应物模型）
   */
  detail?: string;

  /**
   * 处理状态(1=不需要处理,2=未处理,3=已处理)
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



