package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.SceneRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 场景联动业务对象 scene_rule
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SceneRule.class, reverseConvertGenerate = false)
public class SceneRuleBo extends BaseEntity {

    /**
     * 场景ID
     */
    @NotNull(message = "场景ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 场景名称
     */
    @NotBlank(message = "场景名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sceneName;

    /**
     * 执行动作
     */
    @NotBlank(message = "执行动作不能为空", groups = { AddGroup.class, EditGroup.class })
    private String actions;

    /**
     * 场景状态（1-启动，2-停止）
     */
    @NotNull(message = "场景状态（1-启动，2-停止）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
