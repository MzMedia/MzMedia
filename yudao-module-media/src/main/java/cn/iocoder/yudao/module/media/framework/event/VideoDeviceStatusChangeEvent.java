package cn.iocoder.yudao.module.media.framework.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频设备在线状态变更事件
 * 当设备注册、注销、心跳超时时由 Spring Event 发布
 *
 * @author MzMedia Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDeviceStatusChangeEvent {

    /**
     * 设备国标编号
     */
    private String deviceId;

    /**
     * 在线状态 (0: 离线, 1: 在线)
     */
    private Integer status;

    /**
     * 事件发生时间
     */
    private String eventTime;

    /**
     * 变更原因 (REGISTER, KEEPALIVE_TIMEOUT, UNREGISTER)
     */
    private String reason;
}
