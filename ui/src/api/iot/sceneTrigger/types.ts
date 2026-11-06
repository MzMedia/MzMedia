export interface SceneTriggerVO {
  /**
   * 场景触发器ID
   */
  id: string | number;

  /**
   * 所属场景ID
   */
  sceneId: string | number;

  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 告警状态（1-启动，2-停止）
   */
  status: number;

  /**
   * 触发源（1=设备触发，2=定时触发）
   */
  source: number;

  /**
   * 物模型标识符
   */
  modelId: string | number;

  /**
   * 物模型名称
   */
  name: string;

  /**
   * 物模型值
   */
  value: string;

  /**
   * 操作符
   */
  operator: string;

  /**
   * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
   */
  type: number;

  /**
   * 备注
   */
  remark: string;

}

export interface SceneTriggerForm extends BaseEntity {
  /**
   * 场景触发器ID
   */
  id?: string | number;

  /**
   * 所属场景ID
   */
  sceneId?: string | number;

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 告警状态（1-启动，2-停止）
   */
  status?: number;

  /**
   * 触发源（1=设备触发，2=定时触发）
   */
  source?: number;

  /**
   * 物模型标识符
   */
  modelId?: string | number;

  /**
   * 物模型名称
   */
  name?: string;

  /**
   * 物模型值
   */
  value?: string;

  /**
   * 操作符
   */
  operator?: string;

  /**
   * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
   */
  type?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface SceneTriggerQuery extends PageQuery {

  /**
   * 所属场景ID
   */
  sceneId?: string | number;

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 告警状态（1-启动，2-停止）
   */
  status?: number;

  /**
   * 触发源（1=设备触发，2=定时触发）
   */
  source?: number;

  /**
   * 物模型标识符
   */
  modelId?: string | number;

  /**
   * 物模型名称
   */
  name?: string;

  /**
   * 物模型值
   */
  value?: string;

  /**
   * 操作符
   */
  operator?: string;

  /**
   * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
   */
  type?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



