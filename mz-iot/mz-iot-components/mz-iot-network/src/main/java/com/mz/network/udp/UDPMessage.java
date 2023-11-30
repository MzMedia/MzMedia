package com.mz.network.udp;

import com.mz.network.client.message.BaseMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UDPMessage extends BaseMessage {

    private String type;

}
