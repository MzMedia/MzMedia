package cn.iocoder.yudao.module.iot.service.rule.scene.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.event.IotMediaLinkageEvent;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.dal.dataobject.rule.IotSceneRuleDO;
import cn.iocoder.yudao.module.iot.enums.rule.IotSceneRuleActionTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * IoT 视频监控联动的 {@link IotSceneRuleAction} 实现类
 *
 * 当场景规则触发时，解析联动配置并发布 {@link IotMediaLinkageEvent}，
 * 驱动国标视频监控模块执行球机预置位转向或录像抓拍。
 */
@Component
@Slf4j
public class IotMediaSceneRuleAction implements IotSceneRuleAction {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(@Nullable IotDeviceMessage message,
                        IotSceneRuleDO rule,
                        IotSceneRuleDO.Action actionConfig) {
        String params = actionConfig.getParams();
        if (StrUtil.isBlank(params)) {
            log.warn("[execute][规则({}) 配置了视频联动动作，但 params 为空]", rule.getName());
            return;
        }

        try {
            JSONObject json = JSONUtil.parseObj(params);
            String mediaDeviceId = json.getStr("deviceId");
            String channelId = json.getStr("channelId");
            String action = json.getStr("action", "ptz_preset");
            Integer presetIndex = json.getInt("presetIndex", 1);

            IotMediaLinkageEvent event = IotMediaLinkageEvent.builder()
                    .tenantId(rule.getTenantId())
                    .deviceId(mediaDeviceId)
                    .channelId(channelId)
                    .action(action)
                    .presetIndex(presetIndex)
                    .source("场景规则联动: " + rule.getName())
                    .build();

            log.info("[execute][规则({}) 触发视频监控联动: deviceId={}, channelId={}, action={}, presetIndex={}]",
                    rule.getName(), mediaDeviceId, channelId, action, presetIndex);

            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("[execute][规则({}) 执行视频联动解析异常, params={}]", rule.getName(), params, e);
        }
    }

    @Override
    public IotSceneRuleActionTypeEnum getType() {
        return IotSceneRuleActionTypeEnum.MEDIA_LINKAGE;
    }
}
