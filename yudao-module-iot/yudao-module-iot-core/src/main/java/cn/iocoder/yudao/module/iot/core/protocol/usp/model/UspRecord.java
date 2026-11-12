package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-369 USP Record (传输层封装)
 *
 * 对应 Broadband Forum usp-record-1-3.proto
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspRecord {

    /**
     * 协议版本，例如 "1.3" 或 "1.0"
     */
    @Builder.Default
    private String version = "1.3";

    /**
     * 目标 Endpoint ID (To ID, 例如 "proto::agent-001")
     */
    private String toId;

    /**
     * 发送方 Endpoint ID (From ID, 例如 "proto::mzmedia-controller")
     */
    private String fromId;

    /**
     * 传输层安全级别：0-PLAINTEXT, 1-TLS12
     */
    @Builder.Default
    private Integer payloadSecurity = 0;

    /**
     * 消息体二进制载荷 (序列化后的 UspMsg)
     */
    private byte[] payload;

}
