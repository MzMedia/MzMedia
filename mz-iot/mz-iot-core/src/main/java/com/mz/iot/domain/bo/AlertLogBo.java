package com.mz.iot.domain.bo;

import com.mz.iot.domain.AlertLog;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备告警日志业务对象 alert_log
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AlertLog.class, reverseConvertGenerate = false)
public class AlertLogBo extends BaseEntity {

    /**
     * 告警日志ID
     */
    @NotNull(message = "告警日志ID不能为空", groups = { EditGroup.class })
    private Long alertLogId;

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
     * 告警详情（对应物模型）
     */
    @NotBlank(message = "告警详情（对应物模型）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String detail;

    /**
     * 处理状态(1=不需要处理,2=未处理,3=已处理)
     */
    @NotNull(message = "处理状态(1=不需要处理,2=未处理,3=已处理)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
