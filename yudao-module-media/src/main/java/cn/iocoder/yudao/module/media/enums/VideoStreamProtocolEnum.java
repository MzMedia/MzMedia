package cn.iocoder.yudao.module.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流媒体播放协议枚举
 *
 * @author MzMedia Architect
 */
@Getter
@AllArgsConstructor
public enum VideoStreamProtocolEnum {

    WEBRTC("webrtc", "WebRTC超低延迟流 (毫秒级)"),
    FLV("flv", "HTTP-FLV / WS-FLV 流"),
    WS_FLV("ws-flv", "WebSocket FLV 流"),
    HLS("hls", "HLS (m3u8) 兼容流"),
    RTSP("rtsp", "RTSP 原始流"),
    RTMP("rtmp", "RTMP 传统直播流");

    private final String code;
    private final String name;

    public static VideoStreamProtocolEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (VideoStreamProtocolEnum value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
