package cn.iocoder.yudao.module.media.service.redisMsg.control;

import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.conf.exception.ControllerException;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcRequest;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcResponse;
import cn.iocoder.yudao.module.media.gb28181.bean.SendRtpInfo;
import cn.iocoder.yudao.module.media.gb28181.session.SendSsrcFactory;
import cn.iocoder.yudao.module.media.media.bean.MediaInfo;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.service.IMediaServerService;
import cn.iocoder.yudao.module.media.service.ISendRtpServerService;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcController;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcMapping;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RpcController;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RedisRpcController("sendRtp")
public class RedisRpcSendRtpController extends RpcController {

    @Autowired
    private SendSsrcFactory sendSsrcFactory;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ISendRtpServerService sendRtpServerService;

    @Autowired
    private UserSetting userSetting;


    /**
     * 获取发流的信息
     */
    @RedisRpcMapping("getSendRtpItem")
    public RedisRpcResponse getSendRtpItem(RedisRpcRequest request) {
        String callId = request.getParam().toString();
        SendRtpInfo sendRtpItem = sendRtpServerService.queryByCallId(callId);
        if (sendRtpItem == null) {
            log.info("[redis-rpc] 获取发流的信息, 未找到redis中的发流信息， callId：{}", callId);
            RedisRpcResponse response = request.getResponse();
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            return response;
        }
        log.info("[redis-rpc] 获取发流的信息： {}/{}, 目标地址： {}：{}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort());
        // 查询本级是否有这个流
        MediaServer mediaServerItem = mediaServerService.getMediaServerByAppAndStream(sendRtpItem.getApp(), sendRtpItem.getStream());
        if (mediaServerItem == null) {
            RedisRpcResponse response = request.getResponse();
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            return response;
        }
        // 自平台内容
        int localPort = sendRtpServerService.getNextPort(mediaServerItem);
        if (localPort <= 0) {
            log.info("[redis-rpc] getSendRtpItem->服务器端口资源不足" );
            RedisRpcResponse response = request.getResponse();
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            return response;
        }
        // 写入redis， 超时时回复
        sendRtpItem.setStatus(1);
        sendRtpItem.setServerId(userSetting.getServerId());
        sendRtpItem.setLocalIp(mediaServerItem.getSdpIp());
        if (sendRtpItem.getSsrc() == null) {
            sendRtpItem.setSsrc(sendSsrcFactory.getSendSsrc(
                    "Play".equalsIgnoreCase(sendRtpItem.getSessionName()) ? "0" : "1"));
        }
        sendRtpServerService.update(sendRtpItem);
        RedisRpcResponse response = request.getResponse();
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(callId);
        return response;
    }

    /**
     * 开始发流
     */
    @RedisRpcMapping("startSendRtp")
    public RedisRpcResponse startSendRtp(RedisRpcRequest request) {
        String callId = request.getParam().toString();
        SendRtpInfo sendRtpItem = sendRtpServerService.queryByCallId(callId);
        RedisRpcResponse response = request.getResponse();
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        if (sendRtpItem == null) {
            log.info("[redis-rpc] 开始发流, 未找到redis中的发流信息， callId：{}", callId);
            MzResult mzResult = MzResult.fail(ErrorCode.ERROR100.getCode(), "未找到redis中的发流信息");
            response.setBody(mzResult);
            return response;
        }
        log.info("[redis-rpc] 开始发流： {}/{}, 目标地址： {}：{}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort());
        MediaServer mediaServer = mediaServerService.getOne(sendRtpItem.getMediaServerId());
        if (mediaServer == null) {
            log.info("[redis-rpc] startSendRtp->未找到MediaServer： {}", sendRtpItem.getMediaServerId() );
            clearSendRtpItem(sendRtpItem);
            MzResult mzResult = MzResult.fail(ErrorCode.ERROR100.getCode(), "未找到MediaServer");
            response.setBody(mzResult);
            return response;
        }
        MediaInfo mediaInfo = mediaServerService.getMediaInfo(mediaServer, sendRtpItem.getApp(), sendRtpItem.getStream());
        if (mediaInfo == null) {
            log.info("[redis-rpc] startSendRtp->流不在线： {}/{}", sendRtpItem.getApp(), sendRtpItem.getStream() );
            clearSendRtpItem(sendRtpItem);
            MzResult mzResult = MzResult.fail(ErrorCode.ERROR100.getCode(), "流不在线");
            response.setBody(mzResult);
            return response;
        }
        try {
            mediaServerService.startSendRtp(mediaServer, sendRtpItem);
        }catch (ControllerException exception) {
            log.info("[redis-rpc] 发流失败： {}/{}, 目标地址： {}：{}， {}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort(), exception.getMsg());
            clearSendRtpItem(sendRtpItem);
            MzResult mzResult = MzResult.fail(exception.getCode(), exception.getMsg());
            response.setBody(mzResult);
            return response;
        }
        log.info("[redis-rpc] 发流成功： {}/{}, 目标地址： {}：{}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort());
        MzResult mzResult = MzResult.success();
        response.setBody(mzResult);
        return response;
    }

    /**
     * 停止发流
     */
    @RedisRpcMapping("stopSendRtp")
    public RedisRpcResponse stopSendRtp(RedisRpcRequest request) {
        String callId = request.getParam().toString();
        SendRtpInfo sendRtpItem = sendRtpServerService.queryByCallId(callId);
        RedisRpcResponse response = request.getResponse();
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        if (sendRtpItem == null) {
            log.info("[redis-rpc] 停止推流, 未找到redis中的发流信息， key：{}", callId);
            MzResult mzResult = MzResult.fail(ErrorCode.ERROR100.getCode(), "未找到redis中的发流信息");
            response.setBody(mzResult);
            return response;
        }
        log.info("[redis-rpc] 停止推流： {}/{}, 目标地址： {}：{}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort() );
        MediaServer mediaServer = mediaServerService.getOne(sendRtpItem.getMediaServerId());
        if (mediaServer == null) {
            log.info("[redis-rpc] stopSendRtp->未找到MediaServer： {}", sendRtpItem.getMediaServerId() );
            clearSendRtpItem(sendRtpItem);
            MzResult mzResult = MzResult.fail(ErrorCode.ERROR100.getCode(), "未找到MediaServer");
            response.setBody(mzResult);
            return response;
        }
        try {
            mediaServerService.stopSendRtp(mediaServer, sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getSsrc());
        }catch (ControllerException exception) {
            log.info("[redis-rpc] 停止推流失败： {}/{}, 目标地址： {}：{}， code： {}, msg: {}", sendRtpItem.getApp(),
                    sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort(), exception.getCode(), exception.getMsg() );
            response.setBody(MzResult.fail(exception.getCode(), exception.getMsg()));
            return response;
        }
        clearSendRtpItem(sendRtpItem);
        log.info("[redis-rpc] 停止推流成功： {}/{}, 目标地址： {}：{}", sendRtpItem.getApp(), sendRtpItem.getStream(), sendRtpItem.getIp(), sendRtpItem.getPort() );
        response.setBody(MzResult.success());
        return response;
    }

    private void clearSendRtpItem(SendRtpInfo sendRtpItem) {
        if (sendRtpItem == null) {
            return;
        }
        sendRtpServerService.delete(sendRtpItem);

    }

}
