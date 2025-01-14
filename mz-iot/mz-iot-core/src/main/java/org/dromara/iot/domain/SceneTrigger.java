package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 场景联动触发器对象 scene_trigger
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scene_trigger")
public class SceneTrigger extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 场景触发器ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 所属场景ID
     */
    private Long sceneId;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 告警状态（1-启动，2-停止）
     */
    private Long status;

    /**
     * 触发源（1=设备触发，2=定时触发）
     */
    private Long source;

    /**
     * 物模型标识符
     */
    private String modelId;

    /**
     * 物模型名称
     */
    private String name;

    /**
     * 物模型值
     */
    private String value;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
     */
    private Long type;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
