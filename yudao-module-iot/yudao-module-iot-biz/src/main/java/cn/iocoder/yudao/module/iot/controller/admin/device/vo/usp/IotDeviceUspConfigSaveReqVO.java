package cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IoT 设备 USP (TR-369) 配置保存 Request VO")
@Data
public class IotDeviceUspConfigSaveReqVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "产品编号", example = "4096")
    private Long productId;

    @Schema(description = "USP Agent Endpoint ID", example = "proto::agent-001")
    private String agentEndpointId;

    @Schema(description = "USP Controller Endpoint ID", example = "proto::mzmedia-controller")
    private String controllerEndpointId;

    @Schema(description = "MTP 协议 (MQTT / WEBSOCKET)", example = "MQTT")
    private String mtpType;

}
