package com.mz.iot.handler;

import com.mz.network.events.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventSubscriber {
    @EventListener(MessageEvent.class)
    public void handleMessageEvent(MessageEvent event) {
        log.info("Received custom event with message: {}", event.getMessage());
    }
}
