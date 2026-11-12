package cn.iocoder.yudao.module.iot.gateway.protocol.usp;

import lombok.Data;

/**
 * TR-369 (USP) 协议配置
 *
 * @author 芋道源码
 */
@Data
public class IotUspConfig {

    /**
     * USP Controller Endpoint ID
     */
    private String controllerEndpointId = "proto::mzmedia-controller";

    /**
     * MQTT 主题前缀（与 Oktopus 默认规范一致）
     */
    private String topicPrefix = "oktopus/usp/v1";

    /**
     * 请求超时时间（毫秒），默认 30 秒
     */
    private Long requestTimeoutMs = 30000L;

}
