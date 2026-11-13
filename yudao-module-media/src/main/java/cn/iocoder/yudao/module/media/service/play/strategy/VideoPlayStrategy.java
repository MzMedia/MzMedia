package cn.iocoder.yudao.module.media.service.play.strategy;

import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;

/**
 * 视频多协议拉流点播策略接口
 *
 * @author MzMedia Architect
 */
public interface VideoPlayStrategy {

    /**
     * 该策略支持的设备协议类型
     */
    VideoDeviceTypeEnum getSupportType();

    /**
     * 发起实时点播拉流
     *
     * @param channel 通道数据实体
     * @return 统一流媒体播放地址集合
     */
    VideoPlayRespVO startPlay(VideoChannelDO channel);

    /**
     * 停止实时点播/断开流
     *
     * @param channel 通道数据实体
     */
    void stopPlay(VideoChannelDO channel);
}
