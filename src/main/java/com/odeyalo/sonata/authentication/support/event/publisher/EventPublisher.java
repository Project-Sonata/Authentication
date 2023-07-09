package com.odeyalo.sonata.authentication.support.event.publisher;

import com.odeyalo.sonata.suite.brokers.events.SonataEvent;

public interface EventPublisher {

    void publishEvent(SonataEvent event);

}
