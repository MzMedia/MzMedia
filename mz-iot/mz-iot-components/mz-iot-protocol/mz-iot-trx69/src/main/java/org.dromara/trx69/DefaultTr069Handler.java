package org.dromara.trx69;

import org.dromara.protocol.DefaultProductMessageHandler;
import org.dromara.protocol.ProductProtocolManager;
import org.dromara.protocol.core.ICodec;
import org.dromara.protocol.core.IProductProtocol;

import java.util.Map;

public class DefaultTr069Handler extends DefaultProductMessageHandler {
    public DefaultTr069Handler(ProductProtocolManager deviceComponentManager, IProductProtocol protocol, String scriptLang, String script, String codecType, ICodec codec) {
        super(deviceComponentManager, protocol, scriptLang, script, codecType, codec);
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
}