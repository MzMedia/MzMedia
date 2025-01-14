export interface ThingsModelVO {
  /**
   * 物模型ID
   */
  id: string | number;

  /**
   * 物模型名称
   */
  modelName: string;

  /**
   * 产品ID
   */
  productId: string | number;

  /**
   * 标识符，产品下唯一
   */
  identifier: string | number;

  /**
   * 模型类别（1-属性，2-功能，3-事件）
   */
  type: number;

  /**
   * 数据类型（integer、decimal、string、bool、array、enum）
   */
  datatype: string;

  /**
   * 数据定义
   */
  specs: string;

  /**
   * 是否图表展示（0-否，1-是）
   */
  isChart: number;

  /**
   * 是否实时监测（0-否，1-是）
   */
  isMonitor: number;

  /**
   * 是否历史存储（0-否，1-是）
   */
  isHistory: number;

  /**
   * 是否只读数据(0-否，1-是)
   */
  isReadonly: number;

  /**
   * 是否设备分享权限(0-否，1-是)
   */
  isSharePerm: number;

  /**
   * 排序，值越大，排序越靠前
   */
  modelOrder: number;

  /**
   * 备注
   */
  remark: string;

}

export interface ThingsModelForm extends BaseEntity {
  /**
   * 物模型ID
   */
  id?: string | number;

  /**
   * 物模型名称
   */
  modelName?: string;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 标识符，产品下唯一
   */
  identifier?: string | number;

  /**
   * 模型类别（1-属性，2-功能，3-事件）
   */
  type?: number;

  /**
   * 数据类型（integer、decimal、string、bool、array、enum）
   */
  datatype?: string;

  /**
   * 数据定义
   */
  specs?: string;

  /**
   * 是否图表展示（0-否，1-是）
   */
  isChart?: number;

  /**
   * 是否实时监测（0-否，1-是）
   */
  isMonitor?: number;

  /**
   * 是否历史存储（0-否，1-是）
   */
  isHistory?: number;

  /**
   * 是否只读数据(0-否，1-是)
   */
  isReadonly?: number;

  /**
   * 是否设备分享权限(0-否，1-是)
   */
  isSharePerm?: number;

  /**
   * 排序，值越大，排序越靠前
   */
  modelOrder?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface ThingsModelQuery extends PageQuery {

  /**
   * 物模型名称
   */
  modelName?: string;

  /**
   * 产品ID
   */
  productId?: string | number;

  /**
   * 标识符，产品下唯一
   */
  identifier?: string | number;

  /**
   * 模型类别（1-属性，2-功能，3-事件）
   */
  type?: number;

  /**
   * 数据类型（integer、decimal、string、bool、array、enum）
   */
  datatype?: string;

  /**
   * 数据定义
   */
  specs?: string;

  /**
   * 是否图表展示（0-否，1-是）
   */
  isChart?: number;

  /**
   * 是否实时监测（0-否，1-是）
   */
  isMonitor?: number;

  /**
   * 是否历史存储（0-否，1-是）
   */
  isHistory?: number;

  /**
   * 是否只读数据(0-否，1-是)
   */
  isReadonly?: number;

  /**
   * 是否设备分享权限(0-否，1-是)
   */
  isSharePerm?: number;

  /**
   * 排序，值越大，排序越靠前
   */
  modelOrder?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



