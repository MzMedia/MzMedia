package org.dromara.network.codec;

import org.dromara.network.client.message.BaseMessage;
import org.dromara.network.session.BaseSession;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractProtocolCodec extends AbstractVerticle implements Handler<NetSocket> {

    private Map<String, AbstractProtocolHandler> handlerMap = new ConcurrentHashMap<>();
    private Map<String, BaseSession> logicAddressSessionMap = new ConcurrentHashMap<>();
    private Map<NetSocket, BaseSession> socketSessionMap = new ConcurrentHashMap<>();


    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        //vertx.eventBus().registerDefaultCodec(BaseMessage.class, new GenericMessageCodec<BaseMessage>() {});
        //vertx.eventBus().registerDefaultCodec(BaseSession.class, new GenericMessageCodec<BaseSession>() {});
        registerHandlers();
    }

    @Override
    public void handle(NetSocket socket) {
        log.info("收到新的连接：" + socket);
        socket.closeHandler(handler -> {
            log.info("连接已断开：" + socket);
        });

        socket.handler(data -> {
            try {
                BaseMessage message = new BaseMessage();
                message.setBuffer(data);
                message.setSocket(socket);
                if(!socketSessionMap.containsKey(socket)){
                    String logicAddress = getLogicAddress(message);
                    registerSession(logicAddress, socket);
                }
                decode(message);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("解码处理失败,throw:" + e);
            }
        });
    }
    private BaseSession registerSession(String logicAddress, NetSocket socket) {
        BaseSession session = new BaseSession();
        session.setLogicAddress(logicAddress);
        session.setSocket(socket);
        logicAddressSessionMap.put(logicAddress, session);
        socketSessionMap.put(socket, session);
        return session;
    }

    private void removeSession(NetSocket socket) {
        BaseSession session = socketSessionMap.remove(socket);
        if(session != null){
            logicAddressSessionMap.remove(session.getLogicAddress());
        }
    }

    public BaseSession getSessionByLogicAddress(String logicAddress) {
        return logicAddressSessionMap.get(logicAddress);
    }

    protected abstract List<AbstractProtocolHandler> getHandlers();

    private void registerHandlers() {
        List<AbstractProtocolHandler> handlers = getHandlers();
        handlers.forEach(handler -> {
            handlerMap.put(handler.getMessageType(), handler);
            vertx.deployVerticle(handler);
        });
    }

    public AbstractProtocolHandler getHandlerByMessageType(String messageType) {
        return handlerMap.get(messageType);
    }

    protected abstract void decode(BaseMessage message);


    protected abstract String getLogicAddress(BaseMessage message);


    protected void activeSocket(NetSocket socket) {

    }

    protected void afterCloseSocket(NetSocket socket) {

    }

}
