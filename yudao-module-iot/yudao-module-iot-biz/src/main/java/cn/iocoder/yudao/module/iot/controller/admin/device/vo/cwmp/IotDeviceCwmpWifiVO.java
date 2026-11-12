package cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - IoT 设备 CWMP Wi-Fi 配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceCwmpWifiVO {

    @Schema(description = "频段类型 (2.4G / 5G)", example = "2.4G")
    private String band;

    @Schema(description = "Wi-Fi 开关", example = "true")
    private Boolean enable;

    @Schema(description = "Wi-Fi SSID 名称", example = "Home_WiFi_2.4G")
    private String ssid;

    @Schema(description = "Wi-Fi 密码", example = "12345678")
    private String password;

    @Schema(description = "加密模式", example = "WPA2-PSK")
    private String securityMode;

    @Schema(description = "无线信道", example = "6")
    private Integer channel;

    @Schema(description = "是否隐藏 SSID", example = "false")
    private Boolean hidden;

    @Schema(description = "对应的 TR 数据模型路径", example = "InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.")
    private String path;

}
