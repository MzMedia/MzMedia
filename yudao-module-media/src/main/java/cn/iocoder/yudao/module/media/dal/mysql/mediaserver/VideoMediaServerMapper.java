package cn.iocoder.yudao.module.media.dal.mysql.mediaserver;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频监控 - 流媒体节点 Mapper
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoMediaServerMapper extends BaseMapperX<VideoMediaServerDO> {

    default VideoMediaServerDO selectDefaultServer() {
        return selectOne(new LambdaQueryWrapperX<VideoMediaServerDO>()
                .eq(VideoMediaServerDO::getDefaultServer, 1)
                .last("LIMIT 1"));
    }

    default List<VideoMediaServerDO> selectOnlineList() {
        return selectList(new LambdaQueryWrapperX<VideoMediaServerDO>()
                .eq(VideoMediaServerDO::getStatus, 1));
    }
}
