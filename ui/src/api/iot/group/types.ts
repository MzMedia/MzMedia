export interface GroupVO {
  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 分组ID
   */
  groupId: string | number;

}

export interface GroupForm extends BaseEntity {
  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 分组ID
   */
  groupId?: string | number;

}

export interface GroupQuery extends PageQuery {

    /**
     * 日期范围参数
     */
    params?: any;
}



