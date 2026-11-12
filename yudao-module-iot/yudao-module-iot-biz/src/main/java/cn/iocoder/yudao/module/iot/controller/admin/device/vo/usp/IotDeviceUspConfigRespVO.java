package cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 设备 USP (TR-369) 配置 Response VO")
@Data
public class IotDeviceUspConfigRespVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "设备编号", example = "2048")
    private Long deviceId;

    @Schema(description = "产品编号", example = "4096")
    private Long productId;

    @Schema(description = "USP Agent Endpoint ID", example = "proto::agent-001")
    private String agentEndpointId;

    @Schema(description = "USP Controller Endpoint ID", example = "proto::mzmedia-controller")
    private String controllerEndpointId;

    @Schema(description = "MTP 协议 (MQTT / WEBSOCKET)", example = "MQTT")
    private String mtpType;

    @Schema(description = "下发至 Agent 的 Topic")
    private String mqttTopicAgent;

    @Schema(description = "Agent 上报 Controller 的 Topic")
    private String mqttTopicController;

    @Schema(description = "USP 协议版本", example = "1.3")
    private String protocolVersion;

    @Schema(description = "最后联系时间")
    private LocalDateTime lastContactTime;

}
