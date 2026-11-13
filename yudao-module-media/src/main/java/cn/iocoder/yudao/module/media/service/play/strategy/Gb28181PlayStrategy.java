package cn.iocoder.yudao.module.media.service.play.strategy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceChannel;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceChannelService;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.service.IPlayService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * GB28181 国标信令拉流点播策略实现
 *
 * @author MzMedia Architect
 */
@Slf4j
@Component
public class Gb28181PlayStrategy implements VideoPlayStrategy {

    @Resource
    @Lazy
    private IPlayService playService;

    @Resource
    @Lazy
    private IDeviceService deviceService;

    @Resource
    @Lazy
    private IDeviceChannelService deviceChannelService;

    @Override
    public VideoDeviceTypeEnum getSupportType() {
        return VideoDeviceTypeEnum.GB28181;
    }

    @Override
    public VideoPlayRespVO startPlay(VideoChannelDO channelDO) {
        String deviceId = channelDO.getDeviceId();
        String channelId = channelDO.getChannelId();

        Device device = deviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        if (!device.isOnLine()) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_OFFLINE);
        }

        DeviceChannel deviceChannel = deviceChannelService.getOne(deviceId, channelId);
        if (deviceChannel == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_CHANNEL_NOT_EXISTS);
        }

        CompletableFuture<StreamInfo> future = new CompletableFuture<>();

        log.info("[Gb28181PlayStrategy] 发起国标点播: deviceId={}, channelId={}", deviceId, channelId);
        playService.play(device, deviceChannel, (code, msg, data) -> {
            if (code == 0 && data != null) {
                future.complete(data);
            } else {
                future.completeExceptionally(new RuntimeException(msg != null ? msg : "点播协商超时"));
            }
        });

        try {
            // 等待 ZLM 分配端口与 SDP 协商完成 (超时 15 秒)
            StreamInfo streamInfo = future.get(15, TimeUnit.SECONDS);
            return buildRespVO(streamInfo);
        } catch (Exception e) {
            log.error("[Gb28181PlayStrategy] 国标点播异常: deviceId={}, channelId={}", deviceId, channelId, e);
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_PLAY_START_FAILED, e.getMessage());
        }
    }

    @Override
    public void stopPlay(VideoChannelDO channelDO) {
        log.info("[Gb28181PlayStrategy] 停止国标点播: deviceId={}, channelId={}", channelDO.getDeviceId(), channelDO.getChannelId());
        // 信令终止与资源释放由底层 session 和 bye 指令完成
    }

    private VideoPlayRespVO buildRespVO(StreamInfo info) {
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
    }
}
