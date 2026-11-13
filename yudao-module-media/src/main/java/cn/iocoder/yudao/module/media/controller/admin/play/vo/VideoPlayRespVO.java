package cn.iocoder.yudao.module.media.controller.admin.play.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频实时点播播放地址集合 Response VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频实时点播播放地址 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoPlayRespVO {

    @Schema(description = "流唯一标识 ID", example = "34020000001320000001_34020000001320000001")
    private String streamId;

    @Schema(description = "应用标识", example = "live")
    private String app;

    @Schema(description = "媒体服务器节点标识", example = "default")
    private String mediaServerId;

    @Schema(description = "WebRTC 极低延迟播放地址 (首选推荐)", example = "webrtc://127.0.0.1/live/stream1")
    private String webrtc;

    @Schema(description = "WebRTC 播放地址别名 (兼容旧前端)", example = "webrtc://127.0.0.1/live/stream1")
    private String rtc;

    @Schema(description = "HTTP-FLV 播放地址", example = "http://127.0.0.1:80/live/stream1.live.flv")
    private String flv;

    @Schema(description = "WebSocket-FLV 播放地址", example = "ws://127.0.0.1:80/live/stream1.live.flv")
    private String wsFlv;

    @Schema(description = "WebSocket-FLV 播放地址别名 (兼容旧前端)", example = "ws://127.0.0.1:80/live/stream1.live.flv")
    @JsonProperty("ws_flv")
    private String ws_flv;

    @Schema(description = "HLS (m3u8) 兼容播放地址", example = "http://127.0.0.1:80/live/stream1/hls.m3u8")
    private String hls;

    @Schema(description = "RTSP 原始流地址", example = "rtsp://127.0.0.1:554/live/stream1")
    private String rtsp;

    @Schema(description = "RTMP 传统推流地址", example = "rtmp://127.0.0.1:1935/live/stream1")
    private String rtmp;

    public String getRtc() {
        return rtc != null ? rtc : webrtc;
    }

    public String getWs_flv() {
        return ws_flv != null ? ws_flv : wsFlv;
    }
}
