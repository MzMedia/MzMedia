package com.mz.protocol;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.protocol.core.*;
import com.mz.protocol.domain.Protocol;
import com.mz.protocol.domain.ReceiveResult;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Getter
@Setter
public class DefaultProductMessageHandler implements IMessageHandler {

    private final IScriptEngine scriptEngine;

    private final String codecType;

    private final ICodec codec;

    private final ProductProtocolManager deviceComponentManager;

    private final IProductProtocol protocol;

    private final ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>());

    @SneakyThrows
    public DefaultProductMessageHandler(ProductProtocolManager deviceComponentManager,
                                        IProductProtocol protocol, String scriptLang,
                                        String script, String codecType, ICodec codec
    ) {
        this.deviceComponentManager = deviceComponentManager;
        this.protocol = protocol;
        this.codec = codec;
        this.codecType = codecType;
        this.scriptEngine = ScriptEngineFactory.getScriptEngine(scriptLang);
        this.scriptEngine.putScriptEnv("protocol", protocol);
        this.scriptEngine.setScript(script);
    }

    @Override
    public void onReceive(Map<String, Object> params, String type, String payload) {
        onReceive(params, type, payload, (ret) -> {
        });
    }

    public void onReceive(Map<String, Object> params, String type, String payload, Consumer<ReceiveResult> onResult) {
        executorService.submit(() -> {
            try {
                Map<String, Object> ret = null;
                //是否存在脚本，如果存在则执行脚本
                if (Protocol.CODEC_TYPE_CUSTOM_SCRIPT.equals(codecType)) {
                    ret = scriptEngine.invokeMethod(new TypeReference<>() {
                    }, "onReceive", params, type, payload);
                } else if (Protocol.CODEC_TYPE_CUSTOM_JAR.equals(codecType)) {
                    //如果是jar包，则执行jar包中的编解码器
                    ret = codec.decodeMap(payload);
                } else if (Protocol.CODEC_TYPE_JSON.equals(codecType) || Protocol.CODEC_TYPE_XML.equals(codecType)) {
                    ObjectMapper mapper = new ObjectMapper();
                    ret = mapper.readValue(payload, Map.class);
                } else {
                    log.info("not support codec type:{}", codecType);
                }

                Object method = ret.get("method");
                if (method == null) {
                    onResult.accept(null);
                    return;
                }
                //取脚本执行后返回的数据
                Object objData = ret.get("params");
                if (!(objData instanceof Map)) {
                    onResult.accept(null);
                    return;
                }
                switch (method.toString()) {
                    case "baseinfo": //基础信息上报
                        onReceiveInfo(params.get("sn").toString(), (Map<String, Object>) objData);
                        return;
                    case "property": //设备属性上报
                        onReceiveInfo(params.get("sn").toString(), (Map<String, Object>) objData);
                        return;
                    case "event": //设备事件上报
                        onReceiveEvent(params.get("sn").toString(), params.get("eventId").toString(),(Map<String, Object>) objData);
                        return;
                    case "reply": //设备响应消息上报
                        return;
                }

            } catch (Throwable e) {
                log.error("receive component message error", e);
            }
            onResult.accept(null);
        });
    }

    @Override
    public void onAuth(String type, String id, Map<String, Object> params) {

    }

    @Override
    public void onDeviceStateChange(String id, String state, Map<String, Object> params) {

    }

    @Override
    public void onReceiveEvent(String id, String event, Map<String, Object> params) {

    }

    @Override
    public void onReceiveProperty(String id, String property, Map<String, Object> params) {

    }

    @Override
    public void onReceiveInfo(String id, Map<String, Object> params) {

    }

    @Override
    public void send(String id, String method, Map<String, Object> params) {

    }
}
