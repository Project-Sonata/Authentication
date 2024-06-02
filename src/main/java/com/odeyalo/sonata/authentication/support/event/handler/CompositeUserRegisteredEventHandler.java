package com.odeyalo.sonata.authentication.support.event.handler;

import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;

import java.util.List;

public final class CompositeUserRegisteredEventHandler implements UserRegisteredEventHandler {
    private final List<UserRegisteredEventHandler> handlers;

    public CompositeUserRegisteredEventHandler(final List<UserRegisteredEventHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onEvent(final UserRegisteredEvent body) {
        handlers.forEach(it -> it.onEvent(body));
    }
}
