package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspParamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IoT 设备 USP (TR-369) 参数快照 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface IotDeviceUspParamMapper extends BaseMapperX<IotDeviceUspParamDO> {

    default List<IotDeviceUspParamDO> selectListByDeviceIdAndPathPrefix(Long deviceId, String pathPrefix) {
        return selectList(new LambdaQueryWrapperX<IotDeviceUspParamDO>()
                .eq(IotDeviceUspParamDO::getDeviceId, deviceId)
                .likeIfPresent(IotDeviceUspParamDO::getPath, pathPrefix));
    }

    default IotDeviceUspParamDO selectByDeviceIdAndPath(Long deviceId, String path) {
        return selectOne(new LambdaQueryWrapperX<IotDeviceUspParamDO>()
                .eq(IotDeviceUspParamDO::getDeviceId, deviceId)
                .eq(IotDeviceUspParamDO::getPath, path));
    }

}
