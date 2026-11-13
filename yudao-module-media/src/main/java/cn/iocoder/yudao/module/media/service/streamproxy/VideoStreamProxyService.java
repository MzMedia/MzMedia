package cn.iocoder.yudao.module.media.service.streamproxy;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;

/**
 * 视频监控 - 拉流代理 Service 接口
 *
 * @author MzMedia Architect
 */
public interface VideoStreamProxyService {

    /**
     * 创建拉流代理
     */
    Long createStreamProxy(VideoStreamProxySaveReqVO reqVO);

    /**
     * 更新拉流代理
     */
    void updateStreamProxy(VideoStreamProxySaveReqVO reqVO);

    /**
     * 删除拉流代理
     */
    void deleteStreamProxy(Long id);

    /**
     * 根据 app 和 stream 删除拉流代理
     */
    void deleteStreamProxy(String app, String stream);

    /**
     * 获得指定拉流代理
     */
    VideoStreamProxyDO getStreamProxy(Long id);

    /**
     * 根据 app 与 stream 获得拉流代理
     */
    VideoStreamProxyDO getStreamProxyByAppAndStream(String app, String stream);

    /**
     * 获得拉流代理分页
     */
    PageResult<VideoStreamProxyDO> getStreamProxyPage(VideoStreamProxyPageReqVO reqVO);

    /**
     * 启动拉流代理 (下发至 ZLMediaKit)
     */
    void startStreamProxy(Long id);

    /**
     * 根据 app 和 stream 启动拉流代理
     */
    void startStreamProxy(String app, String stream);

    /**
     * 停止拉流代理
     */
    void stopStreamProxy(Long id);

    /**
     * 根据 app 和 stream 停止拉流代理
     */
    void stopStreamProxy(String app, String stream);
}
