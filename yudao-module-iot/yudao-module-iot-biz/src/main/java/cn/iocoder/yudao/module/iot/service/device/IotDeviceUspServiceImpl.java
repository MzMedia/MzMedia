package cn.iocoder.yudao.module.iot.service.device;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp.IotDeviceUspConfigSaveReqVO;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.UspMsg;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspParamDO;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceUspConfigMapper;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceUspParamMapper;
import cn.iocoder.yudao.module.iot.service.device.message.IotDeviceMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * IoT 设备 USP (TR-369) 业务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class IotDeviceUspServiceImpl implements IotDeviceUspService {

    @Resource
    private IotDeviceUspConfigMapper uspConfigMapper;

    @Resource
    private IotDeviceUspParamMapper uspParamMapper;

    @Resource
    private IotDeviceService deviceService;

    @Resource
    private IotDeviceMessageService deviceMessageService;

    @Override
    public void saveUspConfig(IotDeviceUspConfigSaveReqVO saveReqVO) {
        deviceService.validateDeviceExists(saveReqVO.getDeviceId());
        IotDeviceUspConfigDO exist = uspConfigMapper.selectByDeviceId(saveReqVO.getDeviceId());
        if (exist == null) {
            IotDeviceUspConfigDO config = BeanUtils.toBean(saveReqVO, IotDeviceUspConfigDO.class);
            if (StrUtil.isBlank(config.getMqttTopicAgent())) {
                config.setMqttTopicAgent("agent/" + config.getAgentEndpointId());
            }
            if (StrUtil.isBlank(config.getMqttTopicController())) {
                config.setMqttTopicController("controller/" + config.getControllerEndpointId());
            }
            if (StrUtil.isBlank(config.getProtocolVersion())) {
                config.setProtocolVersion("1.3");
            }
            uspConfigMapper.insert(config);
        } else {
            IotDeviceUspConfigDO updateObj = BeanUtils.toBean(saveReqVO, IotDeviceUspConfigDO.class);
            updateObj.setId(exist.getId());
            uspConfigMapper.updateById(updateObj);
        }
    }

    @Override
    public IotDeviceUspConfigDO getUspConfigByDeviceId(Long deviceId) {
        return uspConfigMapper.selectByDeviceId(deviceId);
    }

    @Override
    public List<IotDeviceUspParamDO> getUspParams(Long deviceId, String pathPrefix) {
        return uspParamMapper.selectListByDeviceIdAndPathPrefix(deviceId, pathPrefix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateParams(Long deviceId, List<IotDeviceUspParamDO> params) {
        if (CollUtil.isEmpty(params)) {
            return;
        }
        for (IotDeviceUspParamDO param : params) {
            IotDeviceUspParamDO exist = uspParamMapper.selectByDeviceIdAndPath(deviceId, param.getPath());
            if (exist == null) {
                param.setDeviceId(deviceId);
                uspParamMapper.insert(param);
            } else {
                exist.setValue(param.getValue());
                exist.setType(param.getType());
                if (param.getWritable() != null) {
                    exist.setWritable(param.getWritable());
                }
                uspParamMapper.updateById(exist);
            }
        }
    }

    @Override
    public CompletableFuture<String> uspGet(Long deviceId, List<String> paths) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        if (CollUtil.isEmpty(paths)) {
            paths = Collections.singletonList("Device.DeviceInfo.");
        }

        UspMsg getMsg = UspMessageBuilder.buildGetMsg(paths);
        sendUspMsgDownstream(device, getMsg, "usp.get", paths);
        return CompletableFuture.completedFuture("USP Get sent");
    }

    @Override
    public CompletableFuture<String> uspSet(Long deviceId, Map<String, String> params) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        Assert.notEmpty(params, "修改参数键值对不能为空");

        UspMsg setMsg = UspMessageBuilder.buildSetMsg(params);
        sendUspMsgDownstream(device, setMsg, "usp.set", params);

        // 同步更新本地参数快照
        List<IotDeviceUspParamDO> updateList = new ArrayList<>();
        params.forEach((k, v) -> updateList.add(IotDeviceUspParamDO.builder()
                .deviceId(deviceId)
                .path(k)
                .value(v)
                .writable(true)
                .build()));
        saveOrUpdateParams(deviceId, updateList);

        return CompletableFuture.completedFuture("USP Set sent");
    }

    @Override
    public CompletableFuture<String> uspOperate(Long deviceId, String command, Map<String, String> inputArgs) {
        IotDeviceDO device = deviceService.validateDeviceExists(deviceId);
        Assert.notBlank(command, "操作命令不能为空");

        String cmdKey = "OPERATE_" + System.currentTimeMillis();
        Map<String, String> args = inputArgs != null ? inputArgs : Collections.emptyMap();
        UspMsg operateMsg = UspMessageBuilder.buildOperateMsg(command, cmdKey, args);

        Map<String, Object> methodParams = new HashMap<>();
        methodParams.put("command", command);
        methodParams.put("commandKey", cmdKey);
        methodParams.put("inputArgs", args);

        sendUspMsgDownstream(device, operateMsg, "usp.operate", methodParams);
        return CompletableFuture.completedFuture("USP Operate sent");
    }

    @Override
    public CompletableFuture<String> reboot(Long deviceId) {
        return uspOperate(deviceId, "Device.Reboot()", Collections.emptyMap());
    }

    @Override
    public CompletableFuture<String> factoryReset(Long deviceId) {
        return uspOperate(deviceId, "Device.FactoryReset()", Collections.emptyMap());
    }

    private void sendUspMsgDownstream(IotDeviceDO device, UspMsg uspMsg, String method, Object methodParams) {
        IotDeviceUspConfigDO config = uspConfigMapper.selectByDeviceId(device.getId());
        String toId = config != null && StrUtil.isNotBlank(config.getAgentEndpointId())
                ? config.getAgentEndpointId()
                : (StrUtil.isNotBlank(device.getSerialNumber()) ? device.getSerialNumber() : device.getDeviceName());
        String fromId = config != null && StrUtil.isNotBlank(config.getControllerEndpointId())
                ? config.getControllerEndpointId()
                : UspMessageBuilder.DEFAULT_CONTROLLER_ID;

        byte[] recordBytes = null;
        try {
            recordBytes = UspMessageBuilder.buildRecordBytes(uspMsg, toId, fromId);
        } catch (IOException e) {
            log.error("[sendUspMsgDownstream][序列化 USP Record 失败, deviceId: {}]", device.getId(), e);
        }

        IotDeviceMessage message = IotDeviceMessage.builder()
                .deviceId(device.getId())
                .tenantId(device.getTenantId())
                .method(method)
                .params(methodParams)
                .payload(recordBytes)
                .build();
        deviceMessageService.sendDeviceMessage(message, device);
    }

}
