package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备告警规则对象 alert_rule
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alert_rule")
public class AlertRule extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @TableId(value = "alert_id")
    private Long alertId;

    /**
     * 告警名称
     */
    private String alertName;

    /**
     * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
     */
    private Long alertLevel;

    /**
     * 所属场景ID
     */
    private Long sceneId;

    /**
     * 备注
     */
    private String remark;


}
