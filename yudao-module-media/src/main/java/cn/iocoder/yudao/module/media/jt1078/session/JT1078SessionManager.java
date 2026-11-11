package cn.iocoder.yudao.module.media.jt1078.session;

import cn.iocoder.yudao.module.media.jt1078.codec.JT1078Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JT1078 会话与连接管理器
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class JT1078SessionManager {

    /**
     * simNo -> Netty Channel
     */
    private final Map<String, Channel> sessionMap = new ConcurrentHashMap<>();

    /**
     * simNo_channelNo -> 最近一条音视频消息
     */
    private final Map<String, JT1078Message> latestMessageMap = new ConcurrentHashMap<>();

    public void registerChannel(String simNo, Channel channel) {
        sessionMap.put(simNo, channel);
        log.info("[JT1078 Session] 车辆上线: {}", simNo);
    }

    public void removeChannel(Channel channel) {
        sessionMap.entrySet().removeIf(entry -> entry.getValue().equals(channel));
    }

    public Channel getChannel(String simNo) {
        return sessionMap.get(simNo);
    }

    public void onMessageReceived(JT1078Message message) {
        String key = message.getSimNo() + "_" + message.getChannelNo();
        latestMessageMap.put(key, message);
    }

    public boolean isOnline(String simNo) {
        Channel channel = sessionMap.get(simNo);
        return channel != null && channel.isActive();
    }
}
