package cn.iocoder.yudao.module.media.framework.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流媒体注册/注销/状态变更事件
 * 当 ZLMediaKit 通过 on_stream_changed Webhook 通知时由 Spring Event 发布
 *
 * @author MzMedia Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStreamChangedEvent {

    /**
     * 应用名称 (如 live)
     */
    private String app;

    /**
     * 流 ID
     */
    private String stream;

    /**
     * 是否注册 (true: 注册/推流上线; false: 注销/推流断开)
     */
    private Boolean regist;

    /**
     * 媒体服务器 ID
     */
    private String mediaServerId;

    /**
     * 产生流的客户端 IP
     */
    private String originType;

    /**
     * 产生流的时间戳
     */
    private Long timestamp;
}
