package cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IoT 设备 CWMP 网络 Ping 诊断 Request VO")
@Data
public class IotDeviceCwmpPingReqVO {

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "目标主机或 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "8.8.8.8")
    @NotEmpty(message = "目标主机不能为空")
    private String host;

    @Schema(description = "Ping 次数", example = "4")
    private Integer numberOfRepetitions = 4;

}
