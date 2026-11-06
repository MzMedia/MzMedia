package cn.iocoder.yudao.module.media.gb28181.service.impl;

import cn.iocoder.yudao.module.media.common.InviteSessionType;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.conf.exception.ControllerException;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.bean.PlayException;
import cn.iocoder.yudao.module.media.gb28181.service.IPlayService;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePlayService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sip.message.Response;

@Slf4j
@Service(ChannelDataType.PLAY_SERVICE + ChannelDataType.GB28181)
public class SourcePlayServiceForGbImpl implements ISourcePlayService {

    @Autowired
    private IPlayService deviceChannelPlayService;

    @Override
    public void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback) {
        // 国标通道
        try {
            deviceChannelPlayService.play(channel, record, callback);
        } catch (PlayException e) {
            callback.run(e.getCode(), e.getMsg(), null);
        } catch (ControllerException e) {
            log.error("[点播失败] {}({}), {}", channel.getGbName(), channel.getGbDeviceId(), e.getMsg());
            callback.run(Response.BUSY_HERE, "busy here", null);
        } catch (Exception e) {
            log.error("[点播失败] {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
            callback.run(Response.BUSY_HERE, "busy here", null);
        }
    }

    @Override
    public void stopPlay(CommonGBChannel channel) {
        // 国标通道
        try {
            deviceChannelPlayService.stopPlay(InviteSessionType.PLAY, channel);
        }  catch (Exception e) {
            log.error("[停止点播失败] {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
        }
    }

    @Override
    public void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback) {
        try {
            deviceChannelPlayService.getSnap(channel, callback);
        } catch (PlayException e) {
            callback.run(e.getCode(), e.getMsg(), null);
        } catch (ControllerException e) {
            log.error("[获取抓图失败] {}({}), {}", channel.getGbName(), channel.getGbDeviceId(), e.getMsg());
            callback.run(Response.BUSY_HERE, "busy here", null);
        } catch (Exception e) {
            log.error("[获取抓图失败] {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
            callback.run(Response.BUSY_HERE, "busy here", null);
        }
    }
}
