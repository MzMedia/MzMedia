package cn.iocoder.yudao.module.media.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 视频通道 MapStruct 转换器
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoChannelConvert {

    VideoChannelConvert INSTANCE = Mappers.getMapper(VideoChannelConvert.class);

    VideoChannelRespVO convert(VideoChannelDO bean);

    List<VideoChannelRespVO> convertList(List<VideoChannelDO> list);

    PageResult<VideoChannelRespVO> convertPage(PageResult<VideoChannelDO> page);
}
