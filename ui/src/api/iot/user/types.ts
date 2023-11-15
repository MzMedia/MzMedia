export interface UserVO {
  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 用户昵称
   */
  userName: string;

  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 手机号码
   */
  phonenumber: string;

  /**
   * 是否为设备所有者（0=否，1=是）
   */
  isOwner: number;

  /**
   * 用户物模型权限，多个以英文逗号分隔
   */
  perms: string;

  /**
   * 备注
   */
  remark: string;

}

export interface UserForm extends BaseEntity {
  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 用户昵称
   */
  userName?: string;

  /**
   * 设备ID
   */
  deviceId?: string | number;

  /**
   * 手机号码
   */
  phonenumber?: string;

  /**
   * 是否为设备所有者（0=否，1=是）
   */
  isOwner?: number;

  /**
   * 用户物模型权限，多个以英文逗号分隔
   */
  perms?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface UserQuery extends PageQuery {

  /**
   * 用户昵称
   */
  userName?: string;

  /**
   * 手机号码
   */
  phonenumber?: string;

  /**
   * 是否为设备所有者（0=否，1=是）
   */
  isOwner?: number;

  /**
   * 用户物模型权限，多个以英文逗号分隔
   */
  perms?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



