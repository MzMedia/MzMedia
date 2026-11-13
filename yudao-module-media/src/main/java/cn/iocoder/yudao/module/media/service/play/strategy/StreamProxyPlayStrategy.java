package cn.iocoder.yudao.module.media.service.play.strategy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * RTSP / RTMP 拉流代理点播策略实现
 *
 * @author MzMedia Architect
 */
@Slf4j
@Component
public class StreamProxyPlayStrategy implements VideoPlayStrategy {

    @Resource
    @Lazy
    private IStreamProxyService streamProxyService;

    @Override
    public VideoDeviceTypeEnum getSupportType() {
        return VideoDeviceTypeEnum.STREAM_PROXY;
    }

    @Override
    public VideoPlayRespVO startPlay(VideoChannelDO channelDO) {
        String app = "live";
        String stream = channelDO.getStreamId() != null ? channelDO.getStreamId() : channelDO.getChannelId();

        log.info("[StreamProxyPlayStrategy] 触发拉流代理点播: app={}, stream={}", app, stream);
        CompletableFuture<StreamInfo> future = new CompletableFuture<>();

        streamProxyService.startByAppAndStream(app, stream, (code, msg, data) -> {
            if (code == 0 && data != null) {
                future.complete(data);
            } else {
                future.completeExceptionally(new RuntimeException(msg != null ? msg : "拉流代理启动失败"));
            }
        });

        try {
            StreamInfo info = future.get(10, TimeUnit.SECONDS);
            String rtcUrl = info.getRtc() != null ? info.getRtc().getUrl() : null;
            String wsFlvUrl = info.getWs_flv() != null ? info.getWs_flv().getUrl() : null;
            return VideoPlayRespVO.builder()
                    .streamId(info.getStream())
                    .app(info.getApp())
                    .webrtc(rtcUrl)
                    .rtc(rtcUrl)
                    .flv(info.getFlv() != null ? info.getFlv().getUrl() : null)
                    .wsFlv(wsFlvUrl)
                    .ws_flv(wsFlvUrl)
                    .hls(info.getHls() != null ? info.getHls().getUrl() : null)
                    .rtsp(info.getRtsp() != null ? info.getRtsp().getUrl() : null)
                    .rtmp(info.getRtmp() != null ? info.getRtmp().getUrl() : null)
                    .build();
        } catch (Exception e) {
            log.error("[StreamProxyPlayStrategy] 拉流代理启动异常: app={}, stream={}", app, stream, e);
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_PLAY_START_FAILED, e.getMessage());
        }
    }

    @Override
    public void stopPlay(VideoChannelDO channelDO) {
        String app = "live";
        String stream = channelDO.getStreamId() != null ? channelDO.getStreamId() : channelDO.getChannelId();
        log.info("[StreamProxyPlayStrategy] 停止拉流代理: app={}, stream={}", app, stream);
        streamProxyService.stopByAppAndStream(app, stream);
    }
}
