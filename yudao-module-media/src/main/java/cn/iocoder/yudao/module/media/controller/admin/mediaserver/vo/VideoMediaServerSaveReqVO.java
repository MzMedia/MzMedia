package cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 流媒体节点保存 Request VO")
@Data
public class VideoMediaServerSaveReqVO {

    @Schema(description = "节点 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "zlm-01")
    @NotBlank(message = "节点 ID 不能为空")
    private String id;

    @Schema(description = "节点 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotBlank(message = "节点 IP 不能为空")
    private String ip;

    @Schema(description = "Hook 回调宿主 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotBlank(message = "Hook IP 不能为空")
    private String hookIp;

    @Schema(description = "SDP 协商 IP", example = "127.0.0.1")
    private String sdpIp;

    @Schema(description = "推拉流 IP", example = "127.0.0.1")
    private String streamIp;

    @Schema(description = "HTTP 端口", requiredMode = Schema.RequiredMode.REQUIRED, example = "80")
    @NotNull(message = "HTTP 端口不能为空")
    private Integer httpPort;

    @Schema(description = "HTTPS 端口", example = "443")
    private Integer httpSslPort;

    @Schema(description = "RTMP 端口", example = "1935")
    private Integer rtmpPort;

    @Schema(description = "RTSP 端口", example = "554")
    private Integer rtspPort;

    @Schema(description = "RTP 收流端口范围", example = "30000,30500")
    private String rtpPortRange;

    @Schema(description = "ZLM Secret 鉴权秘钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "035c73f7-bb6b-4889-a715-d9eb2d1925cc")
    @NotBlank(message = "Secret 不能为空")
    private String secret;

    @Schema(description = "是否为默认流媒体服务 (0否 1是)", example = "1")
    private Integer defaultServer;

    @Schema(description = "是否为自动负载节点 (0否 1是)", example = "1")
    private Integer autoConfig;
}
