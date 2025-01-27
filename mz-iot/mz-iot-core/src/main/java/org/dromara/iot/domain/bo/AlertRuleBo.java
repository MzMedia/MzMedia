package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.AlertRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备告警规则业务对象 alert_rule
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AlertRule.class, reverseConvertGenerate = false)
public class AlertRuleBo extends BaseEntity {

    /**
     * 告警ID
     */
    @NotNull(message = "告警ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 告警名称
     */
    @NotBlank(message = "告警名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String alertName;

    /**
     * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
     */
    @NotNull(message = "告警级别（1=提醒通知，2=轻微问题，3=严重警告）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long alertLevel;

    /**
     * 所属场景ID
     */
    @NotNull(message = "所属场景ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sceneId;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
