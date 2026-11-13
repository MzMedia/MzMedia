package cn.iocoder.yudao.module.media.service.play;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayReqVO;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.dal.mysql.channel.VideoChannelMapper;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.service.play.strategy.VideoPlayStrategy;
import cn.iocoder.yudao.module.media.service.play.strategy.VideoPlayStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 视频实时点播统一门面服务实现
 *
 * @author MzMedia Architect
 */
@Slf4j
@Service
public class VideoPlayServiceImpl implements VideoPlayService {

    @Resource
    private VideoChannelMapper videoChannelMapper;

    @Resource
    private VideoPlayStrategyFactory playStrategyFactory;

    @Override
    public VideoPlayRespVO startPlay(VideoPlayReqVO reqVO) {
        // 1. 查询通道元数据
        VideoChannelDO channel = null;
        if (reqVO.getChannelId() != null) {
            channel = videoChannelMapper.selectById(reqVO.getChannelId());
        } else if (reqVO.getDeviceId() != null && reqVO.getChannelCode() != null) {
            channel = videoChannelMapper.selectByDeviceIdAndChannelId(reqVO.getDeviceId(), reqVO.getChannelCode());
        }

        if (channel == null) {
            // 兼容直接使用请求传入的 deviceId & channelCode 虚拟构造通道
            if (reqVO.getDeviceId() != null && reqVO.getChannelCode() != null) {
                channel = new VideoChannelDO();
                channel.setDeviceId(reqVO.getDeviceId());
                channel.setChannelId(reqVO.getChannelCode());
                channel.setDataType(VideoDeviceTypeEnum.GB28181.getCode());
                channel.setStatus(1);
            } else {
                throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_CHANNEL_NOT_EXISTS);
            }
        }

        // 2. 获取该协议类型对应的点播策略
        VideoDeviceTypeEnum deviceType = VideoDeviceTypeEnum.getByCode(channel.getDataType());
        VideoPlayStrategy strategy = playStrategyFactory.getStrategy(deviceType);

        log.info("[VideoPlayService] 发起拉流: channelId={}, 设备类型={}, 编码={}",
                channel.getId(), deviceType != null ? deviceType.getName() : "默认", channel.getChannelId());

        return strategy.startPlay(channel);
    }

    @Override
    public void stopPlay(Long channelId) {
        VideoChannelDO channel = videoChannelMapper.selectById(channelId);
        if (channel == null) {
            return;
        }
        VideoDeviceTypeEnum deviceType = VideoDeviceTypeEnum.getByCode(channel.getDataType());
        VideoPlayStrategy strategy = playStrategyFactory.getStrategy(deviceType);
        strategy.stopPlay(channel);
    }
}
