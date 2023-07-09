package com.odeyalo.sonata.authentication.support.event.handler;

import com.odeyalo.sonata.suite.brokers.events.SonataEvent;

public interface EventHandler<T extends SonataEvent> {

    void onEvent(T body);

}
