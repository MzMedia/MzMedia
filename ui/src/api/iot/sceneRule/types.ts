export interface SceneRuleVO {
  /**
   * 场景ID
   */
  id: string | number;

  /**
   * 场景名称
   */
  sceneName: string;

  /**
   * 执行动作
   */
  actions: string;

  /**
   * 场景状态（1-启动，2-停止）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface SceneRuleForm extends BaseEntity {
  /**
   * 场景ID
   */
  id?: string | number;

  /**
   * 场景名称
   */
  sceneName?: string;

  /**
   * 执行动作
   */
  actions?: string;

  /**
   * 场景状态（1-启动，2-停止）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface SceneRuleQuery extends PageQuery {

  /**
   * 场景名称
   */
  sceneName?: string;

  /**
   * 执行动作
   */
  actions?: string;

  /**
   * 场景状态（1-启动，2-停止）
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



