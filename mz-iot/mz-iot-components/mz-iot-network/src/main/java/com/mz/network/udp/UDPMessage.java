package com.mz.network.udp;

import com.mz.network.client.message.ClientMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UDPMessage extends ClientMessage {

    private String type;

}
