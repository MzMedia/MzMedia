package cn.iocoder.yudao.module.media.service.mediaserver;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;

import java.util.List;

/**
 * 视频监控 - 流媒体节点 Service 接口
 *
 * @author MzMedia Architect
 */
public interface VideoMediaServerService {

    /**
     * 保存流媒体节点 (新增或更新)
     */
    void saveMediaServer(VideoMediaServerSaveReqVO reqVO);

    /**
     * 删除流媒体节点
     */
    void deleteMediaServer(String id);

    /**
     * 获得指定流媒体节点
     */
    VideoMediaServerDO getMediaServer(String id);

    /**
     * 获得流媒体节点分页
     */
    PageResult<VideoMediaServerDO> getMediaServerPage(VideoMediaServerPageReqVO pageReqVO);

    /**
     * 获得默认流媒体节点
     */
    VideoMediaServerDO getDefaultMediaServer();

    /**
     * 获得所有流媒体节点列表
     */
    List<VideoMediaServerDO> getMediaServerList();

    /**
     * 获得所有在线流媒体节点
     */
    List<VideoMediaServerDO> getOnlineList();

    /**
     * 更新节点心跳
     */
    void updateHeartbeat(String id);
}
