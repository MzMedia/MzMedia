package cn.iocoder.yudao.module.media.media.event.media;

import cn.iocoder.yudao.module.media.media.abl.bean.hook.ABLHookParam;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.zlm.dto.hook.OnStreamChangedHookParam;

/**
 * 流离开事件
 */
public class MediaDepartureEvent extends MediaEvent {
    public MediaDepartureEvent(Object source) {
        super(source);
    }

    /**
     * 流实例创建时间戳(秒)，来自ZLM on_stream_changed，用于区分旧会话的迟到注销事件
     */
    private Long createStamp;

    public Long getCreateStamp() {
        return createStamp;
    }

    public void setCreateStamp(Long createStamp) {
        this.createStamp = createStamp;
    }

    public static MediaDepartureEvent getInstance(Object source, OnStreamChangedHookParam hookParam, MediaServer mediaServer){
        MediaDepartureEvent mediaDepartureEven = new MediaDepartureEvent(source);
        mediaDepartureEven.setApp(hookParam.getApp());
        mediaDepartureEven.setStream(hookParam.getStream());
        mediaDepartureEven.setSchema(hookParam.getSchema());
        mediaDepartureEven.setMediaServer(mediaServer);
        return mediaDepartureEven;
    }

    public static MediaDepartureEvent getInstance(Object source, ABLHookParam hookParam, MediaServer mediaServer){
        MediaDepartureEvent mediaDepartureEven = new MediaDepartureEvent(source);
        mediaDepartureEven.setApp(hookParam.getApp());
        mediaDepartureEven.setStream(hookParam.getStream());
        mediaDepartureEven.setMediaServer(mediaServer);
        return mediaDepartureEven;
    }
}
