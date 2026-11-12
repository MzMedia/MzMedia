package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TR-069 CPE 会话对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpSession {

    /**
     * 设备序列号 (SerialNumber)
     */
    private String serialNumber;

    /**
     * 会话 ID (对应 HTTP Cookie)
     */
    private String sessionId;

    /**
     * 设备 ConnectionRequestURL
     */
    private String connectionRequestUrl;

    /**
     * 最后活跃时间戳
     */
    @Builder.Default
    private volatile long lastActiveTime = System.currentTimeMillis();

    /**
     * 最近一次 Inform 报文对象
     */
    private cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.CwmpInform lastInform;

    /**
     * 待下发给 CPE 的 RPC 命令队列
     */
    @Builder.Default
    private ConcurrentLinkedQueue<CwmpPendingCommand> commandQueue = new ConcurrentLinkedQueue<>();

    /**
     * 当前正在等待 CPE 响应的命令
     */
    private volatile CwmpPendingCommand waitingCommand;

    /**
     * 待执行的下行命令封装
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CwmpPendingCommand {
        private String commandId;
        private String soapXml;
        private CompletableFuture<String> future;
        private long createTime;
    }

}
