package cn.iocoder.yudao.module.media.onvif.service.impl;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.onvif.service.IOnvifService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamProxy.bean.StreamProxy;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyPlayService;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.ONVIF)
public class SourcePlayServiceForOnvifImpl implements ISourcePlayService {

    @Autowired
    private IOnvifService onvifService;

    @Autowired
    private IStreamProxyPlayService streamProxyPlayService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            String rtspUrl = onvifService.getPlayStreamUrl(deviceId);
            if (rtspUrl == null || rtspUrl.isEmpty()) {
                callback.run(ErrorCode.ERROR100.getCode(), "未找到有效的ONVIF RTSP流地址", null);
                return;
            }

            StreamProxy proxy = new StreamProxy();
            proxy.setApp("live");
            proxy.setStream("onvif_" + deviceId);
            proxy.setSrcUrl(rtspUrl);
            proxy.setType("default");
            proxy.setEnable(true);
            proxy.setEnableAudio(true);
            proxy.setEnableMp4(record != null && record);

            streamProxyPlayService.startProxy(proxy, callback);
        } catch (Exception e) {
            log.error("[ONVIF 点播异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        try {
            int deviceId = channel.getDataDeviceId();
            StreamProxy proxy = new StreamProxy();
            proxy.setApp("live");
            proxy.setStream("onvif_" + deviceId);
            streamProxyPlayService.stopProxy(proxy);
        } catch (Exception e) {
            log.error("[ONVIF 停止点播失败] 通道: {}", channel.getGbDeviceId(), e);
        }
    }

    @Override
    public void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback) {
        callback.run(ErrorCode.ERROR100.getCode(), "暂未实现抓拍快照", null);
    }
}
