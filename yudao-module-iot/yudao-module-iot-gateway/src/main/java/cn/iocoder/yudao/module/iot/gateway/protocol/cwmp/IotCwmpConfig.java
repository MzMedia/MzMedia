package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import lombok.Data;

/**
 * TR-069 (CWMP) 协议配置
 *
 * @author 芋道源码
 */
@Data
public class IotCwmpConfig {

    /**
     * ACS HTTP 服务根路径
     */
    private String path = "/acs";

    /**
     * 是否开启 HTTP 认证 (Basic / Digest)
     */
    private Boolean authEnabled = true;

    /**
     * 默认 ACS 用户名 (设备连接平台使用的凭据)
     */
    private String defaultUsername = "acs";

    /**
     * 默认 ACS 密码
     */
    private String defaultPassword = "acs";

    /**
     * 会话超时时间（毫秒），默认 30 秒
     */
    private Long sessionTimeoutMs = 30000L;

    /**
     * 下行 RPC 请求等待 CPE 应答的超时时间（毫秒），默认 30 秒
     */
    private Long rpcTimeoutMs = 30000L;

}
