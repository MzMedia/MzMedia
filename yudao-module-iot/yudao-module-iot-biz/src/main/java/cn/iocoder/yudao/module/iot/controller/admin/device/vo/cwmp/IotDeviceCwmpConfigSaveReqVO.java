package cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IoT 设备 CWMP (TR-069) 配置保存 Request VO")
@Data
public class IotDeviceCwmpConfigSaveReqVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "设备编号不能为空")
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

    @Schema(description = "数据模型类型 (TR098 / TR181)", example = "TR181")
    private String dataModelType;

}
