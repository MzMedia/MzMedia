package cn.iocoder.yudao.module.media.hikvision.service.impl;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.hikvision.service.IHikvisionService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamProxy.bean.StreamProxy;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyPlayService;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.HIKVISION)
public class SourcePlayServiceForHikImpl implements ISourcePlayService {

    @Autowired
    private IHikvisionService hikService;

    @Autowired
    private IStreamProxyPlayService streamProxyPlayService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            String rtspUrl = hikService.getStreamUrl(deviceId, 1);
            if (rtspUrl == null || rtspUrl.isEmpty()) {
                callback.run(ErrorCode.ERROR100.getCode(), "未找到有效的海康RTSP流地址", null);
                return;
            }

            StreamProxy proxy = new StreamProxy();
            proxy.setApp("live");
            proxy.setStream("hik_" + deviceId);
            proxy.setSrcUrl(rtspUrl);
            proxy.setType("default");
            proxy.setEnable(true);
            proxy.setEnableAudio(true);
            proxy.setEnableMp4(record != null && record);

            streamProxyPlayService.startProxy(proxy, callback);
        } catch (Exception e) {
            log.error("[海康 点播异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        try {
            int deviceId = channel.getDataDeviceId();
            StreamProxy proxy = new StreamProxy();
            proxy.setApp("live");
            proxy.setStream("hik_" + deviceId);
            streamProxyPlayService.stopProxy(proxy);
        } catch (Exception e) {
            log.error("[海康 停止点播异常] 通道: {}", channel.getGbDeviceId(), e);
        }
    }

    @Override
    public void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            byte[] bytes = hikService.capturePicture(deviceId, 1);
            if (bytes != null) {
                callback.run(ErrorCode.SUCCESS.getCode(), "ok", bytes);
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "海康抓图返回为空", null);
            }
        } catch (Exception e) {
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }
}
