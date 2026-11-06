package cn.iocoder.yudao.module.media.gb28181.service.impl;

import cn.iocoder.yudao.module.media.common.InviteSessionType;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.service.IPlayService;
import cn.iocoder.yudao.module.media.gb28181.service.ISourceDownloadService;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(ChannelDataType.DOWNLOAD_SERVICE + ChannelDataType.GB28181)
public class SourceDownloadServiceForGbImpl implements ISourceDownloadService {

    @Autowired
    private IPlayService deviceChannelPlayService;

    @Override
    public void download(CommonGBChannel channel, Long startTime, Long stopTime, Integer downloadSpeed, ErrorCallback<StreamInfo> callback) {

    }

    @Override
    public void stopDownload(CommonGBChannel channel, String stream) {
        // 国标通道
        try {
            deviceChannelPlayService.stop(InviteSessionType.DOWNLOAD, channel, stream);
        }  catch (Exception e) {
            log.error("[停止下载失败] {}({})", channel.getGbName(), channel.getGbDeviceId(), e);
        }
    }
}
