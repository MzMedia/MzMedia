package cn.iocoder.yudao.module.media.jt1078.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePTZService;
import cn.iocoder.yudao.module.media.jt1078.bean.JT1078Device;
import cn.iocoder.yudao.module.media.jt1078.service.IJT1078Service;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service(ChannelDataType.PTZ_SERVICE + ChannelDataType.JT_1078)
public class SourcePTZServiceForJT1078Impl implements ISourcePTZService {

    @Autowired
    private IJT1078Service jt1078Service;

    @Override
    public void ptz(CommonGBChannel channel, FrontEndControlCodeForPTZ code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            JT1078Device device = jt1078Service.getDevice(deviceId);
            if (device == null) {
                callback.run(ErrorCode.ERROR100.getCode(), "未找到对应的JT1078设备", null);
                return;
            }

            int ptzCmd = 0; // 0: 上, 1: 下, 2: 左, 3: 右
            int speed = code != null && code.getPanSpeed() != null ? code.getPanSpeed() : 50;

            if (code != null) {
                if (code.getPan() != null) {
                    ptzCmd = code.getPan() == 0 ? 3 : 2;
                } else if (code.getTilt() != null) {
                    ptzCmd = code.getTilt() == 0 ? 0 : 1;
                }
            }

            boolean ok = jt1078Service.ptzControl(device.getSimNo(), 1, ptzCmd, speed);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "车载终端不在线或未响应PTZ", null);
            }
        } catch (Exception e) {
            log.error("[JT1078 PTZ异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void preset(CommonGBChannel channel, FrontEndControlCodeForPreset code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "车载协议暂不支持预置位", null);
    }

    @Override
    public void fi(CommonGBChannel channel, FrontEndControlCodeForFI code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void tour(CommonGBChannel channel, FrontEndControlCodeForTour code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void scan(CommonGBChannel channel, FrontEndControlCodeForScan code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void auxiliary(CommonGBChannel channel, FrontEndControlCodeForAuxiliary code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void wiper(CommonGBChannel channel, FrontEndControlCodeForWiper code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void queryPreset(CommonGBChannel channel, ErrorCallback<List<Preset>> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", Collections.emptyList());
    }

    @Override
    public void dragZoom(CommonGBChannel channel, FrontEndControlCodeForDragZoom code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }

    @Override
    public void homePosition(CommonGBChannel channel, Boolean enabled, Integer resetTime, Integer presetIndex, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "ok", null);
    }
}
