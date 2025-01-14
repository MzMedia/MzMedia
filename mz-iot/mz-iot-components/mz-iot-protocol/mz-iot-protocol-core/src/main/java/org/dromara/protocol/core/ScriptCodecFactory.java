package org.dromara.protocol.core;

import java.util.Map;

public class ScriptCodecFactory {
    public static ICodec getCovert(String type) {
        if (type == null) {
            type = "js";
        }
        switch (type) {
            case "python":
            case "lua":
                return new ICodec() {
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
                };
            case "js":
            default:
                return new JavaScriptCodec();
        }
    }
}
