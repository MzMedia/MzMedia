package cn.iocoder.yudao.module.media.service.mediaserver;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoMediaServerConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import cn.iocoder.yudao.module.media.dal.mysql.mediaserver.VideoMediaServerMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VideoMediaServerServiceImpl implements VideoMediaServerService {

    @Resource
    private VideoMediaServerMapper videoMediaServerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMediaServer(VideoMediaServerSaveReqVO reqVO) {
        VideoMediaServerDO exist = videoMediaServerMapper.selectById(reqVO.getId());
        VideoMediaServerDO mediaServerDO = VideoMediaServerConvert.INSTANCE.convert(reqVO);
        if (exist == null) {
            mediaServerDO.setStatus(0); // 初始离线，等待心跳
            videoMediaServerMapper.insert(mediaServerDO);
        } else {
            videoMediaServerMapper.updateById(mediaServerDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMediaServer(String id) {
        VideoMediaServerDO server = videoMediaServerMapper.selectById(id);
        if (server == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.MEDIA_SERVER_NOT_FOUND);
        }
        videoMediaServerMapper.deleteById(id);
    }

    @Override
    public VideoMediaServerDO getMediaServer(String id) {
        return videoMediaServerMapper.selectById(id);
    }

    @Override
    public PageResult<VideoMediaServerDO> getMediaServerPage(VideoMediaServerPageReqVO reqVO) {
        return videoMediaServerMapper.selectPage(reqVO, new LambdaQueryWrapperX<VideoMediaServerDO>()
                .likeIfPresent(VideoMediaServerDO::getId, reqVO.getId())
                .likeIfPresent(VideoMediaServerDO::getIp, reqVO.getIp())
                .eqIfPresent(VideoMediaServerDO::getStatus, reqVO.getStatus())
                .orderByDesc(VideoMediaServerDO::getCreateTime));
    }

    @Override
    public VideoMediaServerDO getDefaultMediaServer() {
        VideoMediaServerDO server = videoMediaServerMapper.selectDefaultServer();
        if (server == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.MEDIA_SERVER_NOT_FOUND);
        }
        return server;
    }

    @Override
    public List<VideoMediaServerDO> getMediaServerList() {
        return videoMediaServerMapper.selectList();
    }

    @Override
    public List<VideoMediaServerDO> getOnlineList() {
        return videoMediaServerMapper.selectOnlineList();
    }

    @Override
    public void updateHeartbeat(String id) {
        VideoMediaServerDO server = videoMediaServerMapper.selectById(id);
        if (server != null) {
            server.setLastKeepaliveTime(LocalDateTime.now());
            server.setStatus(1); // 在线
            videoMediaServerMapper.updateById(server);
        }
    }
}
