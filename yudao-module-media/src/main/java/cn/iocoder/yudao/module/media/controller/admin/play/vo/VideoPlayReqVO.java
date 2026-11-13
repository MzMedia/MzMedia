package cn.iocoder.yudao.module.media.controller.admin.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 视频实时点播拉流请求 VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频实时点播拉流 Request VO")
@Data
public class VideoPlayReqVO {

    @Schema(description = "通道数据库自增 ID (可选，与 channelCode 二选一)", example = "1001")
    private Long channelId;

    @Schema(description = "设备国标编号", example = "34020000001110000001")
    private String deviceId;

    @Schema(description = "通道国标编号", example = "34020000001320000001")
    private String channelCode;

    @Schema(description = "首选播放协议 (webrtc, flv, ws-flv, hls, rtsp, rtmp)", example = "webrtc")
    private String protocol;
}
