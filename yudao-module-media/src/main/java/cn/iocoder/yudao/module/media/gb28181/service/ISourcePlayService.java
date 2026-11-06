package cn.iocoder.yudao.module.media.gb28181.service;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;

/**
 * 资源能力接入-实时录像
 */
public interface ISourcePlayService {

    void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback);

    void stopPlay(CommonGBChannel channel);

    void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback);

}
