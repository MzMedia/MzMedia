package com.mz.protocol;

import com.mz.protocol.core.ICodec;
import com.mz.protocol.core.IMessageHandler;
import com.mz.protocol.core.IProductProtocol;
import com.mz.protocol.core.IScriptEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
@Setter
public class ProductMessageHandler implements IMessageHandler {

    private final IScriptEngine scriptEngine;

    private final ICodec codec;

    private final ProductProtocolManager deviceComponentManager;

    private final IProductProtocol protocol;

    @SneakyThrows
    public ProductMessageHandler(ProductProtocolManager deviceComponentManager,
                                IProductProtocol protocol,
                                IScriptEngine scriptEngine,
                                String script, ICodec codec
    ) {
        this.deviceComponentManager = deviceComponentManager;
        this.protocol = protocol;
        this.codec = codec;
        this.scriptEngine = scriptEngine;
    }

    @Override
    public void onReceive(Map<String, Object> params, String method, String msg) {

    }
}
