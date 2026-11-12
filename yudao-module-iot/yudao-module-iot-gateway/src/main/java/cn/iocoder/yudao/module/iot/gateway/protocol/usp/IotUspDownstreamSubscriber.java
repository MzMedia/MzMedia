package cn.iocoder.yudao.module.iot.gateway.protocol.usp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceRespDTO;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.messagebus.core.IotMessageBus;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.UspMsg;
import cn.iocoder.yudao.module.iot.gateway.protocol.AbstractIotProtocolDownstreamSubscriber;
import cn.iocoder.yudao.module.iot.gateway.protocol.IotProtocol;
import cn.iocoder.yudao.module.iot.gateway.service.device.IotDeviceService;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * TR-369 USP 下行消息订阅者
 *
 * @author 芋道源码
 */
@Slf4j
public class IotUspDownstreamSubscriber extends AbstractIotProtocolDownstreamSubscriber {

    private final IotDeviceService deviceService;
    private final BiConsumer<String, byte[]> sender;
    private final String controllerId;

    public IotUspDownstreamSubscriber(IotProtocol protocol,
                                      IotMessageBus messageBus,
                                      IotDeviceService deviceService,
                                      BiConsumer<String, byte[]> sender,
                                      String controllerId) {
        super(protocol, messageBus);
        this.deviceService = deviceService;
        this.sender = sender;
        this.controllerId = StrUtil.blankToDefault(controllerId, UspMessageBuilder.DEFAULT_CONTROLLER_ID);
    }

    @Override
    protected void handleMessage(IotDeviceMessage message) {
        if (message == null || message.getDeviceId() == null) {
            return;
        }

        IotDeviceRespDTO device = deviceService.getDeviceFromCache(message.getDeviceId());
        if (device == null) {
            log.warn("[onMessage][USP 找不到设备: {}]", message.getDeviceId());
            return;
        }

        String endpointId = StrUtil.isNotBlank(device.getDeviceName()) ? device.getDeviceName() : String.valueOf(device.getId());
        String method = message.getMethod();

        log.info("[onMessage][USP 下发指令, Endpoint: {}, 方法: {}]", endpointId, method);

        UspMsg uspMsg = null;

        // 0. 原始二进制直通下发 (RAW_PUSH)
        if (IotDeviceMessageMethodEnum.RAW_PUSH.getMethod().equals(method) && message.getPayload() != null) {
            sender.accept(endpointId, message.getPayload());
            return;
        }

        // 1. 属性设置 (thing.property.set 或 usp.set)
        if (IotDeviceMessageMethodEnum.PROPERTY_SET.getMethod().equals(method) || "usp.set".equalsIgnoreCase(method)) {
            if (message.getParams() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) message.getParams();
                Map<String, String> strMap = new HashMap<>();
                map.forEach((k, v) -> strMap.put(k, String.valueOf(v)));
                uspMsg = UspMessageBuilder.buildSetMsg(strMap);
            }
        }
        // 2. 参数读取 (usp.get)
        else if ("usp.get".equalsIgnoreCase(method)) {
            if (message.getParams() instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> paths = (List<String>) message.getParams();
                uspMsg = UspMessageBuilder.buildGetMsg(paths);
            } else if (message.getParams() instanceof String) {
                uspMsg = UspMessageBuilder.buildGetMsg((String) message.getParams());
            }
        }
        // 3. 服务调用 (thing.service.invoke 或 usp.operate)
        else if (IotDeviceMessageMethodEnum.SERVICE_INVOKE.getMethod().equals(method) || "usp.operate".equalsIgnoreCase(method)) {
            if (message.getParams() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) message.getParams();
                String cmd = String.valueOf(map.getOrDefault("command", "Device." + StrUtil.upperFirst(message.getRequestId()) + "()"));
                String cmdKey = String.valueOf(map.getOrDefault("commandKey", "KEY_" + message.getId()));
                @SuppressWarnings("unchecked")
                Map<String, String> args = (Map<String, String>) map.get("inputArgs");
                uspMsg = UspMessageBuilder.buildOperateMsg(cmd, cmdKey, args != null ? args : Collections.emptyMap());
            } else {
                String command = "Device." + StrUtil.upperFirst(message.getRequestId()) + "()";
                uspMsg = UspMessageBuilder.buildOperateMsg(command, "KEY_" + message.getId(), Collections.emptyMap());
            }
        }

        if (uspMsg != null) {
            try {
                byte[] recordBytes = UspMessageBuilder.buildRecordBytes(uspMsg, endpointId, controllerId);
                sender.accept(endpointId, recordBytes);
            } catch (Exception e) {
                log.error("[onMessage][构建 USP Record 异常, endpoint: {}]", endpointId, e);
            }
        }
    }

}
