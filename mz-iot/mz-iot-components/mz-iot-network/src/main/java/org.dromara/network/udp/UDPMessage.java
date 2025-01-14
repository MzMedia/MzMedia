package org.dromara.network.udp;

import org.dromara.network.client.message.BaseMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UDPMessage extends BaseMessage {

    private String type;

}
