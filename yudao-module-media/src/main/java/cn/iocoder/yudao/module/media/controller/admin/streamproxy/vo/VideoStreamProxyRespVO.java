package cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 拉流代理 Response VO")
@Data
public class VideoStreamProxyRespVO {

    @Schema(description = "主键 ID", example = "1024")
    private Long id;

    @Schema(description = "应用名", example = "live")
    private String app;

    @Schema(description = "流 ID", example = "camera-gate")
    private String stream;

    @Schema(description = "拉流描述名称", example = "北门球机")
    private String name;

    @Schema(description = "目标拉流源地址", example = "rtsp://192.168.1.200:554/live")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
