package cn.iocoder.yudao.module.media.media.event.media;

import cn.iocoder.yudao.module.media.media.abl.bean.hook.ABLHookParam;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.zlm.dto.hook.OnStreamNotFoundHookParam;

/**
 * 流未找到
 */
public class MediaNotFoundEvent extends MediaEvent {
    public MediaNotFoundEvent(Object source) {
        super(source);
    }

    public static MediaNotFoundEvent getInstance(Object source, OnStreamNotFoundHookParam hookParam, MediaServer mediaServer){
        MediaNotFoundEvent mediaDepartureEven = new MediaNotFoundEvent(source);
        mediaDepartureEven.setApp(hookParam.getApp());
        mediaDepartureEven.setStream(hookParam.getStream());
        mediaDepartureEven.setSchema(hookParam.getSchema());
        mediaDepartureEven.setMediaServer(mediaServer);
        mediaDepartureEven.setParams(hookParam.getParams());
        return mediaDepartureEven;
    }

    public static MediaNotFoundEvent getInstance(Object source, ABLHookParam hookParam, MediaServer mediaServer){
        MediaNotFoundEvent mediaDepartureEven = new MediaNotFoundEvent(source);
        mediaDepartureEven.setApp(hookParam.getApp());
        mediaDepartureEven.setStream(hookParam.getStream());
        mediaDepartureEven.setMediaServer(mediaServer);
        return mediaDepartureEven;
    }
}
