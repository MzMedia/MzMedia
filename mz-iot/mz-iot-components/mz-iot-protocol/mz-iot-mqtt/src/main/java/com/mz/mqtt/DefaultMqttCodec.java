package com.mz.mqtt;

import com.mz.protocol.core.ICodec;

import java.util.Map;

public class DefaultMqttCodec implements ICodec {

    @Override
    public void setScript(String script) {

    }

    @Override
    public void decode(String msg) {

    }

    @Override
    public Map<String, Object> decodeMap(String msg) {
        return null;
    }

    @Override
    public void encode(String service, String device) {

    }

    @Override
    public void putScriptEnv(String key, Object value) {

    }
}
