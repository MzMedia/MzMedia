export interface DeviceGroupVO {
  /**
   * 分组ID
   */
  groupId: string | number;

  /**
   * 设备ID
   */
  deviceId: string | number;

}

export interface DeviceGroupForm extends BaseEntity {
  /**
   * 分组ID
   */
  groupId?: string | number;

  /**
   * 设备ID
   */
  deviceId?: string | number;

}

export interface DeviceGroupQuery extends PageQuery {

    /**
     * 日期范围参数
     */
    params?: any;
}



