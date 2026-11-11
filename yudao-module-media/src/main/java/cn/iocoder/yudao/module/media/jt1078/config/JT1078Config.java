package cn.iocoder.yudao.module.media.jt1078.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jt1078")
public class JT1078Config {
    private boolean enable = false;
    private int port = 1078;
    private int msgPort = 7078;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMsgPort() {
        return msgPort;
    }

    public void setMsgPort(int msgPort) {
        this.msgPort = msgPort;
    }
}
