package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备告警日志对象 alert_log
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alert_log")
public class AlertLog extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警日志ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 告警名称
     */
    private String alertName;

    /**
     * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
     */
    private Long alertLevel;

    /**
     * 告警详情（对应物模型）
     */
    private String detail;

    /**
     * 处理状态(1=不需要处理,2=未处理,3=已处理)
     */
    private Long status;

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
