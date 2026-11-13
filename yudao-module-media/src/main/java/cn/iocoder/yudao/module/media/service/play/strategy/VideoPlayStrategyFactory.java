package cn.iocoder.yudao.module.media.service.play.strategy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 视频拉流点播策略工厂
 *
 * @author MzMedia Architect
 */
@Component
public class VideoPlayStrategyFactory {

    private final Map<VideoDeviceTypeEnum, VideoPlayStrategy> strategyMap = new EnumMap<>(VideoDeviceTypeEnum.class);

    public VideoPlayStrategyFactory(List<VideoPlayStrategy> strategies) {
        for (VideoPlayStrategy strategy : strategies) {
            if (strategy.getSupportType() != null) {
                strategyMap.put(strategy.getSupportType(), strategy);
            }
        }
    }

    public VideoPlayStrategy getStrategy(VideoDeviceTypeEnum deviceType) {
        if (deviceType == null) {
            // 默认回退至 GB28181 策略
            deviceType = VideoDeviceTypeEnum.GB28181;
        }
        VideoPlayStrategy strategy = strategyMap.get(deviceType);
        if (strategy == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_UNSUPPORTED_PROTOCOL);
        }
        return strategy;
    }
}
