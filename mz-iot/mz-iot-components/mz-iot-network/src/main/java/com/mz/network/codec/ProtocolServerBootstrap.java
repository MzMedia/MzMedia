package com.mz.network.codec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.impl.SocketAddressImpl;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ProtocolServerBootstrap extends AbstractVerticle {
    private final Class<?> starter;
    private static final Map<String, ProtocolConfig> protocols = new ConcurrentHashMap<>();
    private static final Map<String, AbstractProtocolCodec> codecMap = new ConcurrentHashMap<>();

    public ProtocolServerBootstrap(Class<?> starter) {
        this.starter = starter;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        loadProtocolCodec();
    }

    private void loadProtocolCodec() {
        try {
            CodecScan codecScan = starter.getAnnotation(CodecScan.class);
            if(codecScan == null){
                protocols.clear();
                return;
            }
            String[] packages = codecScan.value();
            for (String p : packages) {
                Reflections reflection = new Reflections(p);
                Set<Class<?>> classes = reflection.getTypesAnnotatedWith(Protocol.class);
                for (Class<?> aClass : classes) {
                    Protocol annotation = aClass.getAnnotation(Protocol.class);
                    codecMap.put(annotation.value(), (AbstractProtocolCodec) aClass.newInstance());
                    log.info("加载编解码器：" + aClass.getName());
                }
            }
        } catch (Exception e) {
            log.error("加载编解码器失败:" + e);
        }
    }

    @Override
    public void start() {
        protocols.forEach((name, protocol) -> {
            AbstractProtocolCodec codec = codecMap.get(name);
            vertx.deployVerticle(codec);
            //监听对应端口，并将编解码函数注册进去
            SocketAddress address = new SocketAddressImpl(protocol.getPort(), protocol.getHost());
            NetServer server = vertx.createNetServer();
            server.connectHandler(codec).listen(address);
        });
    }

    public AbstractProtocolCodec getProtocolCodec(String protocolName) {
        return codecMap.get(protocolName);
    }
}
