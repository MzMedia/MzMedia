package cn.iocoder.yudao.module.media.service.redisMsg.control;

import com.alibaba.fastjson2.JSONObject;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.conf.redis.RedisRpcConfig;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcMessage;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcRequest;
import cn.iocoder.yudao.module.media.conf.redis.bean.RedisRpcResponse;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.bean.PlatformChannel;
import cn.iocoder.yudao.module.media.gb28181.controller.bean.ChannelListForRpcParam;
import cn.iocoder.yudao.module.media.gb28181.event.EventPublisher;
import cn.iocoder.yudao.module.media.gb28181.service.IPlatformChannelService;
import cn.iocoder.yudao.module.media.gb28181.service.IPlatformService;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcController;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RedisRpcMapping;
import cn.iocoder.yudao.module.media.service.redisMsg.dto.RpcController;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RedisRpcController("platform")
public class RedisRpcPlatformController extends RpcController {

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IPlatformService platformService;

    @Autowired
    private IPlatformChannelService platformChannelService;

    @Autowired
    private EventPublisher eventPublisher;


    private void sendResponse(RedisRpcResponse response){
        log.info("[redis-rpc] >> {}", response);
        response.setToId(userSetting.getServerId());
        RedisRpcMessage message = new RedisRpcMessage();
        message.setResponse(response);
        redisTemplate.convertAndSend(RedisRpcConfig.REDIS_REQUEST_CHANNEL_KEY, message);
    }

    /**
     * 更新
     */
    @RedisRpcMapping("update")
    public RedisRpcResponse update(RedisRpcRequest request) {
        Platform platform = JSONObject.parseObject(request.getParam().toString(), Platform.class);
        RedisRpcResponse response = request.getResponse();
        boolean update = platformService.update(platform);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(Boolean.toString(update));
        return response;
    }


    /**
     * 删除
     */
    @RedisRpcMapping("delete")
    public RedisRpcResponse delete(RedisRpcRequest request) {
        Integer platformId = Integer.parseInt(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        try {
            boolean result = platformService.delete(platformId);
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody(Boolean.toString(result));
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("false");
        }
        return response;
    }
    /**
     * 主动推送通道
     */
    @RedisRpcMapping("pushChannel")
    public RedisRpcResponse pushChannel(RedisRpcRequest request) {
        Integer platformId = Integer.parseInt(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        try {
            platformChannelService.pushChannel(platformId);
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("true");
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("false");
        }
        return response;
    }

    /**
     * 共享通道
     */
    @RedisRpcMapping("addChannelList")
    public RedisRpcResponse addChannelList(RedisRpcRequest request) {
        ChannelListForRpcParam param = JSONObject.parseObject(request.getParam().toString(), ChannelListForRpcParam.class);
        RedisRpcResponse response = request.getResponse();
        try {
            int result = platformChannelService.addChannels(param.getPlatformId(), param.getChannelIds());
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody(result + "");
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("0");
        }
        return response;
    }

    /**
     * 移除全部共享通道
     */
    @RedisRpcMapping("removeAllChannel")
    public RedisRpcResponse removeAllChannel(RedisRpcRequest request) {
        Integer platformId = Integer.parseInt(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        try {
            int result = platformChannelService.removeAllChannel(platformId);
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody(result + "");
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("0");
        }
        return response;
    }

    /**
     * 取消共享通道
     */
    @RedisRpcMapping("removeChannelList")
    public RedisRpcResponse removeChannelList(RedisRpcRequest request) {
        ChannelListForRpcParam param = JSONObject.parseObject(request.getParam().toString(), ChannelListForRpcParam.class);
        RedisRpcResponse response = request.getResponse();
        try {
            int result = platformChannelService.removeChannels(param.getPlatformId(), param.getChannelIds());
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody(result + "");
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("0");
        }
        return response;
    }

    /**
     * 自定义通道
     */
    @RedisRpcMapping("updateCustomChannel")
    public RedisRpcResponse updateCustomChannel(RedisRpcRequest request) {
        PlatformChannel param = JSONObject.parseObject(request.getParam().toString(), PlatformChannel.class);
        RedisRpcResponse response = request.getResponse();
        try {
            platformChannelService.updateCustomChannel(param);
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("true");
        }catch (Exception e) {
            response.setStatusCode(ErrorCode.SUCCESS.getCode());
            response.setBody("false");
        }
        return response;
    }

    /**
     * 目录更新推送
     */
    @RedisRpcMapping("catalogEventPublish")
    public RedisRpcResponse catalogEventPublish(RedisRpcRequest request) {
        JSONObject jsonObject = JSONObject.parseObject(request.getParam().toString());
        Platform platform = jsonObject.getObject("platform", Platform.class);

        List<CommonGBChannel> channels = jsonObject.getJSONArray("channels").toJavaList(CommonGBChannel.class);
        String type = jsonObject.getString("type");
        eventPublisher.catalogEventPublish(platform, channels, type);
        RedisRpcResponse response = request.getResponse();
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

}
