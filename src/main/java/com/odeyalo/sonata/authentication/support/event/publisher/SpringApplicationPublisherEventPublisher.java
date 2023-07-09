package com.odeyalo.sonata.authentication.support.event.publisher;

import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationPublisherEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public SpringApplicationPublisherEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publishEvent(SonataEvent event) {
        eventPublisher.publishEvent(event);
    }
}
