package cn.iocoder.yudao.module.media.streamProxy.service.impl;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyPlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sip.message.Response;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.STREAM_PROXY)
public class SourcePlayServiceForStreamProxyImpl implements ISourcePlayService {

    @Autowired
    private IStreamProxyPlayService playService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        // 拉流代理通道
        try {
            playService.start(channel.getDataDeviceId(), record, callback);
        }catch (Exception e) {
            callback.run(Response.BUSY_HERE, "busy here", null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        // 拉流代理通道
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
