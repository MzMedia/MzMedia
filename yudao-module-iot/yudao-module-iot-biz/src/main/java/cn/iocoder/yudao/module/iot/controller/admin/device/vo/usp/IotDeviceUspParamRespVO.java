package cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 设备 USP 参数快照 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceUspParamRespVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "USP 参数/对象路径", example = "Device.WiFi.SSID.1.SSID")
    private String path;

    @Schema(description = "参数当前值", example = "WiFi_Office")
    private String value;

    @Schema(description = "数据类型", example = "string")
    private String type;

    @Schema(description = "是否可写 (0-只读 1-可写)")
    private Boolean writable;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

}
