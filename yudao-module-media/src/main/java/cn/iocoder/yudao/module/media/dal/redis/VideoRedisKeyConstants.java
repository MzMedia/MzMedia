package cn.iocoder.yudao.module.media.dal.redis;

/**
 * 视频模块 Redis Key 常量规范
 *
 * 规范格式: mz:video:<业务域>:<具体KEY>
 *
 * @author MzMedia Architect
 */
public interface VideoRedisKeyConstants {

    /**
     * 信令服务节点保活 Hash 集合
     * KEY 格式: mz:video:server:signaling_list
     */
    String SIGNALING_SERVER_LIST = "mz:video:server:signaling_list";

    /**
     * 设备在线状态与元数据缓存
     * KEY 格式: mz:video:device:info:{deviceId}
     */
    String DEVICE_INFO_PREFIX = "mz:video:device:info:%s";

    /**
     * 设备心跳保活 KEY
     * KEY 格式: mz:video:device:keepalive:{deviceId}
     */
    String DEVICE_KEEPALIVE_PREFIX = "mz:video:device:keepalive:%s";

    /**
     * 设备通道点播会话上下文
     * KEY 格式: mz:video:play:session:{channelId}
     */
    String PLAY_SESSION_PREFIX = "mz:video:play:session:%s";

    /**
     * ZLM 媒体流通道映射
     * KEY 格式: mz:video:stream:channel:{streamId}
     */
    String STREAM_CHANNEL_PREFIX = "mz:video:stream:channel:%s";

    /**
     * 发送 RTP 端口与会话映射
     * KEY 格式: mz:video:rtp:send_info:{callId}
     */
    String RTP_SEND_INFO_PREFIX = "mz:video:rtp:send_info:%s";
}
