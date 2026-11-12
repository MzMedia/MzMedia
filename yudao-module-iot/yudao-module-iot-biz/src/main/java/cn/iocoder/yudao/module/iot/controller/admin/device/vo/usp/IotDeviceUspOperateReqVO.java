package cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - IoT 设备 USP 操作命令执行 Request VO")
@Data
public class IotDeviceUspOperateReqVO {

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "USP 命令路径 (如 Device.Reboot())", requiredMode = Schema.RequiredMode.REQUIRED, example = "Device.Reboot()")
    @NotEmpty(message = "命令路径不能为空")
    private String command;

    @Schema(description = "命令入参 (Key -> Value)")
    private Map<String, String> inputArgs;

}
