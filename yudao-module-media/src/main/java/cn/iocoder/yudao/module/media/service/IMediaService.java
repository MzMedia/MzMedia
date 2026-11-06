package cn.iocoder.yudao.module.media.service;

import cn.iocoder.yudao.module.media.media.bean.ResultForOnPublish;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;

/**
 * 媒体信息业务
 */
public interface IMediaService {

    /**
     * 播放鉴权
     */
    boolean authenticatePlay(String app, String stream, String callId);

    ResultForOnPublish authenticatePublish(MediaServer mediaServer, String app, String stream, String params);

    boolean closeStreamOnNoneReader(String mediaServerId, String app, String stream, String schema);
}
