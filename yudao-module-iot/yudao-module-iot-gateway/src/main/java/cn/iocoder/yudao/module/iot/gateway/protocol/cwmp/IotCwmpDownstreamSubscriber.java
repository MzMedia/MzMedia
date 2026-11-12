package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceRespDTO;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.messagebus.core.IotMessageBus;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session.CwmpSession;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session.IotCwmpSessionManager;
import cn.iocoder.yudao.module.iot.gateway.protocol.AbstractIotProtocolDownstreamSubscriber;
import cn.iocoder.yudao.module.iot.gateway.service.device.IotDeviceService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * TR-069 下行指令消息订阅者
 *
 * 接收来自 iot-biz 的下行设备指令，转化为 CWMP SOAP 报文并投递入队
 *
 * @author 芋道源码
 */
@Slf4j
public class IotCwmpDownstreamSubscriber extends AbstractIotProtocolDownstreamSubscriber {

    private final IotCwmpSessionManager sessionManager;
    private final IotCwmpConnectionRequestClient connectionRequestClient;
    private final IotDeviceService deviceService;

    public IotCwmpDownstreamSubscriber(IotCwmpProtocol protocol,
                                       IotMessageBus messageBus,
                                       IotCwmpSessionManager sessionManager,
                                       IotCwmpConnectionRequestClient connectionRequestClient,
                                       IotDeviceService deviceService) {
        super(protocol, messageBus);
        this.sessionManager = sessionManager;
        this.connectionRequestClient = connectionRequestClient;
        this.deviceService = deviceService;
    }

    @Override
    protected void onMessage(IotDeviceMessage message) {
        if (message == null || message.getDeviceId() == null) {
            return;
        }

        IotDeviceRespDTO device = deviceService.getDeviceFromCache(message.getDeviceId());
        if (device == null) {
            log.warn("[onMessage][找不到设备信息, deviceId: {}]", message.getDeviceId());
            return;
        }

        // 设备序列号或设备名称
        String sn = StrUtil.isNotBlank(device.getSerialNumber()) ? device.getSerialNumber() : device.getDeviceName();
        String method = message.getMethod();

        log.info("[onMessage][收到下发指令, 设备: {}, 方法: {}]", sn, method);

        String soapXml = null;

        // 1. 属性设置 (thing.property.set)
        if (IotDeviceMessageMethodEnum.PROPERTY_SET.getMethod().equals(method)) {
            if (message.getParams() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) message.getParams();
                Map<String, String> strMap = new HashMap<>();
                map.forEach((k, v) -> strMap.put(k, String.valueOf(v)));
                soapXml = CwmpSoapBuilder.buildSetParameterValues(strMap);
            }
        }
        // 2. 服务调用 (thing.service.invoke)
        else if (IotDeviceMessageMethodEnum.SERVICE_INVOKE.getMethod().equals(method)) {
            String serviceId = message.getRequestId();
            if ("reboot".equalsIgnoreCase(serviceId)) {
                soapXml = CwmpSoapBuilder.buildReboot("RPC_REBOOT");
            } else if ("factoryReset".equalsIgnoreCase(serviceId)) {
                soapXml = CwmpSoapBuilder.buildFactoryReset();
            }
        }
        // 3. OTA 固件升级 (thing.ota.upgrade)
        else if (IotDeviceMessageMethodEnum.OTA_UPGRADE.getMethod().equals(method)) {
            if (message.getParams() != null) {
                String json = JsonUtils.toJsonString(message.getParams());
                Map<String, Object> paramMap = JsonUtils.parseObject(json, Map.class);
                if (paramMap != null) {
                    String url = (String) paramMap.get("url");
                    soapXml = CwmpSoapBuilder.buildDownload("1 Firmware Upgrade Image", url, "", "", 0L, "");
                }
            }
        }
        // 4. 透传下发 (thing.raw.push)
        else if (IotDeviceMessageMethodEnum.RAW_PUSH.getMethod().equals(method)) {
            if (message.getPayload() != null) {
                soapXml = StrUtil.utf8Str(message.getPayload());
            }
        }

        if (StrUtil.isNotBlank(soapXml)) {
            // 投递命令到 CPE 队列
            sessionManager.enqueueCommand(sn, soapXml, 30000L);

            // 检查 CPE 是否有活跃会话，如果没有且已知 ConnectionRequestURL，则反向唤醒
            CwmpSession session = sessionManager.getOrCreateSession(sn);
            if (StrUtil.isNotBlank(session.getConnectionRequestUrl())) {
                connectionRequestClient.trigger(session.getConnectionRequestUrl(), null, null);
            }
        }
    }

}
