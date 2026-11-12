package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.core.messagebus.core.IotMessageBus;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.CwmpInform;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser.CwmpSoapParser;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.session.CwmpSession;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.session.IotCwmpSessionManager;
import cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper;
import cn.iocoder.yudao.module.iot.core.topic.state.IotDeviceStateUpdateReqDTO;
import cn.iocoder.yudao.module.iot.core.util.IotDeviceMessageUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * TR-069 CPE 上行 HTTP SOAP 消息处理器
 *
 * 负责处理 CPE 的 Inform、空 POST 轮询、RPC 响应以及会话周期调度
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class IotCwmpUpstreamHandler implements Handler<RoutingContext> {

    private static final String COOKIE_CWMP_SESSION = "cwmp_session_id";

    private final IotCwmpProtocol protocol;
    private final IotCwmpSessionManager sessionManager;
    private final IotMessageBus messageBus;

    @Override
    public void handle(RoutingContext ctx) {
        String body = ctx.body().asString();
        String remoteAddr = ctx.request().remoteAddress() != null ? ctx.request().remoteAddress().host() : "unknown";

        log.debug("[handle][收到来自 {} 的 CWMP 请求, 长度: {}]", remoteAddr, StrUtil.length(body));

        // 1. 处理空 POST (CPE 表示已就绪，向 ACS 请求下发命令)
        if (StrUtil.isBlank(body)) {
            handleEmptyPost(ctx);
            return;
        }

        // 2. 识别 SOAP 消息方法
        String messageType = CwmpSoapParser.getMessageType(body);
        log.info("[handle][收到来自 {} 的 CWMP 消息类型: {}]", remoteAddr, messageType);

        if ("Inform".equalsIgnoreCase(messageType)) {
            handleInform(ctx, body, remoteAddr);
        } else if ("TransferComplete".equalsIgnoreCase(messageType)) {
            handleTransferComplete(ctx, body);
        } else {
            // 处理 RPC 响应（GetParameterValuesResponse, SetParameterValuesResponse, Fault 等）
            handleRpcResponse(ctx, body);
        }
    }

    /**
     * 处理 CPE 上报的 Inform 报文
     */
    private void handleInform(RoutingContext ctx, String xml, String remoteAddr) {
        CwmpInform inform = CwmpSoapParser.parseInform(xml);
        if (inform == null || StrUtil.isBlank(inform.getSerialNumber())) {
            log.warn("[handleInform][解析 Inform 报文失败或缺少 SerialNumber]");
            ctx.response().setStatusCode(400).end("Invalid Inform");
            return;
        }

        String sn = inform.getSerialNumber();
        CwmpSession session = sessionManager.getOrCreateSession(sn);
        sessionManager.updateConnectionRequestUrl(sn, inform.getConnectionRequestUrl());

        log.info("[handleInform][设备上线/心跳, SN: {}, 厂商: {}, 型号: {}, 固件: {}, 包含事件: {}]",
                sn, inform.getManufacturer(), inform.getProductClass(), inform.getSoftwareVersion(), inform.getEvents());

        // 1. 发送设备上线状态消息至总线
        IotDeviceMessage stateMsg = IotDeviceMessage.builder()
                .id(IotDeviceMessageUtils.generateMessageId())
                .reportTime(LocalDateTime.now())
                .serverId(protocol.getServerId())
                .method(IotDeviceMessageMethodEnum.STATE_UPDATE.getMethod())
                .params(new IotDeviceStateUpdateReqDTO()
                        .setState(IotDeviceStateEnum.ONLINE.getValue())
                        .setOnlineTime(LocalDateTime.now()))
                .build();
        messageBus.publish(IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC, stateMsg);

        // 2. 将核心参数映射到物模型属性并发送至总线
        Map<String, Object> props = Tr181ThingModelMapper.mapCwmpParamsToThingModel(inform.getParameterList());
        if (!props.isEmpty()) {
            IotDeviceMessage propMsg = IotDeviceMessage.builder()
                    .id(IotDeviceMessageUtils.generateMessageId())
                    .reportTime(LocalDateTime.now())
                    .serverId(protocol.getServerId())
                    .method(IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod())
                    .params(props)
                    .build();
            messageBus.publish(IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC, propMsg);
        }

        // 3. 设置会话 Cookie 并回复 InformResponse
        ctx.response().addCookie(Cookie.cookie(COOKIE_CWMP_SESSION, session.getSessionId()).setPath("/"));
        String respXml = CwmpSoapBuilder.buildInformResponse(inform.getHeaderId());

        ctx.response()
                .putHeader("Content-Type", "text/xml; charset=utf-8")
                .putHeader("Connection", "keep-alive")
                .setStatusCode(200)
                .end(respXml);
    }

    /**
     * 处理 CPE 空 POST（出队下行指令）
     */
    private void handleEmptyPost(RoutingContext ctx) {
        String sn = getSnFromCookie(ctx);
        if (StrUtil.isBlank(sn)) {
            log.warn("[handleEmptyPost][缺少会话 Cookie，拒绝请求]");
            ctx.response().setStatusCode(204).putHeader("Connection", "close").end();
            return;
        }

        // 从队列取下一个待下发命令
        CwmpSession.CwmpPendingCommand nextCmd = sessionManager.popNextCommand(sn);
        if (nextCmd != null) {
            log.info("[handleEmptyPost][向设备 {} 发送队列指令: {}]", sn, nextCmd.getCommandId());
            ctx.response()
                    .putHeader("Content-Type", "text/xml; charset=utf-8")
                    .putHeader("Connection", "keep-alive")
                    .setStatusCode(200)
                    .end(nextCmd.getSoapXml());
        } else {
            // 无更多下行指令，结束会话
            log.debug("[handleEmptyPost][设备 {} 无待执行指令，结束会话 (204)]", sn);
            ctx.response()
                    .setStatusCode(204)
                    .putHeader("Connection", "close")
                    .end();
        }
    }

    /**
     * 处理 RPC 响应并尝试继续发送下一条命令
     */
    private void handleRpcResponse(RoutingContext ctx, String xml) {
        String sn = getSnFromCookie(ctx);
        if (StrUtil.isNotBlank(sn)) {
            sessionManager.completeWaitingCommand(sn, xml);

            // 检查是否还有后续命令
            CwmpSession.CwmpPendingCommand nextCmd = sessionManager.popNextCommand(sn);
            if (nextCmd != null) {
                log.info("[handleRpcResponse][继续向设备 {} 发送后续指令: {}]", sn, nextCmd.getCommandId());
                ctx.response()
                        .putHeader("Content-Type", "text/xml; charset=utf-8")
                        .putHeader("Connection", "keep-alive")
                        .setStatusCode(200)
                        .end(nextCmd.getSoapXml());
                return;
            }
        }

        // 无更多命令，结束会话
        ctx.response()
                .setStatusCode(204)
                .putHeader("Connection", "close")
                .end();
    }

    /**
     * 处理固件升级完成 (TransferComplete)
     */
    private void handleTransferComplete(RoutingContext ctx, String xml) {
        String sn = getSnFromCookie(ctx);
        log.info("[handleTransferComplete][设备 {} 文件传输/固件升级完成]", sn);

        String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">\n" +
                "  <soap:Header/>\n" +
                "  <soap:Body>\n" +
                "    <cwmp:TransferCompleteResponse/>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";

        ctx.response()
                .putHeader("Content-Type", "text/xml; charset=utf-8")
                .putHeader("Connection", "keep-alive")
                .setStatusCode(200)
                .end(resp);
    }

    private String getSnFromCookie(RoutingContext ctx) {
        Cookie cookie = ctx.request().getCookie(COOKIE_CWMP_SESSION);
        if (cookie == null || StrUtil.isBlank(cookie.getValue())) {
            return null;
        }
        CwmpSession session = sessionManager.getSessionByCookie(cookie.getValue());
        return session != null ? session.getSerialNumber() : null;
    }

}
