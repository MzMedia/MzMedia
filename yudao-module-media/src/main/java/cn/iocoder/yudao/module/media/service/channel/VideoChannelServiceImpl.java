package cn.iocoder.yudao.module.media.service.channel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelPageReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.dal.mysql.channel.VideoChannelMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 视频通道服务实现类
 *
 * @author MzMedia Architect
 */
@Slf4j
@Service
public class VideoChannelServiceImpl implements VideoChannelService {

    @Resource
    private VideoChannelMapper videoChannelMapper;

    @Override
    public VideoChannelDO getChannel(Long id) {
        return videoChannelMapper.selectById(id);
    }

    @Override
    public VideoChannelDO getChannelByDeviceIdAndChannelId(String deviceId, String channelId) {
        return videoChannelMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
    }

    @Override
    public PageResult<VideoChannelDO> getChannelPage(VideoChannelPageReqVO pageReqVO) {
        return videoChannelMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<VideoChannelDO>()
                .eqIfPresent(VideoChannelDO::getDeviceId, pageReqVO.getDeviceId())
                .likeIfPresent(VideoChannelDO::getChannelId, pageReqVO.getChannelId())
                .likeIfPresent(VideoChannelDO::getName, pageReqVO.getName())
                .eqIfPresent(VideoChannelDO::getStatus, pageReqVO.getStatus())
                .eqIfPresent(VideoChannelDO::getPtzType, pageReqVO.getPtzType())
                .eqIfPresent(VideoChannelDO::getDataType, pageReqVO.getDataType())
                .orderByAsc(VideoChannelDO::getChannelId));
    }

    @Override
    public List<VideoChannelDO> getChannelListByDeviceId(String deviceId) {
        return videoChannelMapper.selectListByDeviceId(deviceId);
    }

    @Override
    public List<VideoChannelDO> getChannelListWithCoordinates() {
        return videoChannelMapper.selectList(new LambdaQueryWrapperX<VideoChannelDO>()
                .isNotNull(VideoChannelDO::getLongitude)
                .isNotNull(VideoChannelDO::getLatitude));
    }
}
