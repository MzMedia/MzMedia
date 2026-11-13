package cn.iocoder.yudao.module.media.service.channel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelPageReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;

import java.util.List;

/**
 * 视频通道服务接口
 *
 * @author MzMedia Architect
 */
public interface VideoChannelService {

    /**
     * 根据主键查询通道
     *
     * @param id 主键 ID
     * @return 通道实体
     */
    VideoChannelDO getChannel(Long id);

    /**
     * 根据设备国标编号与通道国标编码查询通道
     *
     * @param deviceId 设备编号
     * @param channelId 通道编号
     * @return 通道实体
     */
    VideoChannelDO getChannelByDeviceIdAndChannelId(String deviceId, String channelId);

    /**
     * 分页查询视频通道
     *
     * @param pageReqVO 查询参数
     * @return 分页结果
     */
    PageResult<VideoChannelDO> getChannelPage(VideoChannelPageReqVO pageReqVO);

    /**
     * 查询指定设备下的全部通道列表
     *
     * @param deviceId 设备编号
     * @return 通道列表
     */
    List<VideoChannelDO> getChannelListByDeviceId(String deviceId);

    /**
     * 获得包含地图坐标的视频通道列表
     *
     * @return 通道列表
     */
    List<VideoChannelDO> getChannelListWithCoordinates();
}
