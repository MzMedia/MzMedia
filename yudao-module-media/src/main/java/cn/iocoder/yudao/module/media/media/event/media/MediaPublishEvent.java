package cn.iocoder.yudao.module.media.media.event.media;

import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.zlm.dto.hook.OnPublishHookParam;

/**
 * 推流鉴权事件
 */
public class MediaPublishEvent extends MediaEvent {
    public MediaPublishEvent(Object source) {
        super(source);
    }

    public static MediaPublishEvent getInstance(Object source, OnPublishHookParam hookParam, MediaServer mediaServer){
        MediaPublishEvent mediaPublishEvent = new MediaPublishEvent(source);
        mediaPublishEvent.setApp(hookParam.getApp());
        mediaPublishEvent.setStream(hookParam.getStream());
        mediaPublishEvent.setMediaServer(mediaServer);
        mediaPublishEvent.setSchema(hookParam.getSchema());
        mediaPublishEvent.setParams(hookParam.getParams());
        return mediaPublishEvent;
    }

}
