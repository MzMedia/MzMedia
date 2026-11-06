export interface GroupVO {
  /**
   * 分组ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 分组名称
   */
  groupName: string;

  /**
   * 分组排序
   */
  groupOrder: number;

  /**
   * 备注
   */
  remark: string;

}

export interface GroupForm extends BaseEntity {
  /**
   * 分组ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 分组名称
   */
  groupName?: string;

  /**
   * 分组排序
   */
  groupOrder?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface GroupQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 分组名称
   */
  groupName?: string;

  /**
   * 分组排序
   */
  groupOrder?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



