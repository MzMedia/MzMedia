package cn.iocoder.yudao.module.media.media.abl.event;

import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import org.springframework.context.ApplicationEvent;

/**
 * zlm 心跳事件
 */
public class HookAblServerKeepaliveEvent extends ApplicationEvent {

    public HookAblServerKeepaliveEvent(Object source) {
        super(source);
    }

    private MediaServer mediaServerItem;

    public MediaServer getMediaServerItem() {
        return mediaServerItem;
    }

    public void setMediaServerItem(MediaServer mediaServerItem) {
        this.mediaServerItem = mediaServerItem;
    }
}
