package cn.iocoder.yudao.module.media.gb28181.service;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;

/**
 * 资源能力接入-录像下载
 */
public interface ISourceDownloadService {

    void download(CommonGBChannel channel, Long startTime, Long stopTime, Integer downloadSpeed, ErrorCallback<StreamInfo> callback);

    void stopDownload(CommonGBChannel channel, String stream);
}
