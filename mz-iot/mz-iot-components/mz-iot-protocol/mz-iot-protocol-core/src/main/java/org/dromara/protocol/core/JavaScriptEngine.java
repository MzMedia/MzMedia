package org.dromara.protocol.core;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaScriptEngine implements IScriptEngine {

    @Override
    public void setScript(String key) {

    }

    @Override
    public void putScriptEnv(String key, Object val) {

    }

    @Override
    public void invokeMethod(String methodName, Object... args) {

    }

    @Override
    public <T> T invokeMethod(TypeReference<T> type, String methodName, Object... args) {
        return null;
    }
}
