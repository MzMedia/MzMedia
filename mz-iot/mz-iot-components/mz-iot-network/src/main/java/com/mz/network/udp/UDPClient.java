package com.mz.network.udp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mz.network.client.Client;
import com.mz.network.core.IotCommand;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UDPClient implements Client {

    private String clientId;

    private SocketAddress clientAddress;

    private DatagramSocket socket;

    private long lastPingTime = System.currentTimeMillis();

    public UDPClient(String clientId, SocketAddress clientAddress, DatagramSocket socket) {
        this.clientId = clientId;
        this.clientAddress = clientAddress;
        this.socket = socket;
    }

    @Override
    public String getId() {
        return clientAddress.host() + ":" + clientAddress.port();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    @Override
    public void send(String topic, IotCommand command) {
        JSONObject json = (JSONObject) JSON.toJSON(command);
        json.put("type", topic);
        socket.send(json.toString(), clientAddress.port(), clientAddress.host(), result -> {
            if (result.succeeded()) {
                log.debug("向UDP客户端:[{}]发送数据完成:{}", clientAddress, command);
            } else {
                log.error("向UDP客户端:[{}]发送数据失败:{}", clientAddress, command, result.cause());
            }
        });
    }

    @Override
    public void close() {

    }

    @Override
    public String toString() {
        return "UDP Client[" + getClientId() + "]:[" + clientAddress + "]";
    }

    @Override
    public void ping() {
        log.debug("udp client[{}] ping", clientAddress);
        lastPingTime = System.currentTimeMillis();
    }

}
