package cn.iocoder.yudao.module.media.streamPush.service;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;

public interface IStreamPushPlayService {
    void start(Integer id, ErrorCallback<StreamInfo> callback, String platformDeviceId, String platformName );

    void stop(String app, String stream);

    void stop(Integer id);
}
