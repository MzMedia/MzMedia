package cn.iocoder.yudao.module.iot.service.device;

import cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp.IotDeviceUspConfigSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspParamDO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * IoT 设备 USP (TR-369) 业务 Service 接口
 *
 * @author 芋道源码
 */
public interface IotDeviceUspService {

    /**
     * 保存或更新设备 USP 连接配置
     */
    void saveUspConfig(IotDeviceUspConfigSaveReqVO saveReqVO);

    /**
     * 根据设备编号获取 USP 连接配置
     */
    IotDeviceUspConfigDO getUspConfigByDeviceId(Long deviceId);

    /**
     * 获取设备 USP 参数列表
     */
    List<IotDeviceUspParamDO> getUspParams(Long deviceId, String pathPrefix);

    /**
     * 批量保存/更新参数快照
     */
    void saveOrUpdateParams(Long deviceId, List<IotDeviceUspParamDO> params);

    /**
     * 下发 USP GET 请求获取最新参数值
     */
    CompletableFuture<String> uspGet(Long deviceId, List<String> paths);

    /**
     * 下发 USP SET 请求设置参数
     */
    CompletableFuture<String> uspSet(Long deviceId, Map<String, String> params);

    /**
     * 下发 USP OPERATE 执行操作命令
     */
    CompletableFuture<String> uspOperate(Long deviceId, String command, Map<String, String> inputArgs);

    /**
     * 远程重启设备 (Device.Reboot())
     */
    CompletableFuture<String> reboot(Long deviceId);

    /**
     * 恢复出厂设置 (Device.FactoryReset())
     */
    CompletableFuture<String> factoryReset(Long deviceId);

}
