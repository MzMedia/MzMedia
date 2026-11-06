package cn.iocoder.yudao.module.media.gb28181.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.conf.exception.ControllerException;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceChannel;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceChannelService;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.service.IPlayService;
import cn.iocoder.yudao.module.media.gb28181.service.ISourceBroadcastService;
import cn.iocoder.yudao.module.media.vmanager.bean.AudioBroadcastResult;
import cn.iocoder.yudao.module.media.vmanager.bean.AudioTalkResult;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(ChannelDataType.BROADCAST_SERVICE + ChannelDataType.GB28181)
public class SourceBroadcastServiceForGbImpl implements ISourceBroadcastService {

    @Autowired
    private IPlayService playService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Override
    public AudioTalkResult startBroadcast(CommonGBChannel channel) {
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "未找到设备");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "未找到通道");
        }
        AudioBroadcastResult abResult = playService.audioBroadcast(
                device.getDeviceId(), deviceChannel.getDeviceId(), true);
        AudioTalkResult result = new AudioTalkResult();
        result.setPushStream(abResult.getStreamInfo());
        result.setPlayStream(null);
        return result;
    }

    @Override
    public void stopBroadcast(CommonGBChannel channel) {
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) return;
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) return;
        playService.stopAudioBroadcast(device, deviceChannel);
    }

    @Override
    public AudioTalkResult startTalk(CommonGBChannel channel) {
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "未找到设备");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "未找到通道");
        }
        AudioBroadcastResult abResult = playService.audioBroadcast(
                device.getDeviceId(), deviceChannel.getDeviceId(), false);
        AudioTalkResult result = new AudioTalkResult();
        result.setPushStream(abResult.getStreamInfo());
        result.setPlayStream(abResult.getPlayStreamInfo());
        return result;
    }

    @Override
    public void stopTalk(CommonGBChannel channel) {
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) return;
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) return;
        playService.stopTalk(device, deviceChannel, null);
    }
}
