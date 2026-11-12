package cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 设备 CWMP (TR-069) 配置 Response VO")
@Data
public class IotDeviceCwmpConfigRespVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "设备编号", example = "2048")
    private Long deviceId;

    @Schema(description = "产品编号", example = "4096")
    private Long productId;

    @Schema(description = "CPE 连接 ACS 的用户名", example = "acs")
    private String acsUsername;

    @Schema(description = "CPE 连接 ACS 的密码")
    private String acsPassword;

    @Schema(description = "CPE ConnectionRequestURL", example = "http://192.168.1.1:7547/ConnectionRequest-123")
    private String connectionRequestUrl;

    @Schema(description = "ACS 发起连接请求的用户名")
    private String connectionRequestUsername;

    @Schema(description = "ACS 发起连接请求的密码")
    private String connectionRequestPassword;

    @Schema(description = "是否开启周期上报")
    private Boolean periodicInformEnable;

    @Schema(description = "周期上报间隔（秒）", example = "30")
    private Integer periodicInformInterval;

    @Schema(description = "最后一次 Inform 时间")
    private LocalDateTime lastInformTime;

    @Schema(description = "数据模型类型 (TR098 / TR181)", example = "TR181")
    private String dataModelType;

    @Schema(description = "硬件版本", example = "HW_V2.0")
    private String hardwareVersion;

    @Schema(description = "软件版本", example = "SW_V1.0.8")
    private String softwareVersion;

    @Schema(description = "业务配置码")
    private String provisioningCode;

}
