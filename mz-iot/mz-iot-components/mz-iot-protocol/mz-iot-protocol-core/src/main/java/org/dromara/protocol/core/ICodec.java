package org.dromara.protocol.core;

import java.util.Map;

public interface ICodec {
    void setScript(String script);

    void decode(String msg);

    Map<String, Object> decodeMap(String msg);

    void encode(String service, String device);

    void putScriptEnv(String key, Object value);
}
