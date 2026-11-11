package cn.iocoder.yudao.module.media.hikvision.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePTZService;
import cn.iocoder.yudao.module.media.hikvision.service.IHikvisionService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service(ChannelDataType.PTZ_SERVICE + ChannelDataType.HIKVISION)
public class SourcePTZServiceForHikImpl implements ISourcePTZService {

    @Autowired
    private IHikvisionService hikService;

    @Override
    public void ptz(CommonGBChannel channel, FrontEndControlCodeForPTZ code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            int pan = 0;
            int tilt = 0;
            int zoom = 0;
            int speed = code != null && code.getPanSpeed() != null ? code.getPanSpeed() : 50;

            if (code != null) {
                if (code.getPan() != null) {
                    pan = code.getPan() == 0 ? 1 : -1;
                }
                if (code.getTilt() != null) {
                    tilt = code.getTilt() == 0 ? 1 : -1;
                }
                if (code.getZoom() != null) {
                    zoom = code.getZoom() == 0 ? 1 : -1;
                }
            }

            boolean ok = hikService.ptzControl(deviceId, 1, pan, tilt, zoom, speed);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "海康云台控制失败", null);
            }
        } catch (Exception e) {
            log.error("[海康 云台控制异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void preset(CommonGBChannel channel, FrontEndControlCodeForPreset code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            int presetIndex = code != null && code.getPresetId() != null ? code.getPresetId() : 1;
            boolean ok = hikService.presetControl(deviceId, 1, presetIndex);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "海康预置位调用失败", null);
            }
        } catch (Exception e) {
            log.error("[海康 预置位调用异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
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
