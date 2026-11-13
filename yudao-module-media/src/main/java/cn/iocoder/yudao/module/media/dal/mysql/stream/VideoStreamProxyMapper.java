package cn.iocoder.yudao.module.media.dal.mysql.stream;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频监控 - 拉流代理 Mapper
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoStreamProxyMapper extends BaseMapperX<VideoStreamProxyDO> {

    default VideoStreamProxyDO selectByAppAndStream(String app, String stream) {
        return selectOne(new LambdaQueryWrapperX<VideoStreamProxyDO>()
                .eq(VideoStreamProxyDO::getApp, app)
                .eq(VideoStreamProxyDO::getStream, stream));
    }

    default List<VideoStreamProxyDO> selectAutoStartList() {
        return selectList(new LambdaQueryWrapperX<VideoStreamProxyDO>()
                .eq(VideoStreamProxyDO::getEnable, 1));
    }
}
