package cn.iocoder.yudao.module.iot.service.device;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp.IotDeviceCwmpConfigSaveReqVO;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp.IotDeviceCwmpWifiVO;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.IotCwmpConnectionRequestClient;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser.CwmpSoapParser;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpParamDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceDO;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceCwmpConfigMapper;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceCwmpParamMapper;
import cn.iocoder.yudao.module.iot.service.device.message.IotDeviceMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * IoT 设备 CWMP (TR-069) 业务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class IotDeviceCwmpServiceImpl implements IotDeviceCwmpService {

    @Resource
    private IotDeviceCwmpConfigMapper cwmpConfigMapper;

    @Resource
    private IotDeviceCwmpParamMapper cwmpParamMapper;

    @Resource
    private IotDeviceService deviceService;

    @Resource
    private IotDeviceMessageService deviceMessageService;

    @Override
    public void saveCwmpConfig(IotDeviceCwmpConfigSaveReqVO saveReqVO) {
        deviceService.validateDeviceExists(saveReqVO.getDeviceId());
        IotDeviceCwmpConfigDO exist = cwmpConfigMapper.selectByDeviceId(saveReqVO.getDeviceId());
        if (exist == null) {
            IotDeviceCwmpConfigDO config = BeanUtils.toBean(saveReqVO, IotDeviceCwmpConfigDO.class);
            cwmpConfigMapper.insert(config);
        } else {
            IotDeviceCwmpConfigDO updateObj = BeanUtils.toBean(saveReqVO, IotDeviceCwmpConfigDO.class);
            updateObj.setId(exist.getId());
            cwmpConfigMapper.updateById(updateObj);
        }
    }

    @Override
    public IotDeviceCwmpConfigDO getCwmpConfigByDeviceId(Long deviceId) {
        return cwmpConfigMapper.selectByDeviceId(deviceId);
    }

    @Override
    public List<IotDeviceCwmpParamDO> getCwmpParams(Long deviceId, String pathPrefix) {
        return cwmpParamMapper.selectListByDeviceIdAndPathPrefix(deviceId, pathPrefix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateParams(Long deviceId, List<IotDeviceCwmpParamDO> params) {
        if (CollUtil.isEmpty(params)) {
            return;
        }
        for (IotDeviceCwmpParamDO param : params) {
            IotDeviceCwmpParamDO exist = cwmpParamMapper.selectByDeviceIdAndPath(deviceId, param.getPath());
            if (exist == null) {
                param.setDeviceId(deviceId);
                cwmpParamMapper.insert(param);
            } else {
                exist.setValue(param.getValue());
                exist.setType(param.getType());
                if (param.getWritable() != null) {
                    exist.setWritable(param.getWritable());
                }
                cwmpParamMapper.updateById(exist);
            }
        }
    }

    @Override
    public CompletableFuture<String> refreshParameterNames(Long deviceId, String path, boolean nextLevel) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        String targetPath = StrUtil.blankToDefault(path, "InternetGatewayDevice.");
        String soapXml = CwmpSoapBuilder.buildGetParameterNames(targetPath, nextLevel);
        sendRawSoapDownstream(device, soapXml);
        return CompletableFuture.completedFuture("Command enqueued");
    }

    @Override
    public CompletableFuture<String> refreshParameterValues(Long deviceId, List<String> paths) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        if (CollUtil.isEmpty(paths)) {
            paths = Collections.singletonList("InternetGatewayDevice.DeviceInfo.");
        }
        String soapXml = CwmpSoapBuilder.buildGetParameterValues(paths);
        sendRawSoapDownstream(device, soapXml);
        return CompletableFuture.completedFuture("Command enqueued");
    }

    @Override
    public CompletableFuture<String> setParameterValues(Long deviceId, Map<String, String> params) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        Assert.notEmpty(params, "修改参数键值对不能为空");
        String soapXml = CwmpSoapBuilder.buildSetParameterValues(params);
        sendRawSoapDownstream(device, soapXml);

        // 同步更新本地数据库快照
        List<IotDeviceCwmpParamDO> updateList = new ArrayList<>();
        params.forEach((k, v) -> updateList.add(IotDeviceCwmpParamDO.builder()
                .deviceId(deviceId)
                .path(k)
                .value(v)
                .writable(true)
                .build()));
        saveOrUpdateParams(deviceId, updateList);

        return CompletableFuture.completedFuture("Command enqueued");
    }

    @Override
    public CompletableFuture<String> reboot(Long deviceId) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        String soapXml = CwmpSoapBuilder.buildReboot("USER_REBOOT_" + System.currentTimeMillis());
        sendRawSoapDownstream(device, soapXml);
        return CompletableFuture.completedFuture("Reboot command enqueued");
    }

    @Override
    public CompletableFuture<String> factoryReset(Long deviceId) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        String soapXml = CwmpSoapBuilder.buildFactoryReset();
        sendRawSoapDownstream(device, soapXml);
        return CompletableFuture.completedFuture("FactoryReset command enqueued");
    }

    @Override
    public boolean triggerConnectionRequest(Long deviceId) {
        IotDeviceCwmpConfigDO config = cwmpConfigMapper.selectByDeviceId(deviceId);
        if (config == null || StrUtil.isBlank(config.getConnectionRequestUrl())) {
            log.warn("[triggerConnectionRequest][设备 {} 未配置 ConnectionRequestURL]", deviceId);
            return false;
        }

        // 调用 Connection Request Client 唤醒
        IotCwmpConnectionRequestClient client = new IotCwmpConnectionRequestClient();
        return client.trigger(config.getConnectionRequestUrl(),
                config.getConnectionRequestUsername(),
                config.getConnectionRequestPassword());
    }

    @Override
    public IotDeviceCwmpWifiVO getWifiConfig(Long deviceId) {
        List<IotDeviceCwmpParamDO> params = cwmpParamMapper.selectListByDeviceIdAndPathPrefix(deviceId, "WLANConfiguration.1.");
        if (params.isEmpty()) {
            params = cwmpParamMapper.selectListByDeviceIdAndPathPrefix(deviceId, "WiFi.SSID.1.");
        }

        IotDeviceCwmpWifiVO vo = IotDeviceCwmpWifiVO.builder()
                .band("2.4G")
                .enable(true)
                .build();

        for (IotDeviceCwmpParamDO p : params) {
            String path = p.getPath();
            String val = p.getValue();
            if (path.endsWith("SSID")) {
                vo.setSsid(val);
            } else if (path.endsWith("KeyPassphrase")) {
                vo.setPassword(val);
            } else if (path.endsWith("Enable")) {
                vo.setEnable("1".equals(val) || "true".equalsIgnoreCase(val));
            } else if (path.endsWith("Channel")) {
                if (StrUtil.isNotBlank(val)) {
                    vo.setChannel(Integer.parseInt(val));
                }
            } else if (path.endsWith("BeaconType") || path.endsWith("Security.ModeEnabled")) {
                vo.setSecurityMode(val);
            }
        }
        return vo;
    }

    @Override
    public CompletableFuture<String> setWifiConfig(Long deviceId, IotDeviceCwmpWifiVO wifiVO) {
        IotDeviceCwmpConfigDO config = cwmpConfigMapper.selectByDeviceId(deviceId);
        boolean isTr181 = config != null && "TR181".equalsIgnoreCase(config.getDataModelType());

        Map<String, String> sets = new HashMap<>();
        if (isTr181) {
            if (StrUtil.isNotBlank(wifiVO.getSsid())) {
                sets.put("Device.WiFi.SSID.1.SSID", wifiVO.getSsid());
            }
            if (StrUtil.isNotBlank(wifiVO.getPassword())) {
                sets.put("Device.WiFi.AccessPoint.1.Security.KeyPassphrase", wifiVO.getPassword());
            }
            if (wifiVO.getEnable() != null) {
                sets.put("Device.WiFi.SSID.1.Enable", wifiVO.getEnable() ? "true" : "false");
            }
        } else {
            // TR-098
            if (StrUtil.isNotBlank(wifiVO.getSsid())) {
                sets.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID", wifiVO.getSsid());
            }
            if (StrUtil.isNotBlank(wifiVO.getPassword())) {
                sets.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.KeyPassphrase", wifiVO.getPassword());
            }
            if (wifiVO.getEnable() != null) {
                sets.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.Enable", wifiVO.getEnable() ? "1" : "0");
            }
        }

        return setParameterValues(deviceId, sets);
    }

    @Override
    public CompletableFuture<String> runPingDiagnostics(Long deviceId, String host, Integer count) {
        IotDeviceCwmpConfigDO config = cwmpConfigMapper.selectByDeviceId(deviceId);
        boolean isTr181 = config != null && "TR181".equalsIgnoreCase(config.getDataModelType());

        Map<String, String> sets = new HashMap<>();
        String prefix = isTr181 ? "Device.IP.Diagnostics.IPPing." : "InternetGatewayDevice.IPPingDiagnostics.";
        sets.put(prefix + "Host", host);
        sets.put(prefix + "NumberOfRepetitions", String.valueOf(count != null ? count : 4));
        sets.put(prefix + "DiagnosticsState", "Requested");

        return setParameterValues(deviceId, sets);
    }

    private void sendRawSoapDownstream(IotDeviceDO device, String soapXml) {
        IotDeviceMessage message = IotDeviceMessage.builder()
                .deviceId(device.getId())
                .tenantId(device.getTenantId())
                .method(IotDeviceMessageMethodEnum.RAW_PUSH.getMethod())
                .payload(soapXml.getBytes(StandardCharsets.UTF_8))
                .build();
        deviceMessageService.sendDeviceMessage(message, device);
    }

}
