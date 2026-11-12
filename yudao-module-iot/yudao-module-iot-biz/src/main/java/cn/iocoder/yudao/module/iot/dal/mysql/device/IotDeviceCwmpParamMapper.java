package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpParamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IoT 设备 CWMP (TR-069) 参数快照 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface IotDeviceCwmpParamMapper extends BaseMapperX<IotDeviceCwmpParamDO> {

    default List<IotDeviceCwmpParamDO> selectListByDeviceIdAndPathPrefix(Long deviceId, String pathPrefix) {
        return selectList(new LambdaQueryWrapperX<IotDeviceCwmpParamDO>()
                .eq(IotDeviceCwmpParamDO::getDeviceId, deviceId)
                .likeIfPresent(IotDeviceCwmpParamDO::getPath, pathPrefix));
    }

    default IotDeviceCwmpParamDO selectByDeviceIdAndPath(Long deviceId, String path) {
        return selectOne(new LambdaQueryWrapperX<IotDeviceCwmpParamDO>()
                .eq(IotDeviceCwmpParamDO::getDeviceId, deviceId)
                .eq(IotDeviceCwmpParamDO::getPath, path));
    }

}
