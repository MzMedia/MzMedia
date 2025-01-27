package org.dromara.protocol.core;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
public class JavaScriptCodec implements ICodec {
    private IScriptEngine scriptEngine = ScriptEngineFactory.getScriptEngine("js");

    @SneakyThrows
    public void setScript(String script) {
        scriptEngine.setScript(script);
    }

    @SneakyThrows
    public void decode(String msg) {
        scriptEngine.invokeMethod(new TypeReference<>() {
        }, "decode", msg);
    }

    @SneakyThrows
    public Map<String, Object> decodeMap(String msg) {
        return null;
    }

    @SneakyThrows
    @Override
    public void encode(String service, String device) {
        scriptEngine.invokeMethod(new TypeReference<>() {
        }, "encode", service, device);
    }

    @Override
    public void putScriptEnv(String key, Object value) {
        scriptEngine.putScriptEnv(key, value);
    }
}
