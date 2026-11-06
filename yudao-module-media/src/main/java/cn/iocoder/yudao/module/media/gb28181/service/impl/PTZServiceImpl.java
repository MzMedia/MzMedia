package cn.iocoder.yudao.module.media.gb28181.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.conf.exception.ControllerException;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Preset;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceChannelService;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.service.IPTZService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.impl.SIPCommander;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.service.redisMsg.IRedisRpcPlayService;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
public class PTZServiceImpl implements IPTZService {


    @Autowired
    private SIPCommander cmder;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private IRedisRpcPlayService redisRpcPlayService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private IDeviceService deviceService;


    @Override
    public void ptz(Device device, String channelId, int cmdCode, int horizonSpeed, int verticalSpeed, int zoomSpeed) {
        try {
            cmder.frontEndCmd(device, channelId, cmdCode, horizonSpeed, verticalSpeed, zoomSpeed);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 云台控制: {}", e.getMessage());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "命令发送失败: " + e.getMessage());
        }
    }

    @Override
    public void frontEndCommand(Device device, String channelId, int cmdCode, int parameter1, int parameter2, int combindCode2) {
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!userSetting.getServerId().equals(device.getServerId())) {
            // 通道ID
            DeviceChannel deviceChannel = deviceChannelService.getOneForSource(device.getDeviceId(), channelId);
            Assert.notNull(deviceChannel, "通道不存在");
            String msg = redisRpcPlayService.frontEndCommand(device.getServerId(), deviceChannel.getId(), cmdCode, parameter1, parameter2, combindCode2);
            if (msg != null) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), msg);
            }
            return;
        }
        try {
            cmder.frontEndCmd(device, channelId, cmdCode, parameter1, parameter2, combindCode2);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 前端控制: {}", e.getMessage());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "命令发送失败: " + e.getMessage());
        }
    }

    @Override
    public void frontEndCommand(CommonGBChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode2) {
        if (channel.getDataType() != ChannelDataType.GB28181) {
            // 只有国标通道的支持云台控制
            log.warn("[INFO 消息] 只有国标通道的支持云台控制， 通道ID： {}", channel.getGbId());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "不支持");
        }
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到设备ID");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        frontEndCommand(device, deviceChannel.getDeviceId(), cmdCode, parameter1, parameter2, combindCode2);
    }

    @Override
    public void dragZoomIn(CommonGBChannel channel, int length, int width, int midPointX, int midPointY, int lengthX, int lengthY) {
        if (channel.getDataType() != ChannelDataType.GB28181) {
            log.warn("[INFO 消息] 只有国标通道的支持云台控制， 通道ID： {}", channel.getGbId());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "不支持");
        }
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到设备ID");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        deviceService.dragZoomIn(device, deviceChannel.getDeviceId(), length, width, midPointX, midPointY, lengthX, lengthY);
    }

    @Override
    public void dragZoomOut(CommonGBChannel channel, int length, int width, int midPointX, int midPointY, int lengthX, int lengthY) {
        if (channel.getDataType() != ChannelDataType.GB28181) {
            log.warn("[INFO 消息] 只有国标通道的支持云台控制， 通道ID： {}", channel.getGbId());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "不支持");
        }
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到设备ID");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        deviceService.dragZoomOut(device, deviceChannel.getDeviceId(), length, width, midPointX, midPointY, lengthX, lengthY);
    }

    @Override
    public void homePosition(CommonGBChannel channel, Boolean enabled, Integer resetTime, Integer presetIndex, ErrorCallback<String> callback) {
        if (channel.getDataType() != ChannelDataType.GB28181) {
            log.warn("[INFO 消息] 只有国标通道支持看守位，通道ID：{}", channel.getGbId());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "不支持");
        }
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到设备");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到通道");
        }
        deviceService.homePosition(device, deviceChannel.getDeviceId(), enabled, resetTime, presetIndex, callback);
    }

    @Override
    public void queryPresetList(CommonGBChannel channel, ErrorCallback<List<Preset>> callback) {
        if (channel.getDataType() != ChannelDataType.GB28181) {
            // 只有国标通道的支持云台控制
            log.warn("[INFO 消息] 只有国标通道的支持云台控制， 通道ID： {}", channel.getGbId());
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "不支持");
        }
        Device device = deviceService.getDevice(channel.getDataDeviceId());
        if (device == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到设备");
        }
        DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(channel.getGbId());
        if (deviceChannel == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到通道");
        }
        deviceService.queryPreset(device, deviceChannel.getDeviceId(), callback);
    }


}
