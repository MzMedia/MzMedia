package cn.iocoder.yudao.module.media.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 视频模块错误码常量
 *
 * 视频流媒体与GB28181模块，使用 1-020-000-000 段
 *
 * @author MzMedia Architect
 */
public interface VideoErrorCodeConstants {

    // ========== 视频设备相关 1-020-001-000 ==========
    ErrorCode VIDEO_DEVICE_NOT_EXISTS      = new ErrorCode(1_020_001_000, "视频设备不存在");
    ErrorCode VIDEO_DEVICE_OFFLINE         = new ErrorCode(1_020_001_001, "设备处于离线状态，无法建立连接");
    ErrorCode VIDEO_DEVICE_NAME_DUPLICATE  = new ErrorCode(1_020_001_002, "视频设备名称已存在");
    ErrorCode VIDEO_DEVICE_ID_DUPLICATE    = new ErrorCode(1_020_001_003, "设备国标编号已存在");

    // ========== 视频通道相关 1-020-002-000 ==========
    ErrorCode VIDEO_CHANNEL_NOT_EXISTS     = new ErrorCode(1_020_002_000, "视频通道不存在");
    ErrorCode VIDEO_CHANNEL_OFFLINE        = new ErrorCode(1_020_002_001, "视频通道已离线");
    ErrorCode VIDEO_CHANNEL_SYNC_FAILED    = new ErrorCode(1_020_002_002, "视频通道同步失败，设备无响应");

    // ========== 流媒体服务器与点播相关 1-020-003-000 ==========
    ErrorCode VIDEO_MEDIA_SERVER_NOT_FOUND = new ErrorCode(1_020_003_000, "未找到可用的流媒体节点服务器");
    ErrorCode VIDEO_MEDIA_SERVER_OFFLINE   = new ErrorCode(1_020_003_001, "流媒体服务器节点已失联");
    ErrorCode VIDEO_PLAY_START_FAILED      = new ErrorCode(1_020_003_002, "发起视频点播失败: {}");
    ErrorCode VIDEO_PLAY_STOP_FAILED       = new ErrorCode(1_020_003_003, "停止视频点播失败: {}");
    ErrorCode VIDEO_UNSUPPORTED_PROTOCOL   = new ErrorCode(1_020_003_004, "不支持的流媒体协议或设备类型");
    ErrorCode VIDEO_RTP_PORT_EXHAUSTED     = new ErrorCode(1_020_003_005, "媒体服务器 RTP 接收端口已耗尽");
    ErrorCode VIDEO_STREAM_PROXY_NOT_EXISTS = new ErrorCode(1_020_003_006, "拉流代理不存在");
    ErrorCode VIDEO_STREAM_PROXY_EXISTS    = new ErrorCode(1_020_003_007, "该拉流代理应用名与流ID已存在");

    // 兼容简写常量
    ErrorCode MEDIA_SERVER_NOT_FOUND       = VIDEO_MEDIA_SERVER_NOT_FOUND;
    ErrorCode STREAM_NOT_FOUND             = VIDEO_STREAM_PROXY_NOT_EXISTS;

    // ========== 云端录像与回放 1-020-004-000 ==========
    ErrorCode VIDEO_RECORD_NOT_EXISTS      = new ErrorCode(1_020_004_000, "未检索到对应的录像片段");
    ErrorCode VIDEO_RECORD_PLAN_NOT_EXISTS = new ErrorCode(1_020_004_001, "录像计划模板不存在");

    // ========== 云台控制与信令 1-020-005-000 ==========
    ErrorCode VIDEO_PTZ_CONTROL_FAILED     = new ErrorCode(1_020_005_000, "云台PTZ控制指令发送失败: {}");
    ErrorCode VIDEO_SIP_SEND_FAILED        = new ErrorCode(1_020_005_001, "SIP信令发送失败: {}");
}
