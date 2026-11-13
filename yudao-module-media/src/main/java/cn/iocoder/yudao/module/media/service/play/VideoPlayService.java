package cn.iocoder.yudao.module.media.service.play;

import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayReqVO;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;

/**
 * 视频实时点播统一门面服务接口
 *
 * @author MzMedia Architect
 */
public interface VideoPlayService {

    /**
     * 发起通道点播拉流
     *
     * @param reqVO 点播请求参数
     * @return 统一流媒体播放地址
     */
    VideoPlayRespVO startPlay(VideoPlayReqVO reqVO);

    /**
     * 停止通道点播拉流
     *
     * @param channelId 通道主键 ID
     */
    void stopPlay(Long channelId);
}
