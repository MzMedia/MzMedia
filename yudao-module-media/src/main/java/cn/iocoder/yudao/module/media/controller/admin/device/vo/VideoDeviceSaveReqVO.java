package cn.iocoder.yudao.module.media.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 视频设备新增/修改 Request VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频设备保存 Request VO")
@Data
public class VideoDeviceSaveReqVO {

    @Schema(description = "主键ID (更新时必填)", example = "101")
    private Long id;

    @Schema(description = "设备国标编码 (20位)", requiredMode = Schema.RequiredMode.REQUIRED, example = "34020000001110000001")
    @NotBlank(message = "设备国标编码不能为空")
    private String deviceId;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "东门高清球机")
    @NotBlank(message = "设备名称不能为空")
    private String name;

    @Schema(description = "自定义别名", example = "东门球机")
    private String customName;

    @Schema(description = "SIP认证密码", example = "12345678")
    private String password;

    @Schema(description = "字符集 (GB2312/UTF-8)", example = "GB2312")
    private String charset;

    @Schema(description = "信令传输模式 (UDP/TCP)", example = "UDP")
    private String transport;

    @Schema(description = "流传输模式", example = "UDP")
    private String streamMode;

    @Schema(description = "流媒体服务节点ID", example = "default")
    private String mediaServerId;
}
