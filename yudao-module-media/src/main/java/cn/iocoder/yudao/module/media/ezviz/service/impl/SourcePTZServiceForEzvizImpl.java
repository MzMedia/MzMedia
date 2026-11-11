package cn.iocoder.yudao.module.media.ezviz.service.impl;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.ezviz.service.IEzvizService;
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
@Service(ChannelDataType.PTZ_SERVICE + ChannelDataType.EZVIZ)
public class SourcePTZServiceForEzvizImpl implements ISourcePTZService {

    @Autowired
    private IEzvizService ezvizService;

    @Override
    public void ptz(CommonGBChannel channel, FrontEndControlCodeForPTZ code, ErrorCallback<String> callback) {
        try {
            int deviceId = channel.getDataDeviceId();
            int direction = 0; // 0-上, 1-下, 2-左, 3-右, 8-放大, 9-缩小
            boolean stop = false;

            if (code != null) {
                if (code.getPan() != null) {
                    direction = code.getPan() == 0 ? 3 : 2;
                } else if (code.getTilt() != null) {
                    direction = code.getTilt() == 0 ? 0 : 1;
                } else if (code.getZoom() != null) {
                    direction = code.getZoom() == 0 ? 8 : 9;
                } else {
                    stop = true;
                }
            }

            boolean ok = ezvizService.ptzControl(deviceId, direction, 1, stop);
            if (ok) {
                callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "ok");
            } else {
                callback.run(ErrorCode.ERROR100.getCode(), "萤石云云台控制失败", null);
            }
        } catch (Exception e) {
            log.error("[萤石云 云台控制异常] 通道: {}", channel.getGbDeviceId(), e);
            callback.run(ErrorCode.ERROR100.getCode(), e.getMessage(), null);
        }
    }

    @Override
    public void preset(CommonGBChannel channel, FrontEndControlCodeForPreset code, ErrorCallback<String> callback) {
        callback.run(ErrorCode.SUCCESS.getCode(), "暂不支持萤石云预置位操作", null);
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
