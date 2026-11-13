package cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流媒体节点 Response VO")
@Data
public class VideoMediaServerRespVO {

    @Schema(description = "节点 ID", example = "zlm-01")
    private String id;

    @Schema(description = "节点 IP", example = "127.0.0.1")
    private String ip;

    @Schema(description = "Hook 回调宿主 IP", example = "127.0.0.1")
    private String hookIp;

    @Schema(description = "SDP 协商 IP", example = "127.0.0.1")
    private String sdpIp;

    @Schema(description = "推拉流 IP", example = "127.0.0.1")
    private String streamIp;

    @Schema(description = "HTTP 端口", example = "80")
    private Integer httpPort;

    @Schema(description = "HTTPS 端口", example = "443")
    private Integer httpSslPort;

    @Schema(description = "RTMP 端口", example = "1935")
    private Integer rtmpPort;

    @Schema(description = "RTSP 端口", example = "554")
    private Integer rtspPort;

    @Schema(description = "在线状态 (0离线 1在线)", example = "1")
    private Integer status;

    @Schema(description = "是否为默认流媒体服务 (0否 1是)", example = "1")
    private Integer defaultServer;

    @Schema(description = "最后心跳时间")
    private LocalDateTime lastKeepaliveTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
