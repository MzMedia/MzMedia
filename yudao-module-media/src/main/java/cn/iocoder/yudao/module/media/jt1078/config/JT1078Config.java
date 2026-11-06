package cn.iocoder.yudao.module.media.jt1078.config;

import org.springframework.stereotype.Component;

@Component
public class JT1078Config {
    private boolean enable = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
