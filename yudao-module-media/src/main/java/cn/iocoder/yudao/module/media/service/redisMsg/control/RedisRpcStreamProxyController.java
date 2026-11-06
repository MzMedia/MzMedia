package cn.iocoder.yudao.module.media.service.redisMsg.control;

import com.alibaba.fastjson2.JSONObject;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.conf.redis.RedisRpcConfig;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcMessage;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcRequest;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcResponse;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcController;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcMapping;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RpcController;
import cn.iocoder.yudao.module.media.streamProxy.bean.StreamProxy;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyPlayService;
import cn.iocoder.yudao.module.media.streamProxy.service.IStreamProxyService;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RedisRpcController("streamProxy")
public class RedisRpcStreamProxyController extends RpcController {

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IStreamProxyPlayService streamProxyPlayService;

    @Autowired
    private IStreamProxyService streamProxyService;


    private void sendResponse(RedisRpcResponse response){
        log.info("[redis-rpc] >> {}", response);
        response.setToId(userSetting.getServerId());
        RedisRpcMessage message = new RedisRpcMessage();
        message.setResponse(response);
        redisTemplate.convertAndSend(RedisRpcConfig.REDIS_REQUEST_CHANNEL_KEY, message);
    }

    /**
     * 播放
     */
    @RedisRpcMapping("play")
    public RedisRpcResponse play(RedisRpcRequest request) {
        int id = Integer.parseInt(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        if (id <= 0) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        StreamProxy streamProxy = streamProxyService.getStreamProxy(id);
        if (streamProxy == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        streamProxyPlayService.startProxy(streamProxy, (code, msg, streamInfo) -> {
            response.setStatusCode(code);
            response.setBody(JSONObject.toJSONString(streamInfo));
            sendResponse(response);
        });

        return null;
    }

    /**
     * 停止
     */
    @RedisRpcMapping("stop")
    public RedisRpcResponse stop(RedisRpcRequest request) {
        int id = Integer.parseInt(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        if (id <= 0) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        StreamProxy streamProxy = streamProxyService.getStreamProxy(id);
        if (streamProxy == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        streamProxyPlayService.stopProxy(streamProxy);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

}
