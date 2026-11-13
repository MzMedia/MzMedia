package cn.iocoder.yudao.module.media.controller.webhook;

import com.alibaba.fastjson2.JSONObject;
import cn.iocoder.yudao.module.media.framework.event.MediaStreamChangedEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 流媒体服务 (ZLMediaKit) Webhook 回调分发控制器
 * 对应放行白名单路径: /admin-api/video/webhook/**
 *
 * @author MzMedia Architect
 */
@Tag(name = "流媒体服务 - Webhook 回调接口")
@Slf4j
@RestController
@RequestMapping("/video/webhook")
public class ZlmWebhookController {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/on_stream_changed")
    @Operation(summary = "流注册/注销事件回调", description = "ZLMediaKit 产生或断开音视频流时触发")
    @ResponseBody
    public Map<String, Object> onStreamChanged(@RequestBody JSONObject json) {
        log.info("[ZlmWebhook] 收到流状态变更回调: {}", json.toJSONString());

        String app = json.getString("app");
        String stream = json.getString("stream");
        Boolean regist = json.getBoolean("regist");
        String mediaServerId = json.getString("mediaServerId");

        // 发布领域事件，解耦后续的录像触发、通道在线状态同步
        MediaStreamChangedEvent event = MediaStreamChangedEvent.builder()
                .app(app)
                .stream(stream)
                .regist(regist)
                .mediaServerId(mediaServerId)
                .timestamp(System.currentTimeMillis())
                .build();
        eventPublisher.publishEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "success");
        return response;
    }

    @PostMapping("/on_server_keepalive")
    @Operation(summary = "流媒体服务节点心跳保活回调")
    @ResponseBody
    public Map<String, Object> onServerKeepalive(@RequestBody JSONObject json) {
        log.debug("[ZlmWebhook] 收到媒体服务器心跳: {}", json.getString("mediaServerId"));
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "success");
        return response;
    }
}
