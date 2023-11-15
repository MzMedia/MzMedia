export interface CategoryVO {
  /**
   * 产品分类ID
   */
  categoryId: string | number;

  /**
   * 产品分类名称
   */
  categoryName: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys: number;

  /**
   * 父级ID
   */
  parentId: string | number;

  /**
   * 显示顺序
   */
  orderNum: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CategoryForm extends BaseEntity {
  /**
   * 产品分类ID
   */
  categoryId?: string | number;

  /**
   * 产品分类名称
   */
  categoryName?: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys?: number;

  /**
   * 父级ID
   */
  parentId?: string | number;

  /**
   * 显示顺序
   */
  orderNum?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CategoryQuery extends PageQuery {

  /**
   * 产品分类名称
   */
  categoryName?: string;

  /**
   * 是否系统通用（0-否，1-是）
   */
  isSys?: number;

  /**
   * 父级ID
   */
  parentId?: string | number;

  /**
   * 显示顺序
   */
  orderNum?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



