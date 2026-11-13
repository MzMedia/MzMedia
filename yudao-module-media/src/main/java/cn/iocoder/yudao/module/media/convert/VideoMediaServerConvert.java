package cn.iocoder.yudao.module.media.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerRespVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VideoMediaServerConvert {

    VideoMediaServerConvert INSTANCE = Mappers.getMapper(VideoMediaServerConvert.class);

    VideoMediaServerDO convert(VideoMediaServerSaveReqVO bean);

    VideoMediaServerRespVO convert(VideoMediaServerDO bean);

    List<VideoMediaServerRespVO> convertList(List<VideoMediaServerDO> list);

    PageResult<VideoMediaServerRespVO> convertPage(PageResult<VideoMediaServerDO> page);
}
