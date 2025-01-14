package org.dromara.protocol.core;

import com.fasterxml.jackson.core.type.TypeReference;

public class ScriptEngineFactory {
    public static IScriptEngine getScriptEngine(String type) {
        if (type == null) {
            type = "js";
        }
        switch (type) {
            case "python":
            case "lua":
                return new IScriptEngine() {

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
                };
            case "js":
            default:
                return new JavaScriptEngine();
        }
    }
}
