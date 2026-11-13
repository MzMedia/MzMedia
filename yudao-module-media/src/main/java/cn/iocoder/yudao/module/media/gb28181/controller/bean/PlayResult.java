package cn.iocoder.yudao.module.media.gb28181.controller.bean;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;
import org.springframework.web.context.request.async.DeferredResult;

public class PlayResult {

    private DeferredResult<MzResult<StreamInfo>> result;
    private String uuid;

    private Device device;

    public DeferredResult<MzResult<StreamInfo>> getResult() {
        return result;
    }

    public void setResult(DeferredResult<MzResult<StreamInfo>> result) {
        this.result = result;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
