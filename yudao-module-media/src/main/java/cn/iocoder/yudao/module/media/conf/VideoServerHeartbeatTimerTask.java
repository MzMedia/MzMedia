package cn.iocoder.yudao.module.media.conf;

import cn.iocoder.yudao.module.media.common.ServerInfo;
import cn.iocoder.yudao.module.media.conf.trace.TraceIpUtil;
import cn.iocoder.yudao.module.media.storager.IRedisCatchStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 视频信令与媒体服务节点心跳保活定时任务
 * 定时上报当前服务节点信息至 Redis，维持服务集群在线状态
 *
 * @author MzMedia Architect
 */
@Component("videoServerHeartbeatTimerTask")
public class VideoServerHeartbeatTimerTask {

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private SipConfig sipConfig;

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.SECONDS) // 每 2 秒上报一次心跳保活
    public void execute() {
        redisCatchStorage.updateServerInfo(ServerInfo.create(TraceIpUtil.resolveSipShowIp(sipConfig), serverPort), 3);
    }
}
