package cn.iocoder.yudao.module.media.streamProxy.service;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamProxy.bean.StreamProxy;
import jakarta.validation.constraints.NotNull;


public interface IStreamProxyPlayService {

    void start(int id, Boolean record, ErrorCallback<StreamInfo> callback);

    void startProxy(@NotNull StreamProxy streamProxy, ErrorCallback<StreamInfo> callback);

    void stop(int id);

    void stopProxy(StreamProxy streamProxy);
}
