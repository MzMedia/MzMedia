package com.mz.network.events;

import com.mz.network.client.message.BaseMessage;
import org.springframework.context.ApplicationEvent;

public class MessageEvent extends ApplicationEvent {
    private final BaseMessage message;

    public MessageEvent(Object source, BaseMessage message) {
        super(source);
        this.message = message;
    }

    public BaseMessage getMessage() {
        return message;
    }
}
