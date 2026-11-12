package cn.iocoder.yudao.module.iot.service.device;

import cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp.IotDeviceCwmpConfigSaveReqVO;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp.IotDeviceCwmpWifiVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpParamDO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * IoT 设备 CWMP (TR-069) 业务 Service 接口
 *
 * @author 芋道源码
 */
public interface IotDeviceCwmpService {

    /**
     * 保存或更新设备 CWMP 连接配置
     */
    void saveCwmpConfig(IotDeviceCwmpConfigSaveReqVO saveReqVO);

    /**
     * 根据设备编号获取 CWMP 连接配置
     */
    IotDeviceCwmpConfigDO getCwmpConfigByDeviceId(Long deviceId);

    /**
     * 获取设备 CWMP 参数列表
     */
    List<IotDeviceCwmpParamDO> getCwmpParams(Long deviceId, String pathPrefix);

    /**
     * 批量保存/更新参数快照
     */
    void saveOrUpdateParams(Long deviceId, List<IotDeviceCwmpParamDO> params);

    /**
     * 动态触发下发 GetParameterNames 发现参数树
     */
    CompletableFuture<String> refreshParameterNames(Long deviceId, String path, boolean nextLevel);

    /**
     * 动态触发下发 GetParameterValues 读取最新参数值
     */
    CompletableFuture<String> refreshParameterValues(Long deviceId, List<String> paths);

    /**
     * 下发 SetParameterValues 修改设备参数
     */
    CompletableFuture<String> setParameterValues(Long deviceId, Map<String, String> params);

    /**
     * 远程重启设备
     */
    CompletableFuture<String> reboot(Long deviceId);

    /**
     * 恢复出厂设置
     */
    CompletableFuture<String> factoryReset(Long deviceId);

    /**
     * 触发反向连接请求 (Connection Request) 唤醒设备
     */
    boolean triggerConnectionRequest(Long deviceId);

    /**
     * 读取 Wi-Fi 配置
     */
    IotDeviceCwmpWifiVO getWifiConfig(Long deviceId);

    /**
     * 修改 Wi-Fi 配置
     */
    CompletableFuture<String> setWifiConfig(Long deviceId, IotDeviceCwmpWifiVO wifiVO);

    /**
     * 执行 Ping 诊断测试
     */
    CompletableFuture<String> runPingDiagnostics(Long deviceId, String host, Integer count);

}
