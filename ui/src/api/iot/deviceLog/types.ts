export interface DeviceLogVO {
  /**
   * 设备监测信息ID
   */
  id: string | number;

  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 标识符
   */
  identity: string | number;

  /**
   * 物模型名称
   */
  modelName: string;

  /**
   * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
   */
  logType: number;

  /**
   * 日志值
   */
  logValue: string;

  /**
   * 备注
   */
  remark: string;

}

export interface DeviceLogForm extends BaseEntity {
  /**
   * 设备监测信息ID
   */
  id?: string | number;

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 标识符
   */
  identity?: string | number;

  /**
   * 物模型名称
   */
  modelName?: string;

  /**
   * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
   */
  logType?: number;

  /**
   * 日志值
   */
  logValue?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface DeviceLogQuery extends PageQuery {

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 标识符
   */
  identity?: string | number;

  /**
   * 物模型名称
   */
  modelName?: string;

  /**
   * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
   */
  logType?: number;

  /**
   * 日志值
   */
  logValue?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



