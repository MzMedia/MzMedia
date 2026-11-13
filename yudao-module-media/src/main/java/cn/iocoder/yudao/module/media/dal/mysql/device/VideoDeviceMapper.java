package cn.iocoder.yudao.module.media.dal.mysql.device;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频监控 - 接入设备 Mapper
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoDeviceMapper extends BaseMapperX<VideoDeviceDO> {

    default VideoDeviceDO selectByDeviceId(String deviceId) {
        return selectOne(VideoDeviceDO::getDeviceId, deviceId);
    }

    default List<VideoDeviceDO> selectOnlineList() {
        return selectList(new LambdaQueryWrapperX<VideoDeviceDO>()
                .eq(VideoDeviceDO::getOnLine, 1));
    }

    default List<VideoDeviceDO> selectListByMediaServerId(String mediaServerId) {
        return selectList(new LambdaQueryWrapperX<VideoDeviceDO>()
                .eq(VideoDeviceDO::getMediaServerId, mediaServerId));
    }
}
