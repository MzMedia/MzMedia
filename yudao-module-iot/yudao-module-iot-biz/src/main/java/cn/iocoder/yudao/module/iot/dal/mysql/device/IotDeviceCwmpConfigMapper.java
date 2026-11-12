package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IoT 设备 CWMP (TR-069) 配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface IotDeviceCwmpConfigMapper extends BaseMapperX<IotDeviceCwmpConfigDO> {

    default IotDeviceCwmpConfigDO selectByDeviceId(Long deviceId) {
        return selectOne(IotDeviceCwmpConfigDO::getDeviceId, deviceId);
    }

}
