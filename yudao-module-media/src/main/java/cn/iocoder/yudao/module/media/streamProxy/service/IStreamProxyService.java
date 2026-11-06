package cn.iocoder.yudao.module.media.streamProxy.service;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.streamProxy.bean.StreamProxy;
import cn.iocoder.yudao.module.media.vmanager.bean.ResourceBaseInfo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface IStreamProxyService {

    /**
     * 分页查询
     * @param page
     * @param count
     * @return
     */
    PageInfo<StreamProxy> getAll(Integer page, Integer count, String query, Boolean pulling,String mediaServerId);

    /**
     * 删除视频代理
     * @param app
     * @param stream
     */
    void delteByAppAndStream(String app, String stream);

    /**
     * 启用视频代理
     * @param app
     * @param stream
     * @return
     */
    void startByAppAndStream(String app, String stream, ErrorCallback<StreamInfo> callback);

    /**
     * 停用用视频代理
     * @param app
     * @param stream
     * @return
     */
    void stopByAppAndStream(String app, String stream);

    /**
     * 获取ffmpeg.cmd模板
     *
     * @return
     */
    Map<String, String> getFFmpegCMDs(MediaServer mediaServerItem);

    /**
     * 根据app与stream获取streamProxy
     * @return
     */
    StreamProxy getStreamProxyByAppAndStream(String app, String streamId);


    /**
     * 新的节点加入
     * @param mediaServer
     * @return
     */
    void zlmServerOnline(MediaServer mediaServer);

    /**
     * 节点离线
     * @param mediaServer
     * @return
     */
    void zlmServerOffline(MediaServer mediaServer);

    /**
     * 更新代理流
     */
    boolean update(StreamProxy streamProxyItem);

    /**
     * 获取统计信息
     * @return
     */
    ResourceBaseInfo getOverview();

    void add(StreamProxy streamProxy);

    StreamProxy getStreamProxy(int id);

    void delete(int id);

}
