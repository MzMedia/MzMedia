package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.DeviceLog;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备日志业务对象 device_log
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceLog.class, reverseConvertGenerate = false)
public class DeviceLogBo extends BaseEntity {

    /**
     * 设备监测信息ID
     */
    @NotNull(message = "设备监测信息ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 标识符
     */
    @NotBlank(message = "标识符不能为空", groups = { AddGroup.class, EditGroup.class })
    private String identity;

    /**
     * 物模型名称
     */
    @NotBlank(message = "物模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelName;

    /**
     * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
     */
    @NotNull(message = "类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long logType;

    /**
     * 日志值
     */
    @NotBlank(message = "日志值不能为空", groups = { AddGroup.class, EditGroup.class })
    private String logValue;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
