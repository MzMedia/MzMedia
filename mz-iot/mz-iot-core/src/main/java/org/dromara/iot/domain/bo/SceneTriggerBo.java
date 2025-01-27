package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.SceneTrigger;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 场景联动触发器业务对象 scene_trigger
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SceneTrigger.class, reverseConvertGenerate = false)
public class SceneTriggerBo extends BaseEntity {

    /**
     * 场景触发器ID
     */
    @NotNull(message = "场景触发器ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 所属场景ID
     */
    @NotNull(message = "所属场景ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sceneId;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 告警状态（1-启动，2-停止）
     */
    @NotNull(message = "告警状态（1-启动，2-停止）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 触发源（1=设备触发，2=定时触发）
     */
    @NotNull(message = "触发源（1=设备触发，2=定时触发）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long source;

    /**
     * 物模型标识符
     */
    @NotBlank(message = "物模型标识符不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 物模型名称
     */
    @NotBlank(message = "物模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 物模型值
     */
    @NotBlank(message = "物模型值不能为空", groups = { AddGroup.class, EditGroup.class })
    private String value;

    /**
     * 操作符
     */
    @NotBlank(message = "操作符不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operator;

    /**
     * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
     */
    @NotNull(message = "物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
