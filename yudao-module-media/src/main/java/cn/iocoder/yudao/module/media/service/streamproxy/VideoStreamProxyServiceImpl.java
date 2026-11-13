package cn.iocoder.yudao.module.media.service.streamproxy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoStreamProxyConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import cn.iocoder.yudao.module.media.dal.mysql.stream.VideoStreamProxyMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class VideoStreamProxyServiceImpl implements VideoStreamProxyService {

    @Resource
    private VideoStreamProxyMapper videoStreamProxyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStreamProxy(VideoStreamProxySaveReqVO reqVO) {
        // 校验 app + stream 唯一性
        VideoStreamProxyDO exist = videoStreamProxyMapper.selectByAppAndStream(reqVO.getApp(), reqVO.getStream());
        if (exist != null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.STREAM_NOT_FOUND, "该应用名与流ID已存在");
        }

        VideoStreamProxyDO proxyDO = VideoStreamProxyConvert.INSTANCE.convert(reqVO);
        if (proxyDO.getEnable() == null) {
            proxyDO.setEnable(1);
        }
        videoStreamProxyMapper.insert(proxyDO);
        return proxyDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStreamProxy(VideoStreamProxySaveReqVO reqVO) {
        validateStreamProxyExists(reqVO.getId());
        VideoStreamProxyDO proxyDO = VideoStreamProxyConvert.INSTANCE.convert(reqVO);
        videoStreamProxyMapper.updateById(proxyDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStreamProxy(Long id) {
        validateStreamProxyExists(id);
        videoStreamProxyMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStreamProxy(String app, String stream) {
        VideoStreamProxyDO proxy = getStreamProxyByAppAndStream(app, stream);
        if (proxy != null) {
            videoStreamProxyMapper.deleteById(proxy.getId());
        }
    }

    @Override
    public VideoStreamProxyDO getStreamProxy(Long id) {
        return videoStreamProxyMapper.selectById(id);
    }

    @Override
    public VideoStreamProxyDO getStreamProxyByAppAndStream(String app, String stream) {
        return videoStreamProxyMapper.selectByAppAndStream(app, stream);
    }

    @Override
    public PageResult<VideoStreamProxyDO> getStreamProxyPage(VideoStreamProxyPageReqVO reqVO) {
        return videoStreamProxyMapper.selectPage(reqVO, new LambdaQueryWrapperX<VideoStreamProxyDO>()
                .eqIfPresent(VideoStreamProxyDO::getApp, reqVO.getApp())
                .likeIfPresent(VideoStreamProxyDO::getStream, reqVO.getStream())
                .likeIfPresent(VideoStreamProxyDO::getName, reqVO.getName())
                .eqIfPresent(VideoStreamProxyDO::getEnable, reqVO.getEnable())
                .orderByDesc(VideoStreamProxyDO::getId));
    }

    @Override
    public void startStreamProxy(Long id) {
        VideoStreamProxyDO proxy = validateStreamProxyExists(id);
        proxy.setEnable(1);
        videoStreamProxyMapper.updateById(proxy);
        log.info("[拉流代理] 启动代理: app={}, stream={}, url={}", proxy.getApp(), proxy.getStream(), proxy.getUrl());
    }

    @Override
    public void startStreamProxy(String app, String stream) {
        VideoStreamProxyDO proxy = getStreamProxyByAppAndStream(app, stream);
        if (proxy != null) {
            startStreamProxy(proxy.getId());
        }
    }

    @Override
    public void stopStreamProxy(Long id) {
        VideoStreamProxyDO proxy = validateStreamProxyExists(id);
        proxy.setEnable(0);
        videoStreamProxyMapper.updateById(proxy);
        log.info("[拉流代理] 停止代理: app={}, stream={}", proxy.getApp(), proxy.getStream());
    }

    @Override
    public void stopStreamProxy(String app, String stream) {
        VideoStreamProxyDO proxy = getStreamProxyByAppAndStream(app, stream);
        if (proxy != null) {
            stopStreamProxy(proxy.getId());
        }
    }

    private VideoStreamProxyDO validateStreamProxyExists(Long id) {
        if (id == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.STREAM_NOT_FOUND);
        }
        VideoStreamProxyDO proxy = videoStreamProxyMapper.selectById(id);
        if (proxy == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.STREAM_NOT_FOUND);
        }
        return proxy;
    }
}
