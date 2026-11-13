package cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 拉流代理创建/修改 Request VO")
@Data
public class VideoStreamProxySaveReqVO {

    @Schema(description = "主键 ID (新增时不填)", example = "1024")
    private Long id;

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "live")
    @NotBlank(message = "应用名不能为空")
    private String app;

    @Schema(description = "流 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "camera-gate")
    @NotBlank(message = "流 ID 不能为空")
    private String stream;

    @Schema(description = "拉流描述名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "北门球机")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "目标拉流源地址 (RTSP/RTMP/FLV)", requiredMode = Schema.RequiredMode.REQUIRED, example = "rtsp://192.168.1.200:554/live")
    @NotBlank(message = "拉流地址不能为空")
    private String url;

    @Schema(description = "源地址备份", example = "rtsp://192.168.1.201:554/live")
    private String srcUrl;

    @Schema(description = "超时时间 (毫秒)", example = "15000")
    private Integer timeoutMs;

    @Schema(description = "绑定的流媒体服务器 ID", example = "zlm-01")
    private String mediaServerId;

    @Schema(description = "是否启用 (0未启用 1启用)", example = "1")
    private Integer enable;

    @Schema(description = "拉流类型 (0直接拉流 1按需拉流)", example = "0")
    private Integer pullType;
}
