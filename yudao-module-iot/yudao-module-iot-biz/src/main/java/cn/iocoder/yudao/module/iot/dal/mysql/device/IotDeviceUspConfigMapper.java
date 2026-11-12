package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IoT 设备 USP (TR-369) 配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface IotDeviceUspConfigMapper extends BaseMapperX<IotDeviceUspConfigDO> {

    default IotDeviceUspConfigDO selectByDeviceId(Long deviceId) {
        return selectOne(IotDeviceUspConfigDO::getDeviceId, deviceId);
    }

    default IotDeviceUspConfigDO selectByAgentEndpointId(String endpointId) {
        return selectOne(IotDeviceUspConfigDO::getAgentEndpointId, endpointId);
    }

}
