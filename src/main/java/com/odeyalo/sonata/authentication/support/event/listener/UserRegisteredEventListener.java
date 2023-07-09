package com.odeyalo.sonata.authentication.support.event.listener;

import com.odeyalo.sonata.authentication.support.event.handler.UserRegisteredEventHandler;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {
    private final UserRegisteredEventHandler handler;

    public UserRegisteredEventListener(UserRegisteredEventHandler handler) {
        this.handler = handler;
    }

    @EventListener
    public void onEvent(UserRegisteredEvent event) {
        handler.onEvent(event);
    }
}
