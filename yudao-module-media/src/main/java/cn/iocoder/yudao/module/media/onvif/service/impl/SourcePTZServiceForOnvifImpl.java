package cn.iocoder.yudao.module.media.onvif.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePTZService;
import cn.iocoder.yudao.module.media.onvif.service.IOnvifService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service(ChannelDataType.PTZ_SERVICE + ChannelDataType.ONVIF)
public class SourcePTZServiceForOnvifImpl implements ISourcePTZService {

    @Autowired
    private IOnvifService onvifService;

    @Override
    public void ptz(CommonGBChannel channel, FrontEndControlCodeForPTZ code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            Integer pan = code != null ? code.getPan() : null;
            Integer tilt = code != null ? code.getTilt() : null;
            Integer zoom = code != null ? code.getZoom() : null;
            Double speed = code != null && code.getPanSpeed() != null ? (double) code.getPanSpeed() : 50.0;

            boolean success = onvifService.ptzControl(deviceId, pan, tilt, zoom, speed);
            if (success) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "ONVIF云台控制失败", null);
            }
        } catch (Exception e) {
            log.error("[ONVIF 云台控制异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void preset(CommonGBChannel channel, FrontEndControlCodeForPreset code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            Integer cmd = code != null ? code.getCode() : 1;
            String presetToken = code != null && code.getPresetId() != null ? String.valueOf(code.getPresetId()) : "1";

            boolean success = onvifService.presetControl(deviceId, cmd, presetToken);
            if (success) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "ONVIF预置位操作失败", null);
            }
        } catch (Exception e) {
            log.error("[ONVIF 预置位异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void fi(CommonGBChannel channel, FrontEndControlCodeForFI code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持光圈聚焦", null);
    }

    @Override
    public void tour(CommonGBChannel channel, FrontEndControlCodeForTour code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持巡航", null);
    }

    @Override
    public void scan(CommonGBChannel channel, FrontEndControlCodeForScan code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持扫描", null);
    }

    @Override
    public void auxiliary(CommonGBChannel channel, FrontEndControlCodeForAuxiliary code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持辅助开关", null);
    }

    @Override
    public void wiper(CommonGBChannel channel, FrontEndControlCodeForWiper code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持雨刷", null);
    }

    @Override
    public void queryPreset(CommonGBChannel channel, ErrorCallback<List<Preset>> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "success", Collections.emptyList());
    }

    @Override
    public void dragZoom(CommonGBChannel channel, FrontEndControlCodeForDragZoom code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持拉框放大", null);
    }

    @Override
    public void homePosition(CommonGBChannel channel, Boolean enabled, Integer resetTime, Integer presetIndex, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "不支持看守位", null);
    }
}
