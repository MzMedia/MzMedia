package cn.iocoder.yudao.module.media.dahua.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.dahua.service.IDahuaService;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.service.ISourcePTZService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service(ChannelDataType.PTZ_SERVICE + ChannelDataType.DAHUA)
public class SourcePTZServiceForDahuaImpl implements ISourcePTZService {

    @Autowired
    private IDahuaService dahuaService;

    @Override
    public void ptz(CommonGBChannel channel, FrontEndControlCodeForPTZ code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            String ptzCmd = "Up";
            int speed = 5;

            if (code != null) {
                if (code.getPanSpeed() != null) {
                    speed = Math.max(1, (int)(code.getPanSpeed() / 100.0 * 8));
                }
                if (code.getPan() != null && code.getPan() == 0) ptzCmd = "Right";
                else if (code.getPan() != null && code.getPan() == 1) ptzCmd = "Left";
                else if (code.getTilt() != null && code.getTilt() == 0) ptzCmd = "Up";
                else if (code.getTilt() != null && code.getTilt() == 1) ptzCmd = "Down";
                else if (code.getZoom() != null && code.getZoom() == 0) ptzCmd = "ZoomAdd";
                else if (code.getZoom() != null && code.getZoom() == 1) ptzCmd = "ZoomDec";
            }

            boolean ok = dahuaService.ptzControl(deviceId, 1, "start", ptzCmd, speed);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "大华云台控制失败", null);
            }
        } catch (Exception e) {
            log.error("[大华 云台控制异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void preset(CommonGBChannel channel, FrontEndControlCodeForPreset code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            int presetIndex = code != null && code.getPresetId() != null ? code.getPresetId() : 1;
            boolean ok = dahuaService.presetControl(deviceId, 1, presetIndex);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "大华预置位调用失败", null);
            }
        } catch (Exception e) {
            log.error("[大华 预置位调用异常] 通道: {}", channel.getGbDeviceId(), e);
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
