package cn.iocoder.yudao.module.media.gb28181.service;

import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.vmanager.bean.AudioTalkResult;

/**
 * 资源能力接入-语音对讲
 */
public interface ISourceBroadcastService {

    AudioTalkResult startTalk(CommonGBChannel channel);

    void stopTalk(CommonGBChannel channel);

    AudioTalkResult startBroadcast(CommonGBChannel channel);

    void stopBroadcast(CommonGBChannel channel);
}
