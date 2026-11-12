package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IoT 设备 USP (TR-369) 配置 DO
 *
 * @author 芋道源码
 */
@TableName("iot_device_usp_config")
@KeySequence("iot_device_usp_config_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceUspConfigDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 产品编号
     */
    private Long productId;

    /**
     * USP Agent Endpoint ID
     */
    private String agentEndpointId;

    /**
     * USP Controller Endpoint ID
     */
    private String controllerEndpointId;

    /**
     * MTP 协议 (MQTT / WEBSOCKET)
     */
    private String mtpType;

    /**
     * 下发至 Agent 的 Topic
     */
    private String mqttTopicAgent;

    /**
     * Agent 上报 Controller 的 Topic
     */
    private String mqttTopicController;

    /**
     * USP 协议版本
     */
    private String protocolVersion;

    /**
     * 最后联系时间
     */
    private LocalDateTime lastContactTime;

}
