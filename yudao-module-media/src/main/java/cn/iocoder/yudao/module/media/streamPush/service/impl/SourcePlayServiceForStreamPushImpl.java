package cn.iocoder.yudao.module.media.streamPush.service.impl;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.bean.PlayException;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamPush.service.IStreamPushPlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sip.message.Response;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.STREAM_PUSH)
public class SourcePlayServiceForStreamPushImpl implements ISourcePlayService {

    @Autowired
    private IStreamPushPlayService playService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        String serverGBId = null;
        String platformName = null;
        if (platform != null) {
            // 推流
            serverGBId = platform.getServerGBId();
            platformName = platform.getName();
        }
        // 推流
        try {
            playService.start(channel.getDataDeviceId(), callback, serverGBId, platformName);
        }catch (PlayException e) {
            callback.run(e.getCode(), e.getMsg(), null);
        }catch (Exception e) {
            log.error("[点播推流通道失败] 通道： {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
            callback.run(Response.BUSY_HERE, "busy here", null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        // 推流
        try {
            playService.stop(channel.getDataDeviceId());
        }catch (Exception e) {
            log.error("[停止点播失败] {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
        }
    }

    @Override
    public void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback) {

    }
}
