package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备日志对象 device_log
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_log")
public class DeviceLog extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备监测信息ID
     */
    @TableId(value = "log_id")
    private Long logId;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 标识符
     */
    private String identity;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
     */
    private Integer logType;

    /**
     * 日志值
     */
    private String logValue;

    /**
     * 备注
     */
    private String remark;


}
