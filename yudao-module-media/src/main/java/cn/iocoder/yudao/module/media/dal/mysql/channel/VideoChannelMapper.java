package cn.iocoder.yudao.module.media.dal.mysql.channel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频监控 - 设备通道 Mapper
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoChannelMapper extends BaseMapperX<VideoChannelDO> {

    default VideoChannelDO selectByDeviceIdAndChannelId(String deviceId, String channelId) {
        return selectOne(new LambdaQueryWrapperX<VideoChannelDO>()
                .eq(VideoChannelDO::getDeviceId, deviceId)
                .eq(VideoChannelDO::getChannelId, channelId));
    }

    default List<VideoChannelDO> selectListByDeviceId(String deviceId) {
        return selectList(new LambdaQueryWrapperX<VideoChannelDO>()
                .eq(VideoChannelDO::getDeviceId, deviceId));
    }

    default List<VideoChannelDO> selectOnlineListByDeviceId(String deviceId) {
        return selectList(new LambdaQueryWrapperX<VideoChannelDO>()
                .eq(VideoChannelDO::getDeviceId, deviceId)
                .eq(VideoChannelDO::getStatus, 1));
    }

    default VideoChannelDO selectByStreamId(String streamId) {
        return selectOne(VideoChannelDO::getStreamId, streamId);
    }
}
