package cn.iocoder.yudao.module.media.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceRespVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 视频设备 MapStruct 转换器
 *
 * @author MzMedia Architect
 */
@Mapper
public interface VideoDeviceConvert {

    VideoDeviceConvert INSTANCE = Mappers.getMapper(VideoDeviceConvert.class);

    VideoDeviceDO convert(VideoDeviceSaveReqVO bean);

    VideoDeviceRespVO convert(VideoDeviceDO bean);

    List<VideoDeviceRespVO> convertList(List<VideoDeviceDO> list);

    PageResult<VideoDeviceRespVO> convertPage(PageResult<VideoDeviceDO> page);
}
