package cn.iocoder.yudao.module.media.media.zlm.event;

import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.zlm.dto.ZLMServerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * zlm server_start事件
 */
@Setter
@Getter
public class HookZlmServerStartEvent extends ApplicationEvent {

    public HookZlmServerStartEvent(Object source) {
        super(source);
    }

    private MediaServer mediaServer;
    private ZLMServerConfig config;

}
