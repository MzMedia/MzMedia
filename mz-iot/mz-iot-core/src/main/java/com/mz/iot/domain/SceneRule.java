package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 场景联动对象 scene_rule
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scene_rule")
public class SceneRule extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    @TableId(value = "scene_id")
    private Long sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 执行动作
     */
    private String actions;

    /**
     * 场景状态（1-启动，2-停止）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;


}
