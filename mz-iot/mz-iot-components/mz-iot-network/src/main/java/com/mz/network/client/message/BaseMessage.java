package com.mz.network.client.message;

import lombok.Data;


@Data
public class BaseMessage extends Message {
    private String clientId;
    private String productID;
    private String deviceSN;
    private int code;
}
