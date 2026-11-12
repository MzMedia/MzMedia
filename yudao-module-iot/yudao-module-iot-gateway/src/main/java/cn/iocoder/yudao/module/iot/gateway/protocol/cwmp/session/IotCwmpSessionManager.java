package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * TR-069 CPE 会话与指令调度管理器
 *
 * @author 芋道源码
 */
@Slf4j
public class IotCwmpSessionManager {

    /**
     * SerialNumber -> CwmpSession
     */
    private final Map<String, CwmpSession> sessionsBySn = new ConcurrentHashMap<>();

    /**
     * SessionId (Cookie) -> SerialNumber
     */
    private final Map<String, String> snBySessionId = new ConcurrentHashMap<>();

    /**
     * 获取或创建 CPE 会话
     */
    public CwmpSession getOrCreateSession(String serialNumber) {
        return sessionsBySn.computeIfAbsent(serialNumber, sn -> {
            String sessionId = "cwmp_" + IdUtil.fastSimpleUUID();
            CwmpSession session = CwmpSession.builder()
                    .serialNumber(sn)
                    .sessionId(sessionId)
                    .lastActiveTime(System.currentTimeMillis())
                    .build();
            snBySessionId.put(sessionId, sn);
            return session;
        });
    }

    /**
     * 根据 Cookie 会话 ID 查找 CPE 会话
     */
    public CwmpSession getSessionByCookie(String sessionId) {
        if (StrUtil.isBlank(sessionId)) {
            return null;
        }
        String sn = snBySessionId.get(sessionId);
        if (sn == null) {
            return null;
        }
        return sessionsBySn.get(sn);
    }

    /**
     * 更新 CPE 的 ConnectionRequestURL
     */
    public void updateConnectionRequestUrl(String serialNumber, String url) {
        if (StrUtil.isBlank(serialNumber) || StrUtil.isBlank(url)) {
            return;
        }
        CwmpSession session = getOrCreateSession(serialNumber);
        session.setConnectionRequestUrl(url);
        session.setLastActiveTime(System.currentTimeMillis());
    }

    /**
     * 投递 RPC 命令进入设备队列
     *
     * @param serialNumber 设备序列号
     * @param soapXml SOAP 请求报文
     * @param timeoutMs 超时时间（毫秒）
     * @return 响应报文 Future
     */
    public CompletableFuture<String> enqueueCommand(String serialNumber, String soapXml, long timeoutMs) {
        CwmpSession session = getOrCreateSession(serialNumber);
        CompletableFuture<String> future = new CompletableFuture<>();

        CwmpSession.CwmpPendingCommand cmd = new CwmpSession.CwmpPendingCommand(
                IdUtil.fastSimpleUUID(),
                soapXml,
                future,
                System.currentTimeMillis()
        );

        session.getCommandQueue().add(cmd);
        log.info("[enqueueCommand][设备 {} 投递命令成功，当前排队数: {}]", serialNumber, session.getCommandQueue().size());

        // 超时取消保障
        CompletableFuture.delayedExecutor(timeoutMs, TimeUnit.MILLISECONDS).execute(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("CWMP command timeout for device " + serialNumber));
            }
        });

        return future;
    }

    /**
     * 从队列中弹出下一个待发送给 CPE 的命令，并置为 waitingCommand
     */
    public CwmpSession.CwmpPendingCommand popNextCommand(String serialNumber) {
        CwmpSession session = sessionsBySn.get(serialNumber);
        if (session == null) {
            return null;
        }
        session.setLastActiveTime(System.currentTimeMillis());
        CwmpSession.CwmpPendingCommand nextCmd = session.getCommandQueue().poll();
        session.setWaitingCommand(nextCmd);
        return nextCmd;
    }

    /**
     * 完成当前正在等待响应的命令
     */
    public void completeWaitingCommand(String serialNumber, String responseXml) {
        CwmpSession session = sessionsBySn.get(serialNumber);
        if (session == null) {
            return;
        }
        session.setLastActiveTime(System.currentTimeMillis());
        CwmpSession.CwmpPendingCommand waiting = session.getWaitingCommand();
        if (waiting != null) {
            log.info("[completeWaitingCommand][收到设备 {} 响应，完成指令: {}]", serialNumber, waiting.getCommandId());
            waiting.getFuture().complete(responseXml);
            session.setWaitingCommand(null);
        }
    }

    /**
     * 清理过期未活跃的会话
     */
    public void cleanExpiredSessions(long timeoutMs) {
        long now = System.currentTimeMillis();
        sessionsBySn.entrySet().removeIf(entry -> {
            CwmpSession session = entry.getValue();
            boolean expired = (now - session.getLastActiveTime()) > timeoutMs && session.getCommandQueue().isEmpty();
            if (expired) {
                snBySessionId.remove(session.getSessionId());
                log.info("[cleanExpiredSessions][清理过期 CPE 会话: {}]", session.getSerialNumber());
            }
            return expired;
        });
    }

}
