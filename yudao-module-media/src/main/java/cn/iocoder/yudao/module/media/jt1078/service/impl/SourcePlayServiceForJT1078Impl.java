package cn.iocoder.yudao.module.media.jt1078.service.impl;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.jt1078.bean.JT1078Device;
import cn.iocoder.yudao.module.media.jt1078.service.IJT1078Service;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.service.IMediaServerService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.JT_1078)
public class SourcePlayServiceForJT1078Impl implements ISourcePlayService {

    @Autowired
    private IJT1078Service jt1078Service;

    @Autowired
    private IMediaServerService mediaServerService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            JT1078Device device = jt1078Service.getDevice(deviceId);
            if (device == null) {
                callback.run(ErrorCode.ERROR100.getCode(), "未找到对应的JT1078设备", null);
                return;
            }

            int channelNo = 1;
            jt1078Service.requestRealTimeStream(device.getSimNo(), channelNo);

            MediaServer server = mediaServerService.getDefaultMediaServer();
            String app = "rtp";
            String stream = device.getSimNo() + "_" + channelNo;

            StreamInfo streamInfo = mediaServerService.getStreamInfoByAppAndStream(server, app, stream, null, null);
            if (streamInfo != null) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), streamInfo);
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "车载终端推流暂未就绪，请稍后刷新", null);
            }
        } catch (Exception e) {
            log.error("[JT1078 点播异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        try {
            int deviceId = channel.getDataDeviceId();
            JT1078Device device = jt1078Service.getDevice(deviceId);
            if (device != null) {
                jt1078Service.stopRealTimeStream(device.getSimNo(), 1);
            }
        } catch (Exception e) {
            log.error("[JT1078 停止点播失败] 通道: {}", channel.getGbDeviceId(), e);
        }
    }

    @Override
    public void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback) {
        callback.run(ErrorCode.ERROR100.getCode(), "暂不支持部标设备在线截图", null);
    }
}
