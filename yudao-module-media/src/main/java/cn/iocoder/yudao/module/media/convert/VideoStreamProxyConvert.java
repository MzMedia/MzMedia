package cn.iocoder.yudao.module.media.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyRespVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VideoStreamProxyConvert {

    VideoStreamProxyConvert INSTANCE = Mappers.getMapper(VideoStreamProxyConvert.class);

    VideoStreamProxyDO convert(VideoStreamProxySaveReqVO bean);

    VideoStreamProxyRespVO convert(VideoStreamProxyDO bean);

    List<VideoStreamProxyRespVO> convertList(List<VideoStreamProxyDO> list);

    PageResult<VideoStreamProxyRespVO> convertPage(PageResult<VideoStreamProxyDO> page);
}
