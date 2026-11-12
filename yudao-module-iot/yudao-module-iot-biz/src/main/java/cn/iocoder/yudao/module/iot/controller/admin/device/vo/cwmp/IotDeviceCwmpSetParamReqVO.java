package cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - IoT 设备 CWMP 参数设置 Request VO")
@Data
public class IotDeviceCwmpSetParamReqVO {

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "要修改的参数键值对 (Path -> Value)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "修改参数不能为空")
    private Map<String, String> params;

}
